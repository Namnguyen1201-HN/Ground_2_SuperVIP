package Model;

import java.util.List;

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
    
    private int productId;
    private int scanned;
    private List<ProductDetailSerialNumber> serials;

    public StockMovementDetail(int movementDetailId, int movementId, int productDetailId, int quantity, Integer quantityScanned, String productName, String productCode, double costPrice, int productId, int scanned, List<ProductDetailSerialNumber> serials) {
        this.movementDetailId = movementDetailId;
        this.movementId = movementId;
        this.productDetailId = productDetailId;
        this.quantity = quantity;
        this.quantityScanned = quantityScanned;
        this.productName = productName;
        this.productCode = productCode;
        this.costPrice = costPrice;
        this.productId = productId;
        this.scanned = scanned;
        this.serials = serials;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getScanned() {
        return scanned;
    }

    public void setScanned(int scanned) {
        this.scanned = scanned;
    }

    public List<ProductDetailSerialNumber> getSerials() {
        return serials;
    }

    public void setSerials(List<ProductDetailSerialNumber> serials) {
        this.serials = serials;
    }
    
    public StockMovementDetail() {
    }

    public StockMovementDetail(int movementDetailId, int movementId, int productDetailId, int quantity, Integer quantityScanned, String productName, String productCode, double costPrice) {
        this.movementDetailId = movementDetailId;
        this.movementId = movementId;
        this.productDetailId = productDetailId;
        this.quantity = quantity;
        this.quantityScanned = quantityScanned;
        this.productName = productName;
        this.productCode = productCode;
        this.costPrice = costPrice;
    }
    

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


