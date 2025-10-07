package Model;

public class ReceiptDetail {
    private int receiptDetailId;
    private int receiptId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double total;

    public int getReceiptDetailId() { return receiptDetailId; }
    public void setReceiptDetailId(int receiptDetailId) { this.receiptDetailId = receiptDetailId; }
    public int getReceiptId() { return receiptId; }
    public void setReceiptId(int receiptId) { this.receiptId = receiptId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}


