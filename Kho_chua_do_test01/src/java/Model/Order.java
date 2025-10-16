package Model;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    private int orderId;
    private Integer branchId;
    private int createdBy;
    private String orderStatus;
    private Date createdAt;
    private Integer customerId;
    private String paymentMethod;
    private String notes;
    private BigDecimal grandTotal;
    private BigDecimal customerPay;
    private BigDecimal changeAmount;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public Integer getBranchId() { return branchId; }
    public void setBranchId(Integer branchId) { this.branchId = branchId; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

    public BigDecimal getCustomerPay() { return customerPay; }
    public void setCustomerPay(BigDecimal customerPay) { this.customerPay = customerPay; }

    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }
}


