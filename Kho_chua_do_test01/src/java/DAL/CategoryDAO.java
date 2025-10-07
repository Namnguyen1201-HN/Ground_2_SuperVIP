package DAL;

import Model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends DataBaseContext {
    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories ORDER BY CategoryName";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Category c) {
        String sql = "INSERT INTO Categories(CategoryName, Description) VALUES(?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getCategoryName());
            ps.setString(2, c.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Category map(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setCategoryId(rs.getInt("CategoryId"));
        c.setCategoryName(rs.getString("CategoryName"));
        c.setDescription(rs.getString("Description"));
        return c;
    }
}


