package Model;

import java.util.Date;

public class PasswordResetToken {
    private int tokenId;
    private int userId;
    private String token;
    private Date expiryDate;
    private boolean isUsed;
    private Date createdAt;
    
    public PasswordResetToken() {
    }
    
    public PasswordResetToken(int tokenId, int userId, String token, Date expiryDate, boolean isUsed, Date createdAt) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.token = token;
        this.expiryDate = expiryDate;
        this.isUsed = isUsed;
        this.createdAt = createdAt;
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isIsUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}