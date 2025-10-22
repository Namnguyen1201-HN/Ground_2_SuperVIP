package Model;

public class OrderDetail {
    private int orderDetailId;
    private int orderId;
    private int productDetailId;
    private int quantity;

    public OrderDetail() {
    }

    public OrderDetail(int orderDetailId, int orderId, int productDetailId, int quantity) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.productDetailId = productDetailId;
        this.quantity = quantity;
    }

    public int getOrderDetailId() { return orderDetailId; }
    public void setOrderDetailId(int orderDetailId) { this.orderDetailId = orderDetailId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductDetailId() { return productDetailId; }
    public void setProductDetailId(int productDetailId) { this.productDetailId = productDetailId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}


