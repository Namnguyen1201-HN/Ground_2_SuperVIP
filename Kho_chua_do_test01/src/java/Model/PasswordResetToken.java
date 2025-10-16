package Model;

import java.util.Date;

public class PasswordResetToken {
    private int id;
    private int userId;
    private String token;
    private Date expiryDate;

    public PasswordResetToken() {}

    public PasswordResetToken(int id, int userId, String token, Date expiryDate) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", token='" + token + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }
}

