package DAL;

import Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Model.User1;

public class UserDAO extends DataBaseContext {

    private static final String SELECT_ALL_USERS
            = "SELECT u.UserId, u.FullName, u.Username, u.Email, u.Phone, "
            + "u.IdentifierCode, u.IsActive, u.CreatedAt, "
            + "r.RoleName, d.DepartmentName, b.BranchName "
            + "FROM Users u "
            + "LEFT JOIN Departments d ON u.DepartmentId = d.DepartmentId "
            + "LEFT JOIN CompanyBranches b ON d.BranchId = b.BranchId "
            + "JOIN Roles r ON u.RoleId = r.RoleId";

    private static final String AUTHENTICATE_USER
            = "SELECT u.UserId, u.FullName, u.Username, u.Email, u.Phone, r.RoleName, u.IsActive, u.CreatedAt "
            + "FROM Users u "
            + "JOIN Roles r ON u.RoleId = r.RoleId "
            + "WHERE u.Username = ? AND u.PasswordHash = ? AND u.IsActive = 1";

    private static final String CHECK_PHONE_EXISTS
            = "SELECT COUNT(*) FROM Users WHERE Phone = ?";

    private static final String INSERT_USER
            = "INSERT INTO Users (FullName, Username, Phone, Email, PasswordHash, RoleId, IsActive, CreatedAt) "
            + "VALUES (?, ?, ?, ?, ?, (SELECT RoleId FROM Roles WHERE RoleName = 'Salesperson'), ?, ?)";

