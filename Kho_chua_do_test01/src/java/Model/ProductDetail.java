package Model;

import java.time.LocalDateTime;

public class ProductDetail {
    private Integer productDetailId;    // IDENTITY
    private Integer productId;          // FK -> Products
    private String description;         // NVARCHAR(MAX)
    private String productCode;         // NVARCHAR(255)
    private String warrantyPeriod;      // NVARCHAR(50)
    private String productNameUnsigned; // NVARCHAR(255)
    private LocalDateTime createdAt;    // DEFAULT GETDATE()
    private LocalDateTime updatedAt;    // NULL

    public ProductDetail() {}

    public Integer getProductDetailId() { return productDetailId; }
    public void setProductDetailId(Integer productDetailId) { this.productDetailId = productDetailId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getWarrantyPeriod() { return warrantyPeriod; }
    public void setWarrantyPeriod(String warrantyPeriod) { this.warrantyPeriod = warrantyPeriod; }

    public String getProductNameUnsigned() { return productNameUnsigned; }
    public void setProductNameUnsigned(String productNameUnsigned) { this.productNameUnsigned = productNameUnsigned; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
