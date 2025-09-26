package DAL;

import Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DataBaseContext {

    private static final String SELECT_ALL_USERS
            = "SELECT u.UserId, u.FullName, u.Username, u.Email, u.Phone, "
            + "u.IdentifierCode, u.IsActive, u.CreatedAt, "
            + "r.RoleName, d.DepartmentName "
            + "FROM Users u "
            + "JOIN Roles r ON u.RoleId = r.RoleId "
            + "LEFT JOIN Departments d ON u.DepartmentId = d.DepartmentId";

    private static final String AUTHENTICATE_USER = 
        "SELECT u.UserId, u.FullName, u.Username, u.Email, u.Phone, r.RoleName, u.IsActive, u.CreatedAt " +
        "FROM Users u " +
        "JOIN Roles r ON u.RoleId = r.RoleId " +
        "WHERE u.Username = ? AND u.PasswordHash = ? AND u.IsActive = 1";

    private static final String CHECK_PHONE_EXISTS =
        "SELECT COUNT(*) FROM Users WHERE Phone = ?";

    private static final String INSERT_USER =
        "INSERT INTO Users (FullName, Username, Phone, Email, PasswordHash, RoleId, IsActive, CreatedAt) " +
        "VALUES (?, ?, ?, ?, ?, (SELECT RoleId FROM Roles WHERE RoleName = 'Salesperson'), ?, ?)";

    private static final String GET_USER_BY_PHONE =
        "SELECT u.UserId, u.FullName, u.Username, u.Email, u.Phone, r.RoleName, u.IsActive, u.CreatedAt " +
        "FROM Users u " +
        "JOIN Roles r ON u.RoleId = r.RoleId " +
        "WHERE u.Phone = ?";

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
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}