package Model;

/**
 * Model class representing an item in the import request cart
 * Used for temporary storage in session before creating actual import request
 */
public class ImportCartItem {
    private int productDetailID;
    private String productCode;
    private String productNameUnsigned;
    private int quantity;
    private double costPrice;
    private String formattedCostPrice;
    
    public ImportCartItem() {
    }
    
    public ImportCartItem(int productDetailID, String productCode, String productNameUnsigned, 
                         int quantity, double costPrice) {
        this.productDetailID = productDetailID;
        this.productCode = productCode;
        this.productNameUnsigned = productNameUnsigned;
        this.quantity = quantity;
        this.costPrice = costPrice;
        this.formattedCostPrice = String.format("%,.0f", costPrice);
    }

    public int getProductDetailID() {
        return productDetailID;
    }

    public void setProductDetailID(int productDetailID) {
        this.productDetailID = productDetailID;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductNameUnsigned() {
        return productNameUnsigned;
    }

    public void setProductNameUnsigned(String productNameUnsigned) {
        this.productNameUnsigned = productNameUnsigned;
    }
    
    // Alias for JSP compatibility
    public String getProductName() {
        return productNameUnsigned;
    }
    
    public void setProductName(String productName) {
        this.productNameUnsigned = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
        this.formattedCostPrice = String.format("%,.0f", costPrice);
    }

    public String getFormattedCostPrice() {
        return formattedCostPrice;
    }

    public void setFormattedCostPrice(String formattedCostPrice) {
        this.formattedCostPrice = formattedCostPrice;
    }

    public double getTotalAmount() {
        return costPrice * quantity;
    }
}
