package Model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for Revenue Report (from Orders table)
 */
public class RevenueReportDTO {
    private int orderId;
    private String customerName;
    private String paymentMethod;
    private Date createdAt;
    private String branchName;
    private String createdByName; // User who created the order
    private BigDecimal grandTotal;
    private Integer branchId;
    
    public RevenueReportDTO() {
    }
    
    public RevenueReportDTO(int orderId, String customerName, String paymentMethod, 
                          Date createdAt, String branchName, String createdByName, 
                          BigDecimal grandTotal) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.branchName = branchName;
        this.createdByName = createdByName;
        this.grandTotal = grandTotal;
    }
    
    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
    
    public String getCreatedByName() {
        return createdByName;
    }
    
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
    
    public BigDecimal getGrandTotal() {
        return grandTotal;
    }
    
    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }
    
    public Integer getBranchId() {
        return branchId;
    }
    
    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }
}

