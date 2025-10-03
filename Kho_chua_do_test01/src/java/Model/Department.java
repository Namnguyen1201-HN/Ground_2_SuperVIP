package Model;

import java.util.Date;

public class Department {
    private int departmentId;
    private String departmentName;
    private int branchId;   // foreign key -> CompanyBranches
    private Date createdAt;

    public Department() {}

    public Department(int departmentId, String departmentName, int branchId, Date createdAt) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.branchId = branchId;
        this.createdAt = createdAt;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
