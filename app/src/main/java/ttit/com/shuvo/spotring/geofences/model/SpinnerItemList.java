package ttit.com.shuvo.spotring.geofences.model;

public class SpinnerItemList {
    private String id;
    private String name;
    private boolean clicked;
    private String listType;

    public SpinnerItemList(String id, String name, boolean clicked, String listType) {
        this.id = id;
        this.name = name;
        this.clicked = clicked;
        this.listType = listType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }
}
