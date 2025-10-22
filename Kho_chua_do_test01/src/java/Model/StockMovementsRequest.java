package Model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class StockMovementsRequest {

    private int movementId;
    private Integer fromSupplierId;
    private Integer fromBranchId;
    private Integer fromWarehouseId;
    private Integer toBranchId;
    private Integer toWarehouseId;
    private String movementType; // Import/Export
    private int createdBy;
    private Date createdAt;
    private String note;
    private List<StockMovementDetail> details;
    private String fromSupplierName;
    private String createdByName;
    private BigDecimal totalAmount;
    private String responseStatus;
    private String fromBranchName;
    private String formattedDate;
    private String formattedTotalAmount;
    private String fromWarehouseName;
    private String toBranchName;
    private String toWarehouseName;

    public StockMovementsRequest() {
    }

    public StockMovementsRequest(int movementId, Integer fromSupplierId, Integer fromBranchId, Integer fromWarehouseId, Integer toBranchId, Integer toWarehouseId, String movementType, int createdBy, Date createdAt, String note, List<StockMovementDetail> details, String fromSupplierName, String createdByName, BigDecimal totalAmount, String responseStatus, String fromBranchName, String formattedDate, String formattedTotalAmount, String fromWarehouseName, String toBranchName, String toWarehouseName) {
        this.movementId = movementId;
        this.fromSupplierId = fromSupplierId;
        this.fromBranchId = fromBranchId;
        this.fromWarehouseId = fromWarehouseId;
        this.toBranchId = toBranchId;
        this.toWarehouseId = toWarehouseId;
        this.movementType = movementType;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.note = note;
        this.details = details;
        this.fromSupplierName = fromSupplierName;
        this.createdByName = createdByName;
        this.totalAmount = totalAmount;
        this.responseStatus = responseStatus;
        this.fromBranchName = fromBranchName;
        this.formattedDate = formattedDate;
        this.formattedTotalAmount = formattedTotalAmount;
        this.fromWarehouseName = fromWarehouseName;
        this.toBranchName = toBranchName;
        this.toWarehouseName = toWarehouseName;
    }

    
    
    public String getFromSupplierName() {
        return fromSupplierName;
    }

    public void setFromSupplierName(String fromSupplierName) {
        this.fromSupplierName = fromSupplierName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getFromBranchName() {
        return fromBranchName;
    }

    public void setFromBranchName(String fromBranchName) {
        this.fromBranchName = fromBranchName;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getFormattedTotalAmount() {
        return formattedTotalAmount;
    }

    public void setFormattedTotalAmount(String formattedTotalAmount) {
        this.formattedTotalAmount = formattedTotalAmount;
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
    
    

    public int getMovementId() {
        return movementId;
    }

    public void setMovementId(int movementId) {
        this.movementId = movementId;
    }

    public Integer getFromSupplierId() {
        return fromSupplierId;
    }

    public void setFromSupplierId(Integer fromSupplierId) {
        this.fromSupplierId = fromSupplierId;
    }

    public Integer getFromBranchId() {
        return fromBranchId;
    }

    public void setFromBranchId(Integer fromBranchId) {
        this.fromBranchId = fromBranchId;
    }

    public Integer getFromWarehouseId() {
        return fromWarehouseId;
    }

    public void setFromWarehouseId(Integer fromWarehouseId) {
        this.fromWarehouseId = fromWarehouseId;
    }

    public Integer getToBranchId() {
        return toBranchId;
    }

    public void setToBranchId(Integer toBranchId) {
        this.toBranchId = toBranchId;
    }

    public Integer getToWarehouseId() {
        return toWarehouseId;
    }

    public void setToWarehouseId(Integer toWarehouseId) {
        this.toWarehouseId = toWarehouseId;
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

    public List<StockMovementDetail> getDetails() {
        return details;
    }

    public void setDetails(List<StockMovementDetail> details) {
        this.details = details;
    }
}
