package DAL;

import Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DataBaseContext {

    private static final String SELECT_ALL_USERS
            = "SELECT u.UserId, u.FullName, u.Username, u.Email, u.Phone, r.RoleName, u.IsActive, u.CreatedAt "
            + "FROM Users u "
            + "JOIN Roles r ON u.RoleId = r.RoleId";

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
