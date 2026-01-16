package at.campus.backend.modules.studyprograms.model;

public class CampusBuildingDto {
    public String label;        // e.g. "A–D, P, V, W"
    public String address;      // e.g. "Favoritenstraße 226, 1100 Wien"
    public Double lat;
    public Double lon;

    public CampusBuildingDto() {}

    public CampusBuildingDto(String label, String address, Double lat, Double lon) {
        this.label = label;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }
}
