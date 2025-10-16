package Model;

public class StockMovementDetail {
    private int movementDetailId;
    private int movementId;
    private int productDetailId;
    private int quantity;
    private Integer quantityScanned;

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
}


