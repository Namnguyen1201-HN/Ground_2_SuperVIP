package Model;

import java.util.Date;

/**
 * Enhanced User model that maps to the Users table in the database
 * with complete fields based on the database schema
 */
public class User1 {
    // Database fields
    private int userId;
    private String fullName;
    private String username;
    private String passwordHash;
    private String email;
    private String phone;
    private int roleId;
    private boolean isActive;
    private Date createdAt;
    
    // Additional fields for relationship data
    private String roleName; // For joining with Roles table
    
    // Default constructor
    public User1() {
    }
    
    // Constructor with all fields
    public User1(int userId, String fullName, String username, String passwordHash, 
                String email, String phone, int roleId, boolean isActive, Date createdAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
        this.roleId = roleId;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }
    
    // Constructor for new user creation (without userId and createdAt which are auto-generated)
    public User1(String fullName, String username, String passwordHash, 
                String email, String phone, int roleId, boolean isActive) {
        this.fullName = fullName;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
        this.roleId = roleId;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
    
    public int getRoleId() {
        return roleId;
    }
    
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    /**
     * Utility method to check if the user is an administrator
     * @return true if the user has admin role
     */
    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(this.roleName);
    }
    
    /**
     * Utility method to check if user has a specific role
     * @param roleName The role name to check
     * @return true if the user has the specified role
     */
    public boolean hasRole(String roleName) {
        return roleName.equalsIgnoreCase(this.roleName);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}