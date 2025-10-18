package DAL;

import Model.Brand;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO extends DataBaseContext {

    /**
     * Lấy toàn bộ danh sách thương hiệu từ bảng Brands.
     * @return List<Brand> – danh sách tất cả các thương hiệu trong CSDL
     */
    public List<Brand> getAll() {
        List<Brand> list = new ArrayList<>();
        String sql = "SELECT BrandID, BrandName FROM Brands ORDER BY BrandName";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Brand b = new Brand();
                b.setBrandId(rs.getInt("BrandID"));
                b.setBrandName(rs.getString("BrandName"));
                list.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tùy chọn: Lấy một thương hiệu theo ID
     */
    public Brand getById(int id) {
        String sql = "SELECT BrandID, BrandName FROM Brands WHERE BrandID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Brand(rs.getInt("BrandID"), rs.getString("BrandName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
