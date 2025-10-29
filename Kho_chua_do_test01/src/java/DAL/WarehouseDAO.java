package DAL;

import Model.Warehouse;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDAO extends DataBaseContext {

    // Lấy tất cả kho (kể cả không active)
    public List<Warehouse> getAllWarehouses() {
        List<Warehouse> list = new ArrayList<>();
        String sql = "SELECT * FROM Warehouses";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy các kho đang hoạt động
    public List<Warehouse> getActiveWarehouses() {
        List<Warehouse> list = new ArrayList<>();
        String sql = "SELECT * FROM Warehouses WHERE IsActive = 1";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy kho theo ID
    public Warehouse getWarehouseById(int id) {
        String sql = "SELECT * FROM Warehouses WHERE WarehouseID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm map dữ liệu
    private Warehouse map(ResultSet rs) throws SQLException {
        Warehouse w = new Warehouse();
        w.setWarehouseId(rs.getInt("WarehouseID"));
        w.setWarehouseName(rs.getString("WarehouseName"));
        w.setAddress(rs.getString("Address"));
        w.setPhone(rs.getString("Phone"));
        w.setActive(rs.getBoolean("IsActive"));
        return w;
    }

    // Thêm kho mới
    public boolean insertWarehouse(Warehouse w) {
        String sql = "INSERT INTO Warehouses (WarehouseName, Address, Phone, IsActive) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, w.getWarehouseName());
            ps.setString(2, w.getAddress());
            ps.setString(3, w.getPhone());
            ps.setBoolean(4, w.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật kho
    public boolean updateWarehouse(Warehouse w) {
        String sql = "UPDATE Warehouses SET WarehouseName=?, Address=?, Phone=?, IsActive=? WHERE WarehouseID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, w.getWarehouseName());
            ps.setString(2, w.getAddress());
            ps.setString(3, w.getPhone());
            ps.setBoolean(4, w.isActive());
            ps.setInt(5, w.getWarehouseId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa kho
    public boolean deleteWarehouse(int id) {
        String sql = "DELETE FROM Warehouses WHERE WarehouseID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPhoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM Warehouses WHERE Phone = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
