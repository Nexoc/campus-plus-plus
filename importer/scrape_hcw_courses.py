# Imports
import json
import re
import asyncio
from pathlib import Path
from typing import List, Dict, Any
from playwright.async_api import async_playwright
from bs4 import BeautifulSoup
from tqdm import tqdm

# Base website URL used to construct absolute links.
BASE_URL = "https://www.hcw.ac.at"

# URL of the page that lists all study programs (built from BASE_URL).
PROGRAMS_LIST_URL = f"{BASE_URL}/studium-weiterbildung/studienangebot"

# Computes the output directory: take this file's location, go up two folders, then add "data".
# Example: ...\campuswiki\scripts\scrape_hcw_courses.py -> ...\campuswiki\data
OUTPUT_DIR = Path(__file__).resolve().parent.parent / "data"

# Creates the output directory if it doesn't exist yet (including missing parent folders).
# exist_ok=True prevents an error if the folder already exists.
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
print(f"Output directory: {OUTPUT_DIR}")

# Controls whether the browser UI is shown:
# False = visible browser window (good for debugging), True = headless (runs in background).
HEADLESS = True

# Default timeout for Playwright actions/waits, in milliseconds (15 seconds).
TIMEOUT = 15000  # ms

_TITLE_NORMALIZE = str.maketrans({"ä": "a", "ö": "o", "ü": "u", "ß": "ss"})
COMMON_TITLE_KEYS = {
    "inhalt": "content",
    "lernergebnisse": "learning_outcomes",
    "lehrmethode": "teaching_method",
    "prufungsmethode": "exam_method",
    "pruefungsmethode": "exam_method",
    "literatur": "literature",
    "unterrichtssprache": "teaching_language",
}

PROGRAM_INFO_KEYS = {
    "abschluss": "degree",
    "degree": "degree",
    "abschlussgrad": "degree",
    "abschlussart": "degree",
    "abschlussbezeichnung": "degree",
    "abschluss": "degree",
    "semester": "semesters",
    "studiendauer": "semesters",
    "anzahl semester": "semesters",
    "organisationsform": "mode",
    "mode": "mode",
    "ects": "total_ects",
    "gesamt ects": "total_ects",
    "gesamtpunkte": "total_ects",
    "sprache": "language",
    "unterrichtssprache": "language",
    "sprachen": "language",
    "bewerbungsfrist": "application_period",
    "bewerbungsfristen": "application_period",
    "bewerbung": "application_period",
    "studienbeginn": "start_dates",
    "beginn": "start_dates",
}


def _slugify_label(label: str | None) -> str | None:
    if not label:
        return None
    # normalize umlauts and lowercase
    s = label.lower().translate(_TITLE_NORMALIZE)
    # replace non alphanumeric with underscores
    import re as _re
    s = _re.sub(r"[^a-z0-9]+", "_", s)
    s = _re.sub(r"_+", "_", s).strip("_")
    return s or None

# Remove common unicode artifacts (superscript digits, NBSP) and normalize whitespace.
def _clean_text(value: str | None) -> str | None:
    if value is None:
        return None
    s = str(value)
    s = s.replace("\xa0", " ")
    # strip superscript digits ¹²³ and U+2070..U+2079
    s = re.sub(r"[\u00B9\u00B2\u00B3\u2070-\u2079]", "", s)
    s = re.sub(r"\s+", " ", s).strip()
    return s or None

# Helper function to safely parse numeric strings with both comma and period separators.
def _to_number(s: str | None) -> float | None:
    # If input is None or empty, return None.
    if not s:
        return None

    # Strip whitespace from the input string.
    s = s.strip()

    # Extract the leading numeric part (handles cases like "75³" or "75³\nECTS")
    # Match optional digits, optional comma/period, optional more digits
    m = re.search(r"[\d,\.]+", s)
    if not m:
        return None

    s = m.group(0)

    # Replace comma (European format) with period (standard Python float format).
    s = s.replace(",", ".")

    try:
        # Attempt to parse the normalized string as a float.
        return float(s)
    except ValueError:
        # If parsing fails, return None.
        return None

def _semester_number(title: str | None) -> int | None:
    """Extract the first integer from a title like '1. Semester' -> 1."""
    if not title:
        return None
    m = re.search(r"(\d+)", title)
    return int(m.group(1)) if m else None

def _extract_int(value: str | None) -> int | None:
    """Extract the first integer from a string, handling superscripts and special characters.
    E.g., '75³' or '75³\\nECTS' -> 75."""
    if not value:
        return None
    value = str(value).strip()
    m = re.search(r"(\d+)", value)
    return int(m.group(1)) if m else None


