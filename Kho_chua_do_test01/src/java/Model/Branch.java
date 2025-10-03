package Model;

import java.util.Date;

public class Branch {
    private int branchId;
    private String branchName;
    private String location;
    private Date createdAt;

    public Branch() {}

    public Branch(int branchId, String branchName, String location, Date createdAt) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.location = location;
        this.createdAt = createdAt;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
