/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import Model.CustomerRequest;
import Model.Product;
import Model.User;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class CustomerRequestDAO extends DataBaseContext {

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE UserId = ?";
        User user = null;
        try ( PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
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
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("ProductId"));
        p.setProductName(rs.getString("ProductName"));
        int catId = rs.getInt("CategoryId");
        if (!rs.wasNull()) {
            p.setCategoryId(catId);
        }
        int supId = rs.getInt("SupplierId");
        if (!rs.wasNull()) {
            p.setSupplierId(supId);
        }
        p.setPrice(rs.getDouble("Price"));
        p.setQuantity(rs.getInt("Quantity"));
        Date expDate = rs.getDate("ExpiryDate");
        if (expDate != null) {
            p.setExpiryDate(expDate);
        }
        p.setCreatedAt(rs.getTimestamp("CreatedAt"));
        p.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        return p;  
    }

    public Product getProductById(int id) {
        String sql = "SELECT * FROM Products WHERE ProductId = ?";
        try ( PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getTotalCustomerRequestsByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM CustomerRequests WHERE UserId = ?";
        try ( PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<CustomerRequest> getCustomerRequestsByUserIdPaged(int userId, int pageIndex, int pageSize) {
        List<CustomerRequest> customerRequests = new ArrayList<>();
        String sql = "SELECT * FROM CustomerRequests WHERE UserId = ? ORDER BY RequestedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try ( PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int offset = (pageIndex - 1) * pageSize;
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CustomerRequest request = new CustomerRequest();
                    request.setRequestId(rs.getInt("RequestId"));
                    request.setCustomerName(rs.getString("CustomerName"));
                    request.setCustomerContact(rs.getString("CustomerContact"));
                    request.setProductName(rs.getString("ProductName"));
                    request.setQuantity(rs.getInt("Quantity"));
                    request.setRequestedAt(rs.getDate("RequestedAt"));
                    request.setStatus(rs.getString("Status"));
                    request.setProductId(rs.getInt("ProductId"));
                    request.setUserId(rs.getInt("UserId"));
                    customerRequests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerRequests;
    }

    public int getTotalCustomerRequestsFiltered(int userId, String keyword, String status, java.sql.Date fromDate, java.sql.Date toDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM CustomerRequests WHERE UserId = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (CustomerName LIKE ? OR CustomerContact LIKE ? OR ProductName LIKE ?)");
        }
        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            sql.append(" AND Status = ?");
        }
        if (fromDate != null) {
            sql.append(" AND RequestedAt >= ?");
        }
        if (toDate != null) {
            sql.append(" AND RequestedAt <= ?");
        }

        try ( PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, userId);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
                ps.setString(idx++, status);
            }
            if (fromDate != null) {
                ps.setDate(idx++, fromDate);
            }
            if (toDate != null) {
                ps.setDate(idx++, toDate);
            }

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<CustomerRequest> getCustomerRequestsByUserIdPagedFiltered(int userId, int pageIndex, int pageSize, String keyword, String status, java.sql.Date fromDate, java.sql.Date toDate) {
        List<CustomerRequest> customerRequests = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM CustomerRequests WHERE UserId = ?");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (CustomerName LIKE ? OR CustomerContact LIKE ? OR ProductName LIKE ?)");
        }
        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            sql.append(" AND Status = ?");
        }
        if (fromDate != null) {
            sql.append(" AND RequestedAt >= ?");
        }
        if (toDate != null) {
            sql.append(" AND RequestedAt <= ?");
        }

        sql.append(" ORDER BY RequestedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try ( PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, userId);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
                ps.setString(idx++, status);
            }
            if (fromDate != null) {
                ps.setDate(idx++, fromDate);
            }
            if (toDate != null) {
                ps.setDate(idx++, toDate);
            }

            int offset = (pageIndex - 1) * pageSize;
            ps.setInt(idx++, offset);
            ps.setInt(idx++, pageSize);

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CustomerRequest request = new CustomerRequest();
                    request.setRequestId(rs.getInt("RequestId"));
                    request.setCustomerName(rs.getString("CustomerName"));
                    request.setCustomerContact(rs.getString("CustomerContact"));
                    request.setProductName(rs.getString("ProductName"));
                    request.setQuantity(rs.getInt("Quantity"));
                    request.setRequestedAt(rs.getDate("RequestedAt"));
                    request.setStatus(rs.getString("Status"));
                    request.setProductId(rs.getInt("ProductId"));
                    request.setUserId(rs.getInt("UserId"));
                    customerRequests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerRequests;
    }

    public List<CustomerRequest> getAllCustomerRequestByUserId(int userId) {
        List<CustomerRequest> customerRequests = new ArrayList<>();
        String sql = "SELECT * FROM CustomerRequests WHERE UserId = ?";
        try ( PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CustomerRequest request = new CustomerRequest();
                    request.setRequestId(rs.getInt("RequestId"));
                    request.setCustomerName(rs.getString("CustomerName"));
                    request.setCustomerContact(rs.getString("CustomerContact"));
                    request.setProductName(rs.getString("ProductName"));
                    request.setQuantity(rs.getInt("Quantity"));
                    request.setRequestedAt(rs.getDate("RequestedAt"));
                    request.setStatus(rs.getString("Status"));
                    request.setProductId(rs.getInt("ProductId"));
                    request.setUserId(rs.getInt("UserId"));
                    customerRequests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerRequests;
    }
}
