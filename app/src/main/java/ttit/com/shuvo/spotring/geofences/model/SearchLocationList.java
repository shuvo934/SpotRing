package ttit.com.shuvo.spotring.geofences.model;

public class SearchLocationList {
    private String lat;
    private String lng;
    private String name;
    private String display_name;

    public SearchLocationList(String lat, String lng, String name, String display_name) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.display_name = display_name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }
}
