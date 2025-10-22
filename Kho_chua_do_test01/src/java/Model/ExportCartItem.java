package Model;

/**
 * Represents an item in export cart (for branch manager requesting from warehouse)
 */
public class ExportCartItem {
    private int productDetailID;
    private String productName;
    private String productCode;
    private String description;
    private double costPrice;
    private double retailPrice;
    private int quantity;
    private int availableStock; // Available quantity in warehouse
    private String imageURL;

    public ExportCartItem() {
    }

    public ExportCartItem(int productDetailID, String productName, String productCode, 
                         String description, double costPrice, double retailPrice, 
                         int quantity, int availableStock, String imageURL) {
        this.productDetailID = productDetailID;
        this.productName = productName;
        this.productCode = productCode;
        this.description = description;
        this.costPrice = costPrice;
        this.retailPrice = retailPrice;
        this.quantity = quantity;
        this.availableStock = availableStock;
        this.imageURL = imageURL;
    }

    public int getProductDetailID() {
        return productDetailID;
    }

    public void setProductDetailID(int productDetailID) {
        this.productDetailID = productDetailID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public double getSubtotal() {
        return costPrice * quantity;
    }

    @Override
    public String toString() {
        return "ExportCartItem{" +
                "productDetailID=" + productDetailID +
                ", productName='" + productName + '\'' +
                ", productCode='" + productCode + '\'' +
                ", quantity=" + quantity +
                ", availableStock=" + availableStock +
                '}';
    }
}
