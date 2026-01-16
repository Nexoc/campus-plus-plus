import type { Course, RichBlock } from '../model/Course'

type TextBlock = {
  type: 'text'
  content: string
}

export function parseDetailsHtml(detailsHtml?: string): Partial<Course> {
  if (!detailsHtml || !detailsHtml.trim()) {
    return {}
  }

  const doc = new DOMParser().parseFromString(detailsHtml, 'text/html')

  const getH4 = (title: string): HTMLElement | undefined => {
    return Array.from(doc.querySelectorAll('h4')).find(
      h => h.textContent?.trim() === title
    )
  }

  const getText = (title: string): TextBlock[] => {
    const h4 = getH4(title)
    const text = h4?.nextElementSibling?.textContent?.trim()
    if (!text) return []
    return [{ type: 'text', content: text }]
  }

  const getList = (title: string): TextBlock[] => {
    const h4 = getH4(title)
    if (!h4) return []

    const items = h4.nextElementSibling?.querySelectorAll('li') ?? []
    return Array.from(items).map(li => ({
      type: 'text',
      content: li.textContent?.trim() ?? '',
    }))
  }

  const getParagraphs = (title: string): TextBlock[] => {
    const h4 = getH4(title)
    if (!h4) return []

    const result: TextBlock[] = []
    let el = h4.nextElementSibling

    while (el && el.tagName === 'P') {
      const text = el.textContent?.trim()
      if (text) {
        result.push({ type: 'text', content: text })
      }
      el = el.nextElementSibling
    }

    return result
  }

  return {
    content: getText('Inhalt'),
    learningOutcomes: getList('Lernergebnisse'),
    teachingMethod: getText('Lehrmethode'),
    examMethod: getText('Prüfungsmethode'),
    literature: getParagraphs('Literatur'),
    teachingLanguage: getText('Unterrichtssprache'),
  }
}

export function buildDetailsHtml(course: Course): string {
  const text = (blocks?: RichBlock[]) =>
    blocks?.map(b => (b.type === 'text' ? b.content : '')).join('') ?? ''

  const list = (blocks?: RichBlock[]) =>
    blocks?.map(b => `<li>${b.type === 'text' ? b.content : ''}</li>`).join('') ?? ''

  const paragraphs = (blocks?: RichBlock[]) =>
    blocks?.map(b => `<p>${b.type === 'text' ? b.content : ''}</p>`).join('') ?? ''

  return `
<h4>Inhalt</h4>
<p>${text(course.content)}</p>

<h4>Lernergebnisse</h4>
<ul>
${list(course.learningOutcomes)}
</ul>

<h4>Lehrmethode</h4>
<p>${text(course.teachingMethod)}</p>

<h4>Prüfungsmethode</h4>
<p>${text(course.examMethod)}</p>

<h4>Literatur</h4>
${paragraphs(course.literature)}

<h4>Unterrichtssprache</h4>
<p>${text(course.teachingLanguage)}</p>
`.trim()
}
