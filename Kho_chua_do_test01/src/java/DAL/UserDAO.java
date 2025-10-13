package DAL;

import Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DataBaseContext {

    // ==============================
    // SQL QUERIES
    // ==============================
    private static final String SELECT_ALL_USERS
            = "SELECT u.UserID, u.FullName, u.Email, u.Phone, u.PasswordHash, "
            + "u.BranchID, u.WarehouseID, u.RoleID, u.IsActive, u.Gender, u.AvaUrl, "
            + "u.Address, u.TaxNumber, u.WebURL, u.DOB, u.IdentificationID, "
            + "r.RoleName, b.BranchName, w.WarehouseName "
            + "FROM Users u "
            + "LEFT JOIN Roles r ON u.RoleID = r.RoleID "
            + "LEFT JOIN Branches b ON u.BranchID = b.BranchID "
            + "LEFT JOIN Warehouses w ON u.WarehouseID = w.WarehouseID";

    private static final String GET_USER_BY_ID
            = SELECT_ALL_USERS + " WHERE u.UserID = ?";

    private static final String AUTHENTICATE_USER
            = "SELECT u.UserID, u.FullName, u.Email, u.Phone, u.PasswordHash, u.RoleID, "
            + "r.RoleName, u.IsActive, b.BranchName, w.WarehouseName "
            + "FROM Users u "
            + "LEFT JOIN Roles r ON u.RoleID = r.RoleID "
            + "LEFT JOIN Branches b ON u.BranchID = b.BranchID "
            + "LEFT JOIN Warehouses w ON u.WarehouseID = w.WarehouseID "
            + "WHERE u.Email = ? AND u.PasswordHash = ? AND u.IsActive = 1";

    private static final String INSERT_USER
            = "INSERT INTO Users (FullName, Email, Phone, PasswordHash, BranchID, WarehouseID, RoleID, IsActive, Gender, AvaUrl, Address, TaxNumber, WebURL, DOB, IdentificationID) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_USER_PASSWORD
            = "UPDATE Users SET PasswordHash = ? WHERE UserID = ?";

    private static final String CHECK_PHONE_EXISTS
            = "SELECT COUNT(*) FROM Users WHERE Phone = ?";

    private static final String GET_USER_BY_PHONE
            = SELECT_ALL_USERS + " WHERE u.Phone = ?";

    // ==============================
    // METHODS
    // ==============================
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL_USERS)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public User getUserById(int userId) {
        try (PreparedStatement ps = connection.prepareStatement(GET_USER_BY_ID)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User authenticate(String email, String password) {
        try (PreparedStatement ps = connection.prepareStatement(AUTHENTICATE_USER)) {
            ps.setString(1, email);
            ps.setString(2, password); // Có thể đổi sang mã hóa sau
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isPhoneExists(String phone) {
        try (PreparedStatement ps = connection.prepareStatement(CHECK_PHONE_EXISTS)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertUser(User u) {
        try (PreparedStatement ps = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPhone());
            ps.setString(4, u.getPasswordHash());
            if (u.getBranchId() != null) {
                ps.setInt(5, u.getBranchId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            if (u.getWarehouseId() != null) {
                ps.setInt(6, u.getWarehouseId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, u.getRoleId());
            ps.setBoolean(8, u.isActive());
            if (u.getGender() != null) {
                ps.setBoolean(9, u.getGender());
            } else {
                ps.setNull(9, Types.BIT);
            }
            ps.setString(10, u.getAvaUrl());
            ps.setString(11, u.getAddress());
            ps.setString(12, u.getTaxNumber());
            ps.setString(13, u.getWebUrl());
            if (u.getDob() != null) {
                ps.setTimestamp(14, new Timestamp(u.getDob().getTime()));
            } else {
                ps.setNull(14, Types.TIMESTAMP);
            }
            ps.setString(15, u.getIdentificationId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    u.setUserId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(int userId, String newPassword) {
        try (PreparedStatement ps = connection.prepareStatement(UPDATE_USER_PASSWORD)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserByPhone(String phone) {
        try (PreparedStatement ps = connection.prepareStatement(GET_USER_BY_PHONE)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(User u) {
        String sql = "UPDATE Users "
                + "SET FullName=?, Email=?, Phone=?, Address=?, Gender=?, DOB=?, "
                + "RoleID=?, WarehouseID=?, IsActive=? "
                + "WHERE UserID=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPhone());
            ps.setString(4, u.getAddress());

            if (u.getGender() != null) {
                ps.setBoolean(5, u.getGender());
            } else {
                ps.setNull(5, Types.BIT);
            }

            if (u.getDob() != null) {
                ps.setTimestamp(6, new Timestamp(u.getDob().getTime()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            ps.setInt(7, u.getRoleId());

            if (u.getWarehouseId() != null) {
                ps.setInt(8, u.getWarehouseId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.setBoolean(9, u.isActive());
            ps.setInt(10, u.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ UPDATE USER FAILED:");
            e.printStackTrace();
        }
        return false;
    }

    // ==============================
    // MAPPING FUNCTION
    // ==============================
    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("UserID"));
        u.setFullName(rs.getString("FullName"));
        u.setEmail(rs.getString("Email"));
        u.setPhone(rs.getString("Phone"));
        u.setPasswordHash(rs.getString("PasswordHash"));
        u.setRoleId(rs.getInt("RoleID"));
        u.setActive(rs.getBoolean("IsActive"));
        u.setAvaUrl(rs.getString("AvaUrl"));
        u.setAddress(rs.getString("Address"));
        u.setTaxNumber(rs.getString("TaxNumber"));
        u.setWebUrl(rs.getString("WebURL"));
        u.setIdentificationId(rs.getString("IdentificationID"));

        Timestamp dob = rs.getTimestamp("DOB");
        if (dob != null) {
            u.setDob(new java.util.Date(dob.getTime()));
        }

        // liên kết thông tin từ join
        u.setRoleName(rs.getString("RoleName"));
        u.setBranchName(rs.getString("BranchName"));
        u.setWarehouseName(rs.getString("WarehouseName"));

        // null-safe
        Object branch = rs.getObject("BranchID");
        if (branch != null) {
            u.setBranchId((Integer) branch);
        }

        Object warehouse = rs.getObject("WarehouseID");
        if (warehouse != null) {
            u.setWarehouseId((Integer) warehouse);
        }

        Object gender = rs.getObject("Gender");
        if (gender != null) {
            u.setGender((Boolean) gender);
        }

        return u;
    }

}
