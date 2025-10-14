package DAL;

import Model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CategoryDAO — tương thích hoàn toàn với:
 * CREATE TABLE Categories (
 *     CategoryID INT PRIMARY KEY,
 *     CategoryName NVARCHAR(100)
 * );
 */
public class CategoryDAO extends DataBaseContext {

    /** Lấy toàn bộ danh mục (sắp xếp theo tên) */
    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT CategoryID, CategoryName FROM Categories ORDER BY CategoryName";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Thêm danh mục mới */
    public boolean insert(Category c) {
        String sql = "INSERT INTO Categories (CategoryID, CategoryName) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, c.getCategoryID());
            ps.setString(2, c.getCategoryName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xóa danh mục theo ID */
    public boolean delete(int id) {
        String sql = "DELETE FROM Categories WHERE CategoryID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật tên danh mục */
    public boolean update(Category c) {
        String sql = "UPDATE Categories SET CategoryName = ? WHERE CategoryID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getCategoryName());
            ps.setInt(2, c.getCategoryID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Lấy danh mục theo ID */
    public Category getById(int id) {
        String sql = "SELECT CategoryID, CategoryName FROM Categories WHERE CategoryID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Mapper chuyển từ ResultSet → Category */
    private Category map(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setCategoryID(rs.getInt("CategoryID"));
        c.setCategoryName(rs.getString("CategoryName"));
        return c;
    }
}