    private static final String GET_USER_BY_PHONE
            = "SELECT u.UserId, u.FullName, u.Username, u.Email, u.Phone, r.RoleName, u.IsActive, u.CreatedAt "
            + "FROM Users u "
            + "JOIN Roles r ON u.RoleId = r.RoleId "
            + "WHERE u.Phone = ?";

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL_USERS)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserId"));
                user.setFullName(rs.getString("FullName"));
                user.setUsername(rs.getString("Username"));
                user.setEmail(rs.getString("Email"));
                user.setPhone(rs.getString("Phone"));
                user.setIdentifierCode(rs.getString("IdentifierCode"));
                user.setRoleName(rs.getString("RoleName"));
                user.setDepartmentName(rs.getString("DepartmentName"));
                user.setBranchName(rs.getString("BranchName"));
                user.setActive(rs.getBoolean("IsActive"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User authenticateUser(String username, String password) {
        try (PreparedStatement ps = connection.prepareStatement(AUTHENTICATE_USER)) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password)); // Trong thực tế nên mã hóa mật khẩu

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserId"));
                user.setFullName(rs.getString("FullName"));
                user.setUsername(rs.getString("Username"));
                user.setEmail(rs.getString("Email"));
                user.setPhone(rs.getString("Phone"));
                user.setRoleName(rs.getString("RoleName"));
                user.setActive(rs.getBoolean("IsActive"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                return user;
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

    public boolean createUser(User user) {
        try (PreparedStatement ps = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getEmail());
            ps.setString(5, generateDefaultPassword()); // Tạo mật khẩu mặc định
            ps.setBoolean(6, user.isActive());
            ps.setTimestamp(7, new Timestamp(user.getCreatedAt().getTime()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
                return true;
            }
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
                User user = new User();
                user.setUserId(rs.getInt("UserId"));
                user.setFullName(rs.getString("FullName"));
                user.setUsername(rs.getString("Username"));
                user.setEmail(rs.getString("Email"));
                user.setPhone(rs.getString("Phone"));
                user.setRoleName(rs.getString("RoleName"));
                user.setActive(rs.getBoolean("IsActive"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        String sql = "UPDATE Users SET PasswordHash = ? WHERE UserId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hashPassword(newPassword));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserEmail(int userId, String email) {
        String sql = "UPDATE Users SET Email = ? WHERE UserId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper methods
    private String hashPassword(String password) {
        // Trong thực tế nên sử dụng BCrypt hoặc các thuật toán mã hóa mạnh
        // Đây chỉ là demo đơn giản
        return password; // Tạm thời không mã hóa
    }

    private String generateDefaultPassword() {
        // Tạo mật khẩu mặc định (trong thực tế nên tạo random và gửi qua SMS/Email)
        return "WM" + System.currentTimeMillis() % 10000;
    }

    public List<User> searchUsersByName(String name) {
        List<User> users = new ArrayList<>();
        String sql = SELECT_ALL_USERS + " WHERE u.FullName LIKE ? OR u.Username LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchPattern = "%" + name + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserId"));
                user.setFullName(rs.getString("FullName"));
                user.setUsername(rs.getString("Username"));
                user.setEmail(rs.getString("Email"));
                user.setPhone(rs.getString("Phone"));
                user.setRoleName(rs.getString("RoleName"));
                user.setActive(rs.getBoolean("IsActive"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                user.setBranchName(rs.getString("BranchName"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<User> getActiveUsers() {
        List<User> users = new ArrayList<>();
        String sql = SELECT_ALL_USERS + " WHERE u.IsActive = 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserId"));
                user.setFullName(rs.getString("FullName"));
                user.setUsername(rs.getString("Username"));
                user.setEmail(rs.getString("Email"));
                user.setPhone(rs.getString("Phone"));
                user.setRoleName(rs.getString("RoleName"));
                user.setActive(rs.getBoolean("IsActive"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                user.setBranchName(rs.getString("BranchName"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // =====================
    // Forgot password flow
    // =====================
    public User getUserByEmail(String email) {
        String sql = "SELECT UserId, FullName, Username, Email, Phone, IsActive, CreatedAt FROM Users WHERE Email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserId"));
                user.setFullName(rs.getString("FullName"));
                user.setUsername(rs.getString("Username"));
                user.setEmail(rs.getString("Email"));
                user.setPhone(rs.getString("Phone"));
                user.setActive(rs.getBoolean("IsActive"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User1 getUser1ByEmail(String email) {
        String sql = "SELECT u.UserId, u.FullName, u.Username, u.PasswordHash, u.Email, u.Phone, u.RoleId, u.IsActive, u.CreatedAt, r.RoleName "
                + "FROM Users u JOIN Roles r ON u.RoleId = r.RoleId WHERE u.Email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User1 user = new User1();
                user.setUserId(rs.getInt("UserId"));
                user.setFullName(rs.getString("FullName"));
                user.setUsername(rs.getString("Username"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setEmail(rs.getString("Email"));
                user.setPhone(rs.getString("Phone"));
                user.setRoleId(rs.getInt("RoleId"));
                user.setIsActive(rs.getBoolean("IsActive"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                user.setRoleName(rs.getString("RoleName"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createPasswordResetToken(int userId, String token) {
        String sql = "INSERT INTO PasswordResetTokens (UserId, Token, ExpiryDate, IsUsed, CreatedAt) VALUES (?, ?, ?, 0, ?)";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiry = new Timestamp(System.currentTimeMillis() + 24L * 60L * 60L * 1000L); // 24 hours
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.setTimestamp(3, expiry);
            ps.setTimestamp(4, now);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isValidResetToken(String token) {
        String sql = "SELECT COUNT(*) FROM PasswordResetTokens WHERE Token = ? AND IsUsed = 0 AND ExpiryDate > GETDATE()";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean resetPassword(String token, String newPassword) throws SQLException {
        String selectSql = "SELECT TOP 1 UserId FROM PasswordResetTokens WHERE Token = ? AND IsUsed = 0 AND ExpiryDate > GETDATE()";
        String updatePasswordSql = "UPDATE Users SET PasswordHash = ? WHERE UserId = ?";
        String consumeTokenSql = "UPDATE PasswordResetTokens SET IsUsed = 1 WHERE Token = ?";

        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            Integer userId = null;
            try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                ps.setString(1, token);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt("UserId");
                } else {
                    connection.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(updatePasswordSql)) {
                ps.setString(1, hashPassword(newPassword));
                ps.setInt(2, userId);
                if (ps.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(consumeTokenSql)) {
                ps.setString(1, token);
                if (ps.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }
    
    public boolean insertUser(User u) {
        String sql = "INSERT INTO Users (FullName, Username, Email, Phone, IdentifierCode, DepartmentId, RoleId, IsActive, CreatedAt, PasswordHash) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getIdentifierCode());
            ps.setInt(6, u.getDepartmentId());
            ps.setInt(7, u.getRoleId());
            ps.setBoolean(8, u.isActive());
            ps.setTimestamp(9, new java.sql.Timestamp(u.getCreatedAt().getTime()));
            ps.setString(10, "123456"); // mật khẩu mặc định

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
}
