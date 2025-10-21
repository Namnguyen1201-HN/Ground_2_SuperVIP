package Model;

public class StockMovementDetail {
    private int movementDetailId;
    private int movementId;
    private int productDetailId;
    private int quantity;
    private Integer quantityScanned;
    
    // Display fields (joined from other tables)
    private String productName;
    private String productCode;
    private double costPrice;

    public int getMovementDetailId() { return movementDetailId; }
    public void setMovementDetailId(int movementDetailId) { this.movementDetailId = movementDetailId; }
    public int getMovementId() { return movementId; }
    public void setMovementId(int movementId) { this.movementId = movementId; }
    public int getProductDetailId() { return productDetailId; }
    public void setProductDetailId(int productDetailId) { this.productDetailId = productDetailId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Integer getQuantityScanned() { return quantityScanned; }
    public void setQuantityScanned(Integer quantityScanned) { this.quantityScanned = quantityScanned; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
}


