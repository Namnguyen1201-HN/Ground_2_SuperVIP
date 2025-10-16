package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {

    private Integer productId;          // IDENTITY
    private String productName;         // NVARCHAR(255)
    private Integer brandId;            // FK -> Brands
    private Integer categoryId;         // FK -> Categories
    private Integer supplierId;         // FK -> Suppliers
    private BigDecimal costPrice;       // DECIMAL(18,2)
    private BigDecimal retailPrice;     // DECIMAL(18,2)
    private String imageUrl;            // NVARCHAR(MAX)
    private BigDecimal vat;             // DECIMAL(18,2)
    private LocalDateTime createdAt;    // DEFAULT GETDATE()
    private Boolean isActive;           // BIT
    private String brandName;
    private String categoryName;
    private String supplierName;

    public Product() {
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    // Model/Product.java
private Integer totalQty;        // tổng tồn (read-only từ truy vấn)
public Integer getTotalQty() { return totalQty; }
public void setTotalQty(Integer totalQty) { this.totalQty = totalQty; }

}
