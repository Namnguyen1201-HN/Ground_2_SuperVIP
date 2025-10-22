package Model;

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
    
    // Display names (joined from other tables)
    private String fromSupplierName;
    private String fromBranchName;
    private String fromWarehouseName;
    private String toBranchName;
    private String toWarehouseName;
    private String creatorName;

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
}
