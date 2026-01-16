package at.campus.backend.modules.studyprograms.service;

import at.campus.backend.modules.studyprograms.model.CampusBuildingDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StudyProgramCampusMapRegistry {

    // We return a list of buildings (markers) to support labels like "A–D", "E–F", etc.
    private record BuildingMarker(String label, String address, double lat, double lon) {}

    // --- HCW Favoriten locations (precise coordinates from OpenStreetMap) ---
    private static final BuildingMarker FAVORITEN_226 =
            new BuildingMarker("A–D, P, V, W", "Favoritenstraße 226, 1100 Wien", 48.1578869, 16.3818663);

    private static final BuildingMarker FAVORITEN_222 =
            new BuildingMarker("E, F", "Favoritenstraße 222, 1100 Wien", 48.1596670, 16.3826104);

    private static final BuildingMarker FAVORITEN_232 =
            new BuildingMarker("G, H, I", "Favoritenstraße 232, 1100 Wien", 48.1815186, 16.3752422);

    private static final List<BuildingMarker> FAVORITEN_ALL = List.of(
            FAVORITEN_226, FAVORITEN_222, FAVORITEN_232
    );

    private record Rule(List<String> keywords, List<BuildingMarker> buildings) {}

    // Rules ordered from most specific -> more general
    // Each program is mapped to its specific building where the program is actually carried out
    private static final List<Rule> RULES = List.of(
            // --- Gebäude E, F (Favoritenstraße 222) ---
            // Departments: Angewandte Pflegewissenschaft, Applied Life Sciences, Campus Wien Academy, Akademische Weiterbildung
            new Rule(List.of(
                    "nursing", "pflege", "angewandte", "applied",
                    "physiotherapie", "physiotherapy",
                    "hebammen", "midwifery",
                    "ergotherapie", "occupational therapy",
                    "logopaedie", "speech",
                    "diätologie", "diaetologie", "diet",
                    "academy", "weiterbildung"
            ), List.of(FAVORITEN_222)),

            // --- Gebäude G, H, I (Favoritenstraße 232) ---
            // Departments: Gesundheitswissenschaften (Bachelor Gesundheits- und Krankenpflege)
            new Rule(List.of(
                    "krankenpflege", "krankenpflege",
                    "bachelor", "bachelorstudium",
                    "wiener gesundheitsverbund"
            ), List.of(FAVORITEN_232)),

            // --- Gebäude A, B, C, D, P, V, W (Favoritenstraße 226) ---
            // Departments: Bauen und Gestalten, Gesundheitswissenschaften, Soziales, Technik, Verwaltung, Wirtschaft, Sicherheit, Politik
            new Rule(List.of(
                    "computer science", "digital communications", "csdc",
                    "it security", "software", "informatics", "informatik",
                    "technische informatik", "elektronik", "engineering",
                    "manufacturing", "biotech", "biotechnology", "bioengineering",
                    "architektur", "architecture",
                    "bauingenieurwesen", "civil engineering",
                    "green building", "gebäude", "gebaeude",
                    "bauen", "gestalten", "building", "design",
                    "soziale arbeit", "social work",
                    "sozial", "social",
                    "elementarpädagogik", "elementarpaedagogik",
                    "pädagogik", "paedagogik", "education",
                    "public management", "management",
                    "gesundheit", "health"
            ), List.of(FAVORITEN_226))
    );

    /**
     * Returns a list of buildings (map markers) for a given study program name.
     * If no rule matches, returns a minimal default set to keep the map useful.
     */
    public List<CampusBuildingDto> resolveBuildingsForProgramName(String programName) {
        if (programName == null || programName.isBlank()) {
            return defaultBuildings();
        }

        String name = programName.toLowerCase();

        for (Rule rule : RULES) {
            for (String kw : rule.keywords) {
                if (name.contains(kw)) {
                    return toDtos(rule.buildings);
                }
            }
        }

        // Fallback: show at least one relevant HCW building (FR-S-3 acceptance)
        return defaultBuildings();
    }

    private List<CampusBuildingDto> defaultBuildings() {
        // Minimal default: show the main building set or at least one marker
        return toDtos(List.of(FAVORITEN_226));
        // If you prefer always showing all three markers, use:
        // return toDtos(FAVORITEN_ALL);
    }

    private List<CampusBuildingDto> toDtos(List<BuildingMarker> markers) {
        List<CampusBuildingDto> list = new ArrayList<>();
        for (BuildingMarker m : markers) {
            list.add(new CampusBuildingDto(
                    m.label(),
                    m.address(),
                    m.lat(),
                    m.lon()
            ));
        }
        return list;
    }
}
