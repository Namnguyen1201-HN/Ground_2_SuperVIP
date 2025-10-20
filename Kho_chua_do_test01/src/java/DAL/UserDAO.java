package DAL;

import Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DataBaseContext {

    /**
     * Authenticate user with email/phone and password
     */
    public User authenticateUser(String username, String password) {
        User user = null;
        String hashedPassword = hashPassword(password);

        String query = "SELECT u.UserID, u.FullName, u.Email, u.Phone, u.PasswordHash, "
                + "u.BranchID, u.WarehouseID, u.RoleID, u.IsActive, u.Gender, u.AvaUrl, u.Address, "
                + "r.RoleName, b.BranchName, w.WarehouseName "
                + "FROM Users u "
                + "INNER JOIN Roles r ON u.RoleID = r.RoleID "
                + "LEFT JOIN Branches b ON u.BranchID = b.BranchID "
                + "LEFT JOIN Warehouses w ON u.WarehouseID = w.WarehouseID "
                + "WHERE (u.Email = ? OR u.Phone = ?) AND u.IsActive = 1";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("PasswordHash");

                // Verify password
                if (verifyPassword(password, storedPassword)) {
                    user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPhone(rs.getString("Phone"));
                    user.setBranchId(rs.getInt("BranchID"));
                    user.setWarehouseId(rs.getInt("WarehouseID"));
                    user.setRoleId(rs.getInt("RoleID"));
                    user.setRoleName(rs.getString("RoleName"));
                    user.setIsActive(rs.getInt("IsActive"));
                    user.setGender(rs.getBoolean("Gender"));
                    user.setAvaUrl(rs.getString("AvaUrl"));
                    user.setAddress(rs.getString("Address"));
                    user.setBranchName(rs.getString("BranchName"));
                    user.setWarehouseName(rs.getString("WarehouseName"));
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return user;
    }

    /**
     * Verify password
     */
    private boolean verifyPassword(String password, String storedPassword) {
        // Compare with hashed password
        return hashPassword(password).equals(storedPassword);
    }

    /**
     * Hash password using SHA-256
     */
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return password;
        }
    }

    /**
     * Get user by ID with full information
     */
    public User getUserById(int userID) {
        User user = null;
        String query = "SELECT u.UserID, u.FullName, u.Email, u.Phone, u.PasswordHash, "
                + "u.BranchID, u.WarehouseID, u.RoleID, u.IsActive, u.Gender, u.AvaUrl, u.Address, "
                + "r.RoleName, b.BranchName, w.WarehouseName "
                + "FROM Users u "
                + "INNER JOIN Roles r ON u.RoleID = r.RoleID "
                + "LEFT JOIN Branches b ON u.BranchID = b.BranchID "
                + "LEFT JOIN Warehouses w ON u.WarehouseID = w.WarehouseID "
                + "WHERE u.UserID = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User();
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
            }
            rs.close();
            stmt.close();
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

    /**
     * Insert new user
     */
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

}
