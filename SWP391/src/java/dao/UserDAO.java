/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.User;
import java.sql.*;
/**
 *
 * @author Lenovo
 */


public class UserDAO {
    private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    // Đăng ký user mới
    public boolean register(User user) {
        try {
            String sql = "INSERT INTO Users (FullName, Username, PasswordHash, Phone, RoleId) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getPhone());
            ps.setInt(5, user.getRoleId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Đăng nhập
    public User login(String username, String password) {
        try {
            String sql = "SELECT * FROM Users WHERE Username=? AND PasswordHash=? AND IsActive=1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("UserId"));
                u.setFullName(rs.getString("FullName"));
                u.setUsername(rs.getString("Username"));
                u.setPhone(rs.getString("Phone"));
                u.setRoleId(rs.getInt("RoleId"));
                return u;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