def parse_detail_html(detail_html: str | None) -> Dict[str, Any] | None:
    """
    Parse detail HTML into a dict keyed by section title -> body list.
    Each body list contains blocks: {"type": "text", content: "..."} or {"type": "list", ordered, items}.
    Later sections with the same title overwrite earlier ones.
    """
    if not detail_html:
        return None

    soup = BeautifulSoup(detail_html, "html.parser")
    flat: Dict[str, Any] = {}
    current_title = None

    def append_body(block):
        if current_title is None:
            return
        flat.setdefault(current_title, []).append(block)

    def add_list(tag):
        ordered = tag.name == "ol"
        items = []
        for li in tag.find_all("li", recursive=True):
            txt = li.get_text(" ", strip=True)
            if txt:
                items.append(txt)
        if items:
            append_body({"type": "list", "ordered": ordered, "items": items})

    def add_paragraphs(tag):
        # Only direct paragraphs to avoid duping text from inside lists
        for p in tag.find_all("p", recursive=False):
            txt = p.get_text(" ", strip=True)
            if txt:
                append_body({"type": "text", "content": txt})

    for elem in soup.children:
        if isinstance(elem, str) and not elem.strip():
            continue

        if hasattr(elem, "name") and elem.name == "h4":
            current_title = elem.get_text(strip=True)
            flat.setdefault(current_title, [])
        elif current_title is not None and hasattr(elem, "name"):
            if elem.name in ("ul", "ol"):
                add_list(elem)
            elif elem.name == "div":
                # Prefer lists inside the div; then standalone paragraphs
                for lst in elem.find_all(["ul", "ol"], recursive=False):
                    add_list(lst)
                add_paragraphs(elem)
                # Fallback: if neither lists nor direct <p>, capture compact text
                if not flat.get(current_title):
                    txt = elem.get_text(" ", strip=True)
                    if txt:
                        append_body({"type": "text", "content": txt})
            elif elem.name == "p":
                txt = elem.get_text(" ", strip=True)
                if txt:
                    append_body({"type": "text", "content": txt})

    # Map only common section titles to normalized keys
    mapped: Dict[str, Any] = {}
    for t, body in flat.items():
        norm = _norm_title(t)
        key = COMMON_TITLE_KEYS.get(norm)
        if key:
            mapped[key] = body
    return mapped or None


def _norm_title(title: str | None) -> str | None:
    if not title:
        return None
    return title.lower().translate(_TITLE_NORMALIZE).strip()

# Function to parse curriculum from HTML (pre-loaded with all hidden elements already present).
def parse_curriculum_from_html(html: str) -> List[Dict[str, Any]]:
    # Parse the full page HTML using BeautifulSoup with lxml parser.
    soup = BeautifulSoup(html, "lxml")

    # Find the main curriculum accordion container.
    curriculum_root = soup.find(id="accordion-curriculum")

    # If no curriculum accordion found, return empty list.
    if not curriculum_root:
        return []

    # Initialize the result list to hold semester groupings.
    result: List[Dict[str, Any]] = []

    last_seen_module = None

    # Iterate over each direct child accordion-item (each represents one semester).
    for item in curriculum_root.find_all("div", class_="accordion-item", recursive=False):
        # Extract semester title from the accordion button.
        button = item.find("button", class_="accordion-button")
        semester_title = button.get_text(strip=True) if button else "Unknown Semester"
        semester_number = _semester_number(semester_title)

        # Find all course/module span elements in this semester (marked by infobutton-curriculum class).
        for info_span in item.find_all("span", class_="infobutton-curriculum"):
            # Initialize module for course items.
            parent_module = None

            # Determine course/module type from data-type attribute or label heuristic.
            data_type = (info_span.get("data-type") or "").lower()
            kind = "module" if "modul" in data_type else "course"

            # if it's a course set the last seen module as its module
            if kind == "course":
                parent_module = last_seen_module

            # Find the associated hidden header block (contains title, SWS, ECTS).
            header_block = info_span.find_next("div", class_="infotext-header-curriculum")

            # Initialize title and numeric fields to None.
            title = None
            sws = None
            ects = None

            if header_block:
                # Extract title from the first h3 in the header block.
                h3 = header_block.find("h3")
                if h3:
                    title = _clean_text(h3.get_text(strip=True))

                if kind == "module":
                    last_seen_module = title

                # Extract SWS (Semesterwochenstunden / contact hours).
                sws_elem = header_block.find("div", class_="sws")
                # SWS + ECTS share the same wrapper; take strong.val in order
                vals = sws_elem.select("strong.val")
                if len(vals) >= 1:
                    sws = _to_number(vals[0].get_text(strip=True))
                if len(vals) >= 2:
                    ects = _to_number(vals[1].get_text(strip=True))

            # Find the associated hidden detail block (contains full description).
            detail_block = info_span.find_next("div", class_="infotext-curriculum")
            detail_html = detail_block.decode_contents() if detail_block else None
            detail_fields = parse_detail_html(detail_html)

            # Create course/module item record with all extracted information.
            course_item = {
                "kind": kind,
                "title": _clean_text(title),
                "sws": sws,
                "ects": ects,
                "parent_module": parent_module,
                "semester": semester_number,
                "details_html": detail_html,
                "content": (detail_fields or {}).get("content"),
                "learning_outcomes": (detail_fields or {}).get("learning_outcomes"),
                "teaching_method": (detail_fields or {}).get("teaching_method"),
                "exam_method": (detail_fields or {}).get("exam_method"),
                "literature": (detail_fields or {}).get("literature"),
                "teaching_language": (detail_fields or {}).get("teaching_language"),
            }

            # Append to this semester's items list.
            result.append(course_item)

    # Return the semester-grouped curriculum structure.
    return result


