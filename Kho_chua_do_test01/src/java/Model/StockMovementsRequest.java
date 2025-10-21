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
