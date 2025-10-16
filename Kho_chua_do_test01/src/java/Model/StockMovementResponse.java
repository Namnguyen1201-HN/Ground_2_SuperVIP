package Model;

import java.util.Date;

public class StockMovementResponse {
    private int responseId;
    private int movementId;
    private int responsedBy;
    private Date responseAt;
    private String responseStatus;
    private String note;

    public int getResponseId() { return responseId; }
    public void setResponseId(int responseId) { this.responseId = responseId; }
    public int getMovementId() { return movementId; }
    public void setMovementId(int movementId) { this.movementId = movementId; }
    public int getResponsedBy() { return responsedBy; }
    public void setResponsedBy(int responsedBy) { this.responsedBy = responsedBy; }
    public Date getResponseAt() { return responseAt; }
    public void setResponseAt(Date responseAt) { this.responseAt = responseAt; }
    public String getResponseStatus() { return responseStatus; }
    public void setResponseStatus(String responseStatus) { this.responseStatus = responseStatus; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}