# Define an async function that returns a list of program-detail URLs found on the programs list page.
async def get_program_urls(page) -> List[str]:
    # Navigate to the page that lists all programs; wait until the DOM is loaded (not necessarily all network idle).
    await page.goto(PROGRAMS_LIST_URL, wait_until="domcontentloaded")

    # Try to dismiss the cookie/privacy consent dialog (it may or may not be present).
    try:
        # Find the button that contains the visible text "ALLE AKZEPTIEREN" and click it.
        # timeout=5000 means "wait up to 5 seconds for the button to be clickable".
        await page.locator("button:has-text('ALLE AKZEPTIEREN')").click(timeout=5000)

        # Wait 0.5 seconds to give the UI time to close the dialog and unblock the page.
        await page.wait_for_timeout(500)

    # If the dialog never appears, the selector doesn't match, or the click fails, don't crash.
    except Exception:
        # Do nothing and continue scraping normally.
        pass

    # Select all <a> elements whose href contains the expected programs path segment.
    # Using a substring selector makes this more robust to small markup changes.
    anchors = await page.locator("section.courses a[href*='/studium-weiterbildung/studienangebot/']").all()

    # Prepare a list to collect candidate URLs (may contain duplicates).
    seen = set()
    urls = []

    # Iterate over all matching anchor elements.
    for a in anchors:
        # Read the href attribute value.
        href = await a.get_attribute("href")

        # Skip anchors without href.
        if not href:
            continue

        url = None
        if href.startswith("/studium-weiterbildung/studienangebot/"):
            url = BASE_URL + href
        # If href is already absolute and is under the list URL, keep it as-is.
        elif href.startswith(PROGRAMS_LIST_URL + "/"):
            url = href

        if url and url not in seen:
            seen.add(url)
            urls.append(url)

    print(f"Discovered {len(urls)} program URLs:\n{chr(10).join(urls)}")
    # Return the list of discovered program URLs.
    return urls

# ...existing code...

def _parse_program_info(info: List[Dict[str, Any]] | None) -> tuple[Dict[str, Any], Dict[str, Any]]:
    """Normalize program info list.

    Returns (flattened_known, flattened_all_normalized)
    - flattened_known: only mapped common keys
    - flattened_all_normalized: all entries with slug keys (duplicates aggregate into lists)
    """
    if not info:
        return {}, {}

    flattened_known: Dict[str, Any] = {}
    flattened_all: Dict[str, Any] = {}

    def _assign(d: Dict[str, Any], key: str, value: Any):
        if key in d:
            # if already a list, append; else convert to list
            if isinstance(d[key], list):
                d[key].append(value)
            else:
                d[key] = [d[key], value]
        else:
            d[key] = value

    for item in info:
        label = item.get("label")
        value = _clean_text(item.get("value"))
        norm = _norm_title(label)
        mapped = PROGRAM_INFO_KEYS.get(norm)

        # Map common known keys
        if mapped:
            if mapped == "semesters":
                extracted = _extract_int(value)
                flattened_known[mapped] = extracted if extracted is not None else value
            elif mapped == "total_ects":
                extracted = _extract_int(value)
                flattened_known[mapped] = extracted if extracted is not None else value
            else:
                flattened_known[mapped] = _clean_text(value)
        # Always add to normalized-all bucket
        slug = _slugify_label(label)
        if slug:
            _assign(flattened_all, slug, _clean_text(value))

    return flattened_known, flattened_all


