package ttit.com.shuvo.spotring.dashboard.model;

import java.util.ArrayList;

import ttit.com.shuvo.spotring.geofences.model.CustomRepetitionDataList;

public class SavedLocationList {
    private String geo_id;
    private String lat;
    private String lng;
    private String radius;
    private String event_name;
    private String event_address;
    private String notes;
    private String alert_type_id;
    private String alert_type;
    private String event_type_id;
    private String event_type_name;
    private String alert_when_id;
    private String alert_when;
    private String repeat_type_id;
    private String repeat_type;
    private Boolean isActive;
    private ArrayList<CustomRepetitionDataList> customRepetitionDataLists;
    private boolean updated;

    public SavedLocationList(String geo_id, String lat, String lng, String radius, String event_name, String event_address,
                             String notes, String alert_type_id, String alert_type, String event_type_id,
                             String event_type_name, String alert_when_id, String alert_when,
                             String repeat_type_id, String repeat_type,
                             Boolean isActive, ArrayList<CustomRepetitionDataList> customRepetitionDataLists, boolean updated) {
        this.geo_id = geo_id;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        this.event_name = event_name;
        this.event_address = event_address;
        this.notes = notes;
        this.alert_type_id = alert_type_id;
        this.alert_type = alert_type;
        this.event_type_id = event_type_id;
        this.event_type_name = event_type_name;
        this.alert_when_id = alert_when_id;
        this.alert_when = alert_when;
        this.repeat_type_id = repeat_type_id;
        this.repeat_type = repeat_type;
        this.isActive = isActive;
        this.customRepetitionDataLists = customRepetitionDataLists;
        this.updated = updated;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public String getGeo_id() {
        return geo_id;
    }

    public void setGeo_id(String geo_id) {
        this.geo_id = geo_id;
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

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_address() {
        return event_address;
    }

    public void setEvent_address(String event_address) {
        this.event_address = event_address;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAlert_type_id() {
        return alert_type_id;
    }

    public void setAlert_type_id(String alert_type_id) {
        this.alert_type_id = alert_type_id;
    }

    public String getAlert_type() {
        return alert_type;
    }

    public void setAlert_type(String alert_type) {
        this.alert_type = alert_type;
    }

    public String getEvent_type_id() {
        return event_type_id;
    }

    public void setEvent_type_id(String event_type_id) {
        this.event_type_id = event_type_id;
    }

    public String getEvent_type_name() {
        return event_type_name;
    }

    public void setEvent_type_name(String event_type_name) {
        this.event_type_name = event_type_name;
    }

    public String getAlert_when_id() {
        return alert_when_id;
    }

    public void setAlert_when_id(String alert_when_id) {
        this.alert_when_id = alert_when_id;
    }

    public String getAlert_when() {
        return alert_when;
    }

    public void setAlert_when(String alert_when) {
        this.alert_when = alert_when;
    }

    public String getRepeat_type_id() {
        return repeat_type_id;
    }

    public void setRepeat_type_id(String repeat_type_id) {
        this.repeat_type_id = repeat_type_id;
    }

    public String getRepeat_type() {
        return repeat_type;
    }

    public void setRepeat_type(String repeat_type) {
        this.repeat_type = repeat_type;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public ArrayList<CustomRepetitionDataList> getCustomRepetitionDataLists() {
        return customRepetitionDataLists;
    }

    public void setCustomRepetitionDataLists(ArrayList<CustomRepetitionDataList> customRepetitionDataLists) {
        this.customRepetitionDataLists = customRepetitionDataLists;
    }
}
