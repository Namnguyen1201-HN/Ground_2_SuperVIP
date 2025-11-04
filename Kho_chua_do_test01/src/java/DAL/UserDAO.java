package DAL;

import Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DataBaseContext {

    /**
     * Authenticate user with email/phone and password
     */
    public User authenticateUser(String username, String hashedPassword) {
        User user = null;

        String query = "SELECT u.*, r.RoleName, b.BranchName, w.WarehouseName "
                + "FROM Users u "
                + "LEFT JOIN Roles r ON u.RoleID = r.RoleID "
                + "LEFT JOIN Branches b ON u.BranchID = b.BranchID "
                + "LEFT JOIN Warehouses w ON u.WarehouseID = w.WarehouseID "
                + "WHERE (u.Email = ? OR u.Phone = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username.trim());
            stmt.setString(2, username.trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("PasswordHash");
                if (storedHash != null && storedHash.trim().equalsIgnoreCase(hashedPassword.trim())) {
                    user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPhone(rs.getString("Phone"));
                    user.setBranchId((Integer) rs.getObject("BranchID"));
                    user.setWarehouseId((Integer) rs.getObject("WarehouseID"));
                    user.setRoleId(rs.getInt("RoleID"));
                    user.setRoleName(rs.getString("RoleName"));
                    user.setIsActive(rs.getInt("IsActive"));
                    user.setGender((Boolean) rs.getObject("Gender"));
                    user.setAvaUrl(rs.getString("AvaUrl"));
                    user.setAddress(rs.getString("Address"));
                    user.setBranchName(rs.getString("BranchName"));
                    user.setWarehouseName(rs.getString("WarehouseName"));
                } else {
                    System.out.println("⚠️ HASH NOT MATCH");
                    System.out.println("DB: " + storedHash);
                    System.out.println("INPUT: " + hashedPassword);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    /**
     * Get user by ID with full information
     */
    public User getUserById(int userID) {
        User user = null;
        String query = "SELECT u.UserID, u.FullName, u.Email, u.Phone, u.PasswordHash, "
                + "u.BranchID, u.WarehouseID, u.RoleID, u.IsActive, u.Gender, u.AvaUrl, u.Address, u.DOB, "
                + "u.IdentificationID, u.TaxNumber, u.WebURL, "
                + // ✅ THÊM CÁC CỘT NÀY
                "r.RoleName, b.BranchName, w.WarehouseName "
                + "FROM Users u "
                + "INNER JOIN Roles r ON u.RoleID = r.RoleID "
                + "LEFT JOIN Branches b ON u.BranchID = b.BranchID "
                + "LEFT JOIN Warehouses w ON u.WarehouseID = w.WarehouseID "
                + "WHERE u.UserID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPhone(rs.getString("Phone"));
                    user.setPasswordHash(rs.getString("PasswordHash"));

                    // ✅ Dùng getObject để giữ đúng NULL thay vì 0
                    user.setBranchId((Integer) rs.getObject("BranchID"));
                    user.setWarehouseId((Integer) rs.getObject("WarehouseID"));

                    user.setRoleId(rs.getInt("RoleID"));
                    user.setRoleName(rs.getString("RoleName"));
                    user.setIsActive(rs.getInt("IsActive"));
                    user.setAvaUrl(rs.getString("AvaUrl"));
                    user.setAddress(rs.getString("Address"));
                    user.setBranchName(rs.getString("BranchName"));
                    user.setWarehouseName(rs.getString("WarehouseName"));

                    // ✅ Giới tính null-safe
                    Object genderObj = rs.getObject("Gender");
                    user.setGender(genderObj != null ? rs.getBoolean("Gender") : null);

                    // ✅ DOB (giữ nguyên độ chính xác)
                    java.sql.Timestamp dob = rs.getTimestamp("DOB");
                    if (dob != null) {
                        user.setDob(dob); // kiểu Timestamp kế thừa java.util.Date
                    }

                    // ✅ QUAN TRỌNG: Lấy CCCD/Hộ chiếu
                    user.setIdentificationId(rs.getString("IdentificationID"));

                    // (Tùy bạn có dùng)
                    user.setTaxNumber(rs.getString("TaxNumber"));
                    user.setWebUrl(rs.getString("WebURL"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String query = "SELECT u.UserID, u.FullName, u.Email, u.Phone, u.PasswordHash, "
                + "u.BranchID, u.WarehouseID, u.RoleID, u.IsActive, u.Gender, u.AvaUrl, u.Address, "
                + "r.RoleName, b.BranchName, w.WarehouseName "
                + "FROM Users u "
                + "LEFT JOIN Roles r ON u.RoleID = r.RoleID "
                + "LEFT JOIN Branches b ON u.BranchID = b.BranchID "
                + "LEFT JOIN Warehouses w ON u.WarehouseID = w.WarehouseID";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPhone(rs.getString("Phone"));
                user.setBranchId(rs.getInt("BranchID"));
                user.setWarehouseId(rs.getInt("WarehouseID"));
                user.setRoleId(rs.getInt("RoleID"));
                user.setRoleName(rs.getString("RoleName"));
                user.setIsActive(rs.getInt("IsActive"));
                user.setAvaUrl(rs.getString("AvaUrl"));
                user.setAddress(rs.getString("Address"));
                user.setBranchName(rs.getString("BranchName"));
                user.setWarehouseName(rs.getString("WarehouseName"));
                list.add(user);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    /**
     * Check if phone exists
     */
    public boolean isPhoneExists(String phone) {
        String query = "SELECT COUNT(*) FROM Users WHERE Phone = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExists(String email) {
        // Thay đổi [Users] và [Email] cho phù hợp với CSDL của bạn
        String sql = "SELECT COUNT(*) FROM [Users] WHERE LOWER([Email]) = LOWER(?)";

        try (
                PreparedStatement ps = connection.prepareStatement(sql)) { // Tự động đóng statement

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) { // Tự động đóng result set
                if (rs.next()) {
                    // rs.getInt(1) sẽ lấy giá trị của cột đầu tiên (COUNT(*))
                    // Nếu count > 0, nghĩa là email đã được tìm thấy.
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            // In lỗi ra console để debug, trong ứng dụng thực tế nên dùng logger
            e.printStackTrace();
        }
        // Trả về false nếu có lỗi xảy ra hoặc không tìm thấy
        return false;
    }

    public boolean isIdentificationIdExists(String identificationId) {
        // Câu lệnh SQL để đếm số bản ghi có IdentificationID trùng khớp
        String sql = "SELECT COUNT(*) FROM [Users] WHERE [IdentificationID] = ?";

        // Sử dụng try-with-resources để tự động đóng kết nối
        try (
                PreparedStatement ps = connection.prepareStatement(sql)) {

            // Gán giá trị cho tham số trong câu lệnh SQL
            ps.setString(1, identificationId);

            // Thực thi truy vấn và lấy kết quả
            try (ResultSet rs = ps.executeQuery()) {
                // Di chuyển con trỏ đến hàng đầu tiên
                if (rs.next()) {
                    // Lấy giá trị đếm từ cột đầu tiên. Nếu lớn hơn 0, tức là ID đã tồn tại.
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            // In lỗi ra console để debug
            e.printStackTrace();
        }

        // Trả về false nếu có lỗi hoặc không tìm thấy
        return false;
    }

    /**
     * Check if email exists (case insensitive)
     */
//    public boolean isEmailExists(String email) {
//        String query = "SELECT COUNT(*) FROM Users WHERE LOWER(Email) = LOWER(?)";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setString(1, email.trim());
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                return rs.getInt(1) > 0;
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//        return false;
//    }

    /**
     * Check if identification ID exists
     */
//    public boolean isIdentificationIdExists(String identificationId) {
//        String query = "SELECT COUNT(*) FROM Users WHERE IdentificationID = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setString(1, identificationId.trim());
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                return rs.getInt(1) > 0;
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//        return false;
//    }

    /**
     * Insert new user
     */
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean insertUser(User u) {
        String query = "INSERT INTO Users (FullName, Email, Phone, PasswordHash, BranchID, WarehouseID, "
                + "RoleID, IsActive, Gender, AvaUrl, Address, TaxNumber, WebURL, DOB, IdentificationID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, u.getFullName());
            stmt.setString(2, u.getEmail());
            stmt.setString(3, u.getPhone());
            stmt.setString(4, hashPassword(u.getPasswordHash()));

            if (u.getBranchId() != null) {
                stmt.setInt(5, u.getBranchId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            if (u.getWarehouseId() != null) {
                stmt.setInt(6, u.getWarehouseId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setInt(7, u.getRoleId());
            stmt.setInt(8, u.getIsActive());

            if (u.getGender() != null) {
                stmt.setBoolean(9, u.getGender());
            } else {
                stmt.setNull(9, Types.BIT);
            }

            stmt.setString(10, u.getAvaUrl());
            stmt.setString(11, u.getAddress());
            stmt.setString(12, u.getTaxNumber());
            stmt.setString(13, u.getWebUrl());

            if (u.getDob() != null) {
                stmt.setTimestamp(14, new Timestamp(u.getDob().getTime()));
            } else {
                stmt.setNull(14, Types.TIMESTAMP);
            }

            stmt.setString(15, u.getIdentificationId());

            int rows = stmt.executeUpdate();
            stmt.close();
            return rows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Update user
     */
    public boolean updateUser(User u) {
        String query = "UPDATE Users SET FullName=?, Email=?, Phone=?, Address=?, Gender=?, DOB=?, "
                + "IdentificationID=?, TaxNumber=?, WebURL=?, IsActive=?, "
                + "RoleID=?, BranchID=?, WarehouseID=? "
                + "WHERE UserID=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, u.getFullName());
            stmt.setString(2, u.getEmail());
            stmt.setString(3, u.getPhone());
            stmt.setString(4, u.getAddress());

            if (u.getGender() != null) {
                stmt.setBoolean(5, u.getGender());
            } else {
                stmt.setNull(5, Types.BIT);
            }

            if (u.getDob() != null) {
                stmt.setTimestamp(6, new Timestamp(u.getDob().getTime()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }

            stmt.setString(7, u.getIdentificationId());
            stmt.setString(8, u.getTaxNumber());
            stmt.setString(9, u.getWebUrl());
            stmt.setInt(10, u.getIsActive());

            stmt.setInt(11, u.getRoleId());
            if (u.getBranchId() != null) {
                stmt.setInt(12, u.getBranchId());
            } else {
                stmt.setNull(12, Types.INTEGER);
            }
            if (u.getWarehouseId() != null) {
                stmt.setInt(13, u.getWarehouseId());
            } else {
                stmt.setNull(13, Types.INTEGER);
            }

            stmt.setInt(14, u.getUserId());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Update password
     */
    public boolean updatePassword(int userID, String newPassword) {
        String query = "UPDATE Users SET PasswordHash = ? WHERE UserID = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, hashPassword(newPassword));
            stmt.setInt(2, userID);

            int rows = stmt.executeUpdate();
            stmt.close();
            return rows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Delete user
     */
    public boolean deleteUser(int userID) {
        String query = "DELETE FROM Users WHERE UserID = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userID);
            int rows = stmt.executeUpdate();
            stmt.close();
            return rows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Lấy ShiftID hiện tại của nhân viên
    public Integer getShiftIdByUserId(int userId) {
        String sql = "SELECT ShiftID FROM UserShift WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ShiftID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

// Cập nhật ca làm của nhân viên (nếu đã có thì UPDATE, chưa có thì INSERT)
    public boolean updateUserShift(int userId, int shiftId) {
        String checkSql = "SELECT COUNT(*) FROM UserShift WHERE UserID = ?";
        String insertSql = "INSERT INTO UserShift (UserID, ShiftID) VALUES (?, ?)";
        String updateSql = "UPDATE UserShift SET ShiftID = ? WHERE UserID = ?";

        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            check.setInt(1, userId);
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                    ps.setInt(1, shiftId);
                    ps.setInt(2, userId);
                    return ps.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                    ps.setInt(1, userId);
                    ps.setInt(2, shiftId);
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserFullById(int userID) {
        User user = null;

        // JOIN để lấy tên kho (và chỉ lấy tên để tránh trùng cột)
        String sql
                = "SELECT u.UserID, u.FullName, u.Email, u.Phone, u.Address, u.PasswordHash, "
                + "       u.BranchID, u.WarehouseID, u.RoleID, u.IsActive, u.Gender, "
                + "       u.DOB, u.IdentificationID, u.TaxNumber, u.WebURL, "
                + "       w.WarehouseName AS WarehouseName "
                + // 👈 thêm tên kho
                "FROM Users u "
                + "LEFT JOIN Warehouses w ON u.WarehouseID = w.WarehouseID "
                + "WHERE u.UserID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();

                    // Các cột từ Users
                    user.setUserId(rs.getInt("UserID"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPhone(rs.getString("Phone"));
                    user.setAddress(rs.getString("Address"));
                    user.setPasswordHash(rs.getString("PasswordHash"));

                    // Các ID có thể null
                    user.setBranchId((Integer) rs.getObject("BranchID"));
                    user.setWarehouseId((Integer) rs.getObject("WarehouseID"));

                    user.setRoleId(rs.getInt("RoleID"));
                    user.setIsActive(rs.getInt("IsActive"));

                    // Gender (BIT) có thể null
                    Object genderObj = rs.getObject("Gender");
                    user.setGender(genderObj == null ? null : (rs.getBoolean("Gender")));

                    // DOB có thể null
                    java.sql.Timestamp dobTs = rs.getTimestamp("DOB");
                    user.setDob(dobTs == null ? null : new java.util.Date(dobTs.getTime()));

                    user.setIdentificationId(rs.getString("IdentificationID"));
                    user.setTaxNumber(rs.getString("TaxNumber"));
                    user.setWebUrl(rs.getString("WebURL"));

                    // Tên kho từ JOIN
                    user.setWarehouseName(rs.getString("WarehouseName")); // 👈 quan trọng
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

// Xóa ca làm của nhân viên
    public boolean deleteUserShift(int userId) {
        String sql = "DELETE FROM UserShift WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String hashSHA256(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get user by email for password reset
     */
    public User getUserByEmail(String email) {
        User user = null;
        String query = "SELECT UserID, FullName, Email, Phone, PasswordHash, BranchID, WarehouseID, " +
                      "RoleID, IsActive, Gender, AvaUrl, Address FROM Users WHERE Email = ? AND IsActive = 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email.trim());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPhone(rs.getString("Phone"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setBranchId((Integer) rs.getObject("BranchID"));
                user.setWarehouseId((Integer) rs.getObject("WarehouseID"));
                user.setRoleId(rs.getInt("RoleID"));
                user.setIsActive(rs.getInt("IsActive"));
                user.setGender((Boolean) rs.getObject("Gender"));
                user.setAvaUrl(rs.getString("AvaUrl"));
                user.setAddress(rs.getString("Address"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    
    /**
     * Create password reset token
     */
    public boolean createPasswordResetToken(int userId, String token) {
        // First, delete any existing tokens for this user
        String deleteQuery = "DELETE FROM PasswordResetTokens WHERE UserID = ?";
        
        // Then insert the new token with 24 hour expiry
        String insertQuery = "INSERT INTO PasswordResetTokens (UserID, Token, ExpiryDate) VALUES (?, ?, DATEADD(HOUR, 24, GETDATE()))";
        
        try {
            // Delete existing tokens
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, userId);
                deleteStmt.executeUpdate();
            }
            
            // Insert new token
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, userId);
                insertStmt.setString(2, token);
                int rows = insertStmt.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check if reset token is valid (exists and not expired)
     */
    public boolean isValidResetToken(String token) {
        String query = "SELECT COUNT(*) FROM PasswordResetTokens WHERE Token = ? AND ExpiryDate > GETDATE()";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Reset password using token
     */
    public boolean resetPassword(String token, String newPassword) {
        // First verify token is valid and get user ID
        String getUserQuery = "SELECT UserID FROM PasswordResetTokens WHERE Token = ? AND ExpiryDate > GETDATE()";
        String updatePasswordQuery = "UPDATE Users SET PasswordHash = ? WHERE UserID = ?";
        String deleteTokenQuery = "DELETE FROM PasswordResetTokens WHERE Token = ?";
        
        try {
            int userId = -1;
            
            // Get user ID from valid token
            try (PreparedStatement getUserStmt = connection.prepareStatement(getUserQuery)) {
                getUserStmt.setString(1, token);
                ResultSet rs = getUserStmt.executeQuery();
                
                if (rs.next()) {
                    userId = rs.getInt("UserID");
                } else {
                    return false; // Token invalid or expired
                }
            }
            
            // Update password
            try (PreparedStatement updateStmt = connection.prepareStatement(updatePasswordQuery)) {
                updateStmt.setString(1, hashPassword(newPassword));
                updateStmt.setInt(2, userId);
                int rows = updateStmt.executeUpdate();
                
                if (rows > 0) {
                    // Delete the used token
                    try (PreparedStatement deleteStmt = connection.prepareStatement(deleteTokenQuery)) {
                        deleteStmt.setString(1, token);
                        deleteStmt.executeUpdate();
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        UserDAO ud = new UserDAO();
        System.out.println(hashSHA256("12345678A"));
    }
}
