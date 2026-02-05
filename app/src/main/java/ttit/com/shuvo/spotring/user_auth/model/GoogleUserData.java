package ttit.com.shuvo.spotring.user_auth.model;

public class GoogleUserData {
    private String name;
    private String emailOrId;
    private String photoUrl;
    private String idToken;

    public GoogleUserData(String name, String emailOrId, String photoUrl, String idToken) {
        this.name = name != null ? name : "";
        this.emailOrId = emailOrId != null ? emailOrId : "";
        this.photoUrl = photoUrl != null ? photoUrl : "";
        this.idToken = idToken != null ? idToken : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailOrId() {
        return emailOrId;
    }

    public void setEmailOrId(String emailOrId) {
        this.emailOrId = emailOrId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
