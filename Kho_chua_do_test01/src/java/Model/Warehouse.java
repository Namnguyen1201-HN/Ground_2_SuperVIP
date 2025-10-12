package Model;

public class Warehouse {
    private Integer warehouseId;
    private String warehouseName;
    private String address;
    private String phone;
    private boolean isActive;

    public Warehouse() {
    }

    public Warehouse(Integer warehouseId, String warehouseName, String address, String phone, boolean isActive) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.address = address;
        this.phone = phone;
        this.isActive = isActive;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "warehouseId=" + warehouseId +
                ", warehouseName='" + warehouseName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
