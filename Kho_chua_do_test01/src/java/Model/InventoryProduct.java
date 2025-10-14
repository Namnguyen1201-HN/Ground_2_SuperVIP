/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import java.util.Objects;

/**
 *
 * @author Kawaii
 */
public class InventoryProduct {

    private Integer inventoryId;     // InventoryID (FK -> Inventory)
    private Integer productDetailId; // ProductDetailID (FK -> ProductDetails)
    private Integer quantity;        // Quantity

    public InventoryProduct() {}

    public InventoryProduct(Integer inventoryId, Integer productDetailId, Integer quantity) {
        this.inventoryId = inventoryId;
        this.productDetailId = productDetailId;
        this.quantity = quantity;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getProductDetailId() {
        return productDetailId;
    }

    public void setProductDetailId(Integer productDetailId) {
        this.productDetailId = productDetailId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "InventoryProduct{" +
                "inventoryId=" + inventoryId +
                ", productDetailId=" + productDetailId +
                ", quantity=" + quantity +
                '}';
    }

    // equals/hashCode dựa trên khóa kép
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryProduct)) return false;
        InventoryProduct that = (InventoryProduct) o;
        return Objects.equals(inventoryId, that.inventoryId) &&
               Objects.equals(productDetailId, that.productDetailId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryId, productDetailId);
    }
}

