package Model;

import java.util.List;

public class StockMovementDetail {
    private int movementDetailId;
    private int movementId;
    private int productDetailId;
    private int quantity;
    private Integer quantityScanned;
    private int productId;
    private String productCode;
    private String productName;
    private int scanned;
    private List<ProductDetailSerialNumber> serials;

    public StockMovementDetail() {
    }

    public StockMovementDetail(int movementDetailId, int movementId, int productDetailId, int quantity, Integer quantityScanned, int productId, String productCode, String productName, int scanned, List<ProductDetailSerialNumber> serials) {
        this.movementDetailId = movementDetailId;
        this.movementId = movementId;
        this.productDetailId = productDetailId;
        this.quantity = quantity;
        this.quantityScanned = quantityScanned;
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.scanned = scanned;
        this.serials = serials;
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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    @Override
    public String toString() {
        return "StockMovementDetail{" + "movementDetailId=" + movementDetailId + ", movementId=" + movementId + ", productDetailId=" + productDetailId + ", quantity=" + quantity + ", quantityScanned=" + quantityScanned + ", productId=" + productId + ", productCode=" + productCode + ", productName=" + productName + ", scanned=" + scanned + '}';
    }
}


