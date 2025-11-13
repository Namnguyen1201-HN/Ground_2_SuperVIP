package Model;

import java.math.BigDecimal;
import java.util.Date;

public class Promotion {
    private int promotionId;
    private String promoName;
    private BigDecimal discountPercent;
    private Date startDate;
    private Date endDate;
    private int branchCount;
    private int productCount;
    
    // Computed field for status
    private String status; // "active", "scheduled", "expired"
    private long daysRemaining; // Số ngày còn lại (nếu chưa hết hạn)

    public Promotion() {}

    public Promotion(int promotionId, String promoName, BigDecimal discountPercent, 
                    Date startDate, Date endDate, int branchCount, int productCount) {
        this.promotionId = promotionId;
        this.promoName = promoName;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.branchCount = branchCount;
        this.productCount = productCount;
    }

    // Getters and Setters
    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getBranchCount() {
        return branchCount;
    }

    public void setBranchCount(int branchCount) {
        this.branchCount = branchCount;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }
}