def extract_program_title_and_info(html: str) -> tuple[str | None, Dict[str, Any], Dict[str, Any]]:
    """Extract the on-page program title (H2 in fixed header) and its DT/DD facts, flattened."""
    soup = BeautifulSoup(html, "lxml")

    title_el = (
        soup.select_one("div.headline h2")
    )
    title = _clean_text(title_el.get_text(" ", strip=True)) if title_el else None

    coursedetail = (
        soup.select_one("section.coursedetail")
    )

    info: List[Dict[str, Any]] = []
    if coursedetail:
        # Collect DT/DD pairs in order, across one or multiple <dl>
        for dl in coursedetail.find_all("dl"):
            dt = dl.find("dt")
            while dt:
                dd = dt.find_next_sibling("dd")
                if not dd:
                    break
                label = dt.get_text(" ", strip=True)

                # Prefer list items if present; otherwise preserve paragraph newlines
                li_items = [li.get_text(" ", strip=True) for li in dd.find_all("li")]
                if li_items:
                    value: Any = li_items
                else:
                    value = dd.get_text("\n", strip=True).replace("\r", "\n")

                info.append(
                    {
                        "label": label,
                        "value": value
                    }
                )

                # move to next DT after this DD
                dt = dd.find_next_sibling("dt")

    flattened, flattened_all = _parse_program_info(info)
    return title, flattened, flattened_all

# ...existing code...

async def scrape_program(page, program_url: str) -> Dict[str, Any]:
    await page.goto(program_url, wait_until="domcontentloaded")
    await page.wait_for_timeout(500)
    html_content = await page.content()

    # Pull title + DT/DD facts from the fixed header
    program_title, program_info_flat, program_info_all = extract_program_title_and_info(html_content)

    # Parse curriculum (grouped by semester, with module/course items)
    curriculum = parse_curriculum_from_html(html_content)

    record = {
        "program_url": program_url,
        "program_title": program_title,
        "degree": program_info_flat.get("degree"),
        "semesters": program_info_flat.get("semesters"),
        "mode": program_info_flat.get("mode"),
        "total_ects": program_info_flat.get("total_ects"),
        "language": program_info_flat.get("language"),
        "application_period": program_info_flat.get("application_period"),
        "start_dates": program_info_flat.get("start_dates"),
        "curriculum": curriculum,
    }

    # Add all normalized program info entries as top-level fields (slug keys), without overwriting existing keys
    for k, v in program_info_all.items():
        if k not in record:
            record[k] = v

    return record
# ...existing code...


# Define the async entry point that coordinates the browser session and writes output.
async def main():
    # Start Playwright and ensure it is cleaned up automatically when done.
    async with async_playwright() as playwright:
        # Launch a Chromium browser instance; headless mode controlled by the HEADLESS constant.
        browser = await playwright.chromium.launch(headless=HEADLESS)

        # Create an isolated browser context (fresh cookies/cache).
        context = await browser.new_context()

        # Open a new page/tab to do the navigation and scraping.
        page = await context.new_page()

        # Discover all program URLs from the programs listing page.
        program_urls = await get_program_urls(page)

        # If discovery fails (no URLs found), fall back to a single known URL for testing.
        if not program_urls:
            print("No program URLS found. Nothing to scrape.")
            return

        # Prepare list to hold the scrape results for each program.
        results = []

        # Loop over each program URL with a terminal progress bar.
        for url in tqdm(program_urls, desc="Programs"):
            try:
                # Scrape one program and append the resulting data.
                data = await scrape_program(page, url)
                results.append(data)
            except Exception as e:
                # If scraping fails, record the error alongside the URL instead of crashing.
                results.append({"program_url": url, "error": str(e)})

        # Build the output file path inside the configured output directory.
        out_path = OUTPUT_DIR / "hcw_courses.json"

        # Write the results as pretty-printed UTF-8 JSON (ensure_ascii=False keeps umlauts readable).
        out_path.write_text(
            json.dumps(results, ensure_ascii=False, indent=2),
            encoding="utf-8",
        )

        # Close the browser instance.
        await browser.close()


# Standard Python entry point guard: only run main() when executed as a script (not when imported).
if __name__ == "__main__":
    # Run the async main coroutine using asyncio's event loop runner.
    asyncio.run(main())
