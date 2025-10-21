package Model;

import java.util.Date;

/**
 * Model representing a stock movement request
 * Can be Import (from supplier to warehouse) or Export (from warehouse to branch)
 */
public class StockMovementRequest {
    private int movementID;
    private Integer fromSupplierID;
    private Integer fromBranchID;
    private Integer fromWarehouseID;
    private Integer toBranchID;
    private Integer toWarehouseID;
    private String movementType; // "Import" or "Export"
    private int createdBy;
    private Date createdAt;
    private String note;
    
    // Join fields for display
    private String fromSupplierName;
    private String fromBranchName;
    private String fromWarehouseName;
    private String toBranchName;
    private String toWarehouseName;
    private String creatorName;
    
    // Response information
    private String responseStatus; // From latest response
    private Date responseAt;
    private String responseNote;

    public StockMovementRequest() {
    }

    public StockMovementRequest(int movementID, Integer fromSupplierID, Integer fromBranchID, 
                                Integer fromWarehouseID, Integer toBranchID, Integer toWarehouseID, 
                                String movementType, int createdBy, Date createdAt, String note) {
        this.movementID = movementID;
        this.fromSupplierID = fromSupplierID;
        this.fromBranchID = fromBranchID;
        this.fromWarehouseID = fromWarehouseID;
        this.toBranchID = toBranchID;
        this.toWarehouseID = toWarehouseID;
        this.movementType = movementType;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.note = note;
    }

    // Getters and Setters
    public int getMovementID() {
        return movementID;
    }

    public void setMovementID(int movementID) {
        this.movementID = movementID;
    }

    public Integer getFromSupplierID() {
        return fromSupplierID;
    }

    public void setFromSupplierID(Integer fromSupplierID) {
        this.fromSupplierID = fromSupplierID;
    }

    public Integer getFromBranchID() {
        return fromBranchID;
    }

    public void setFromBranchID(Integer fromBranchID) {
        this.fromBranchID = fromBranchID;
    }

    public Integer getFromWarehouseID() {
        return fromWarehouseID;
    }

    public void setFromWarehouseID(Integer fromWarehouseID) {
        this.fromWarehouseID = fromWarehouseID;
    }

    public Integer getToBranchID() {
        return toBranchID;
    }

    public void setToBranchID(Integer toBranchID) {
        this.toBranchID = toBranchID;
    }

    public Integer getToWarehouseID() {
        return toWarehouseID;
    }

    public void setToWarehouseID(Integer toWarehouseID) {
        this.toWarehouseID = toWarehouseID;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFromSupplierName() {
        return fromSupplierName;
    }

    public void setFromSupplierName(String fromSupplierName) {
        this.fromSupplierName = fromSupplierName;
    }

    public String getFromBranchName() {
        return fromBranchName;
    }

    public void setFromBranchName(String fromBranchName) {
        this.fromBranchName = fromBranchName;
    }

    public String getFromWarehouseName() {
        return fromWarehouseName;
    }

    public void setFromWarehouseName(String fromWarehouseName) {
        this.fromWarehouseName = fromWarehouseName;
    }

    public String getToBranchName() {
        return toBranchName;
    }

    public void setToBranchName(String toBranchName) {
        this.toBranchName = toBranchName;
    }

    public String getToWarehouseName() {
        return toWarehouseName;
    }

    public void setToWarehouseName(String toWarehouseName) {
        this.toWarehouseName = toWarehouseName;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Date getResponseAt() {
        return responseAt;
    }

    public void setResponseAt(Date responseAt) {
        this.responseAt = responseAt;
    }

    public String getResponseNote() {
        return responseNote;
    }

    public void setResponseNote(String responseNote) {
        this.responseNote = responseNote;
    }

    @Override
    public String toString() {
        return "StockMovementRequest{" +
                "movementID=" + movementID +
                ", movementType='" + movementType + '\'' +
                ", fromSupplierID=" + fromSupplierID +
                ", fromWarehouseID=" + fromWarehouseID +
                ", toBranchID=" + toBranchID +
                ", toWarehouseID=" + toWarehouseID +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}
