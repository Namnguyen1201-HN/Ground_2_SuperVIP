/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.Date;

/**
 *
 * @author admin
 */
public class Supplier {
    private int supplierId;
    private String supplierName;
    private String contactName;
    private String email;
    private String phone;
    private Date createdAt;
    private Date updatedAt;

    public Supplier() {
    }

    public Supplier(int supplierId, String supplierName, String contactName, String email, String phone, Date createdAt, Date updatedAt) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.contactName = contactName;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Supplier{" + "supplierId=" + supplierId + ", supplierName=" + supplierName + ", contactName=" + contactName + ", email=" + email + ", phone=" + phone + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
    }
}
