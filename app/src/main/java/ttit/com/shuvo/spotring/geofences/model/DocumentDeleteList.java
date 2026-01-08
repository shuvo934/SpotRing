package ttit.com.shuvo.spotring.geofences.model;

public class DocumentDeleteList {
    private String documentId;
    private boolean isDeleted;

    public DocumentDeleteList(String documentId, boolean isDeleted) {
        this.documentId = documentId;
        this.isDeleted = isDeleted;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
