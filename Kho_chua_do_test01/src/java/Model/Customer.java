/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.Date;

/**
 *
 * @author Admin
 */
public class Customer {
    private int customerID;
    private String fullname;
    private String phoneNumber;
    private String email;
    private String address;
    private Boolean gender;
    private Date dateOfBirth;
    private Date createdAt;
    private Date updatedAt;
    private Double totalSpent;
    private Integer branchId;

    public Customer() {
    }

    public Customer(int customerID, String fullname, String phoneNumber, String email, String address, Boolean gender, Date dateOfBirth, Date createdAt, Date updatedAt, Double totalSpent) {
        this.customerID = customerID;
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.totalSpent = totalSpent;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double setTotalSpent) {
        this.totalSpent = setTotalSpent;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    @Override
    public String toString() {
        return "Customer{" + "customerID=" + customerID + ", fullname=" + fullname + ", phoneNumber=" + phoneNumber + ", email=" + email + ", address=" + address + ", gender=" + gender + ", dateOfBirth=" + dateOfBirth + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", totalSpent=" + totalSpent + '}';
    }
}
