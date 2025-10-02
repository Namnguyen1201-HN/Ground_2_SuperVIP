package DAL;

import Model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends DataBaseContext {

    // Lấy toàn bộ danh sách sản phẩm
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(int id) {
        String sql = "SELECT * FROM Products WHERE ProductId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm sản phẩm
    public boolean insertProduct(Product p) {
        String sql = "INSERT INTO Products (ProductName, CategoryId, SupplierId, Price, Quantity, ExpiryDate, CreatedAt, UpdatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            if (p.getCategoryId() != null) {
                ps.setInt(2, p.getCategoryId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            if (p.getSupplierId() != null) {
                ps.setInt(3, p.getSupplierId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getQuantity());
            if (p.getExpiryDate() != null) {
                ps.setDate(6, new java.sql.Date(p.getExpiryDate().getTime()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật sản phẩm
    public boolean updateProduct(Product p) {
        String sql = "UPDATE Products SET ProductName=?, CategoryId=?, SupplierId=?, Price=?, Quantity=?, ExpiryDate=?, UpdatedAt=GETDATE() "
                + "WHERE ProductId=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            if (p.getCategoryId() != null) {
                ps.setInt(2, p.getCategoryId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            if (p.getSupplierId() != null) {
                ps.setInt(3, p.getSupplierId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getQuantity());
            if (p.getExpiryDate() != null) {
                ps.setDate(6, new java.sql.Date(p.getExpiryDate().getTime()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            ps.setInt(7, p.getProductId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa sản phẩm
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM Products WHERE ProductId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm sản phẩm theo tên
    public List<Product> searchProductsByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE ProductName COLLATE Latin1_General_CI_AI LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE CategoryId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    
    public List<Product> findProductsWithThreshold(Integer categoryId, String keyword, String stock, int threshold) {
    List<Product> list = new ArrayList<>();
    StringBuilder sql = new StringBuilder("SELECT * FROM Products WHERE 1=1");
    List<Object> params = new ArrayList<>();

    if (categoryId != null) {
        sql.append(" AND CategoryId = ?");
        params.add(categoryId);
    }
    if (keyword != null && !keyword.isBlank()) {
        sql.append(" AND ProductName LIKE ?");
        params.add("%" + keyword.trim() + "%");
    }

    // so sánh tồn kho theo lựa chọn
    if ("in".equals(stock)) {
        sql.append(" AND Quantity > 0");
    } else if ("out".equals(stock)) {
        sql.append(" AND Quantity = 0");
    } else if ("belowMin".equals(stock)) {      // dùng ngưỡng cố định
        sql.append(" AND Quantity < ?");
        params.add(threshold);
    } else if ("aboveMax".equals(stock)) {      // dùng ngưỡng cố định
        sql.append(" AND Quantity > ?");
        params.add(threshold);
    }
    sql.append(" ORDER BY ProductId");

    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
        for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapResultSetToProduct(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}


    // Hàm private để map ResultSet sang Product object
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
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
}
