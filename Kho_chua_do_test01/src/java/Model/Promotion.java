package Model;

import java.math.BigDecimal;
import java.util.Date;

public class Promotion {
    private int promotionId;
    private String promoName;
    private BigDecimal discountPercent;
    private Date startDate;
    private Date endDate;
    private int productCount;
    private int branchCount;

    public Promotion() {
    }

    public Promotion(int promotionId, String promoName, BigDecimal discountPercent, Date startDate, Date endDate) {
        this.promotionId = promotionId;
        this.promoName = promoName;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.productCount = 0;
        this.branchCount = 0;
    }

    public Promotion(int promotionId, String promoName, BigDecimal discountPercent, Date startDate, Date endDate, int productCount, int branchCount) {
        this.promotionId = promotionId;
        this.promoName = promoName;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.productCount = productCount;
        this.branchCount = branchCount;
    }

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

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public int getBranchCount() {
        return branchCount;
    }

    public void setBranchCount(int branchCount) {
        this.branchCount = branchCount;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "promotionId=" + promotionId +
                ", promoName='" + promoName + '\'' +
                ", discountPercent=" + discountPercent +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", productCount=" + productCount +
                ", branchCount=" + branchCount +
                '}';
    }
}