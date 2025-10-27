package DAL;

import Model.ProductDetailSerialNumber;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO thao tác với bảng ProductDetailSerialNumber
 */
public class ProductDetailSerialDAO extends DataBaseContext {

    /**
     * Lấy danh sách serial theo MovementDetailID
     */
    public List<ProductDetailSerialNumber> getSerialsByMovementDetailId(int movementDetailId) {
        List<ProductDetailSerialNumber> list = new ArrayList<>();

        String sql = """
            SELECT ProductDetailID, SerialNumber, Status, OrderID, BranchID, WarehouseID, 
                   MovementDetailID, MovementHistory
            FROM ProductDetailSerialNumber
            WHERE MovementDetailID = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementDetailId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductDetailSerialNumber s = new ProductDetailSerialNumber();
                    s.setProductDetailID(rs.getInt("ProductDetailID"));
                    s.setSerialNumber(rs.getString("SerialNumber"));
                    s.setStatus(rs.getBoolean("Status")); // BIT -> Boolean
                    s.setOrderID((Integer) rs.getObject("OrderID"));
                    s.setBranchID((Integer) rs.getObject("BranchID"));
                    s.setWarehouseID((Integer) rs.getObject("WarehouseID"));
                    s.setMovementDetailID(rs.getInt("MovementDetailID"));
                    s.setMovementHistory(rs.getString("MovementHistory"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Kiểm tra serial đã tồn tại chưa
     */
    private boolean isSerialExists(String serial) {
        String sql = "SELECT COUNT(*) FROM ProductDetailSerialNumber WHERE SerialNumber = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, serial);
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

    /**
     * Thêm mới một serial (có kiểm tra trùng)
     */
    public boolean insertSerial(int productDetailId, int movementDetailId, String serial) {
        // 1️⃣ Kiểm tra trùng
        if (isSerialExists(serial)) {
            System.out.println("⚠️ Serial đã tồn tại: " + serial);
            return false;
        }

        // 2️⃣ Kiểm tra số lượng tối đa
        String checkSql = """
        SELECT Quantity, QuantityScanned 
        FROM StockMovementDetail 
        WHERE MovementDetailID = ?
    """;
        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            check.setInt(1, movementDetailId);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    int quantity = rs.getInt("Quantity");
                    int scanned = rs.getInt("QuantityScanned");
                    if (scanned >= quantity) {
                        System.out.println("⚠️ Đã đủ số lượng, không thể thêm serial nữa!");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // 3️⃣ Nếu còn chỗ, thêm serial mới
        String sql = """
        INSERT INTO ProductDetailSerialNumber
        (ProductDetailID, SerialNumber, Status, MovementDetailID)
        VALUES (?, ?, 1, ?)
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productDetailId);
            ps.setString(2, serial);
            ps.setInt(3, movementDetailId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xoá serial (nếu cần)
     */
    public boolean deleteSerial(String serial) {
        String sql = "DELETE FROM ProductDetailSerialNumber WHERE SerialNumber = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, serial);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Đếm số serial thuộc 1 MovementDetail
     */
    public int countSerialByMovementDetail(int movementDetailId) {
        String sql = "SELECT COUNT(*) FROM ProductDetailSerialNumber WHERE MovementDetailID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementDetailId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
