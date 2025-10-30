package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO mở rộng từ Product để mang thêm thông tin trình bày/tổng hợp.
 * - Giữ nguyên toàn bộ field/behavior của Product
 * - Bổ sung các field chi tiết & khuyến mãi (nếu có)
 */
public class ProductDTO extends Product {

    /* ====== FIELDS BỔ SUNG ====== */
    private Integer productDetailId;     // ProductDetails.ProductDetailID
    private Integer inventoryQuantity;   // tồn theo chi nhánh/kho cụ thể (nếu có)
    private String  description;         // mô tả biến thể/chi tiết
    private String  productCode;         // mã/serial
    private String  warrantyPeriod;      // thời hạn bảo hành (text)

    // Thông tin khuyến mãi (tuỳ có)
    private String        promoName;
    private BigDecimal    discountPercent;       // % giảm (0-100)
    private LocalDateTime promoStartDate;
    private LocalDateTime promoEndDate;

    // Tiện cho UI: trạng thái dạng text (suy ra từ isActive)
    private String statusText;

    /* ====== CONSTRUCTORS ====== */
    public ProductDTO() {
        super();
    }

    /** Convenience: khởi tạo từ một Product có sẵn (copy toàn bộ field cha). */
    public ProductDTO(Product base) {
        if (base != null) {
            this.setProductId(base.getProductId());
            this.setProductName(base.getProductName());
            this.setBrandId(base.getBrandId());
            this.setCategoryId(base.getCategoryId());
            this.setSupplierId(base.getSupplierId());
            this.setCostPrice(base.getCostPrice());
            this.setRetailPrice(base.getRetailPrice());
            this.setImageUrl(base.getImageUrl());
            this.setVat(base.getVat());
            this.setCreatedAt(base.getCreatedAt());
            this.setIsActive(base.getIsActive());
            this.setBrandName(base.getBrandName());
            this.setCategoryName(base.getCategoryName());
            this.setSupplierName(base.getSupplierName());
            this.setTotalQty(base.getTotalQty());
        }
        // cập nhật statusText theo isActive
        this.statusText = (getIsActive() != null && getIsActive())
                ? "Đang kinh doanh" : "Không kinh doanh";
    }

    /** All-args cho phần mở rộng (các field của Product giữ nguyên qua setter). */
    public ProductDTO(Integer productDetailId,
                      Integer inventoryQuantity,
                      String description,
                      String productCode,
                      String warrantyPeriod,
                      String promoName,
                      BigDecimal discountPercent,
                      LocalDateTime promoStartDate,
                      LocalDateTime promoEndDate) {
        super();
        this.productDetailId   = productDetailId;
        this.inventoryQuantity = inventoryQuantity;
        this.description       = description;
        this.productCode       = productCode;
        this.warrantyPeriod    = warrantyPeriod;
        this.promoName         = promoName;
        this.discountPercent   = discountPercent;
        this.promoStartDate    = promoStartDate;
        this.promoEndDate      = promoEndDate;
    }

    /* ====== GETTERS / SETTERS ====== */
    public Integer getProductDetailId() {
        return productDetailId;
    }

    public void setProductDetailId(Integer productDetailId) {
        this.productDetailId = productDetailId;
    }

    public Integer getInventoryQuantity() {
        return inventoryQuantity;
    }

    public void setInventoryQuantity(Integer inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
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

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public LocalDateTime getPromoStartDate() {
        return promoStartDate;
    }

    public void setPromoStartDate(LocalDateTime promoStartDate) {
        this.promoStartDate = promoStartDate;
    }

    public LocalDateTime getPromoEndDate() {
        return promoEndDate;
    }

    public void setPromoEndDate(LocalDateTime promoEndDate) {
        this.promoEndDate = promoEndDate;
    }

    public String getStatusText() {
        // nếu chưa set thủ công thì suy ra theo isActive
        if (statusText == null) {
            Boolean active = getIsActive();
            statusText = (active != null && active) ? "Đang kinh doanh" : "Không kinh doanh";
        }
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    /* ====== Helper tiện dụng ====== */

    /** Cập nhật statusText theo isActive hiện tại. */
    public void refreshStatusText() {
        this.statusText = (getIsActive() != null && getIsActive())
                ? "Đang kinh doanh" : "Không kinh doanh";
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "productDetailId=" + productDetailId +
                ", inventoryQuantity=" + inventoryQuantity +
                ", description='" + description + '\'' +
                ", productCode='" + productCode + '\'' +
                ", warrantyPeriod='" + warrantyPeriod + '\'' +
                ", promoName='" + promoName + '\'' +
                ", discountPercent=" + discountPercent +
                ", promoStartDate=" + promoStartDate +
                ", promoEndDate=" + promoEndDate +
                ", statusText='" + getStatusText() + '\'' +
                "} " + super.toString();
    }
}
