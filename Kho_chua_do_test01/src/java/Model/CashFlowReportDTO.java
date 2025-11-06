package Model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for Cash Flow Report
 */
public class CashFlowReportDTO {
    private int cashFlowId;
    private String flowType; // revenue or expense
    private BigDecimal amount;
    private String category;
    private String paymentMethod;
    private Date createdAt;
    private String branchName;
    private String createdBy; // User name or ID
    private Integer branchId;
    
    public CashFlowReportDTO() {
    }
    
    public CashFlowReportDTO(int cashFlowId, String flowType, BigDecimal amount, 
                            String category, String paymentMethod, Date createdAt,
                            String branchName, String createdBy) {
        this.cashFlowId = cashFlowId;
        this.flowType = flowType;
        this.amount = amount;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.branchName = branchName;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public int getCashFlowId() {
        return cashFlowId;
    }
    
    public void setCashFlowId(int cashFlowId) {
        this.cashFlowId = cashFlowId;
    }
    
    public String getFlowType() {
        return flowType;
    }
    
    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getBranchName() {
        return branchName;
    }
    
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public Integer getBranchId() {
        return branchId;
    }
    
    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }
}


