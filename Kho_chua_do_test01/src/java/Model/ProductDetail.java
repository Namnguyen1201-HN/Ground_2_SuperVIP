package Model;

import java.util.Date;

/**
 * Model class representing a Product Detail (variant of a product)
 */
public class ProductDetail {
    private int productDetailID;
    private int productID;
    private String description;
    private String productCode;
    private String warrantyPeriod;
    private String productNameUnsigned;
    private Date createdAt;
    private double costPrice;
    private double retailPrice;
    
    // Join fields from Products table
    private String productName;
    private String imageURL;
    private String supplierName;
    private int quantityInStock; // From InventoryProducts
    
    public ProductDetail() {
    }

    public ProductDetail(int productDetailID, int productID, String description, String productCode,
                        String warrantyPeriod, String productNameUnsigned, Date createdAt) {
        this.productDetailID = productDetailID;
        this.productID = productID;
        this.description = description;
        this.productCode = productCode;
        this.warrantyPeriod = warrantyPeriod;
        this.productNameUnsigned = productNameUnsigned;
        this.createdAt = createdAt;
    }

    public int getProductDetailID() {
        return productDetailID;
    }

    public void setProductDetailID(int productDetailID) {
        this.productDetailID = productDetailID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getWarrantyPeriod() {
        return warrantyPeriod;
    }

    public void setWarrantyPeriod(String warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }

    public String getProductNameUnsigned() {
        return productNameUnsigned;
    }

    public void setProductNameUnsigned(String productNameUnsigned) {
        this.productNameUnsigned = productNameUnsigned;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }
}
