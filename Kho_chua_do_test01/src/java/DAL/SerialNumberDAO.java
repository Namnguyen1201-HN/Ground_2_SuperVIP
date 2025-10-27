/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import DAL.DataBaseContext;
import Model.ProductDetailSerialNumber;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.*;

/**
 *
 * @author TieuPham
 */
public class SerialNumberDAO extends DataBaseContext {

    /**
     * 🔹 Lấy danh sách serial theo MovementDetailID
     */
    /**
     * 🔹 Lấy danh sách serial theo MovementDetailID
     */
    // ==== LẤY LIST SERIAL THEO MOVEMENT DETAIL (để show ra JSP) ====
    public List<ProductDetailSerialNumber> getSerialsByMovementDetailId(int movementDetailId) {
        List<ProductDetailSerialNumber> list = new ArrayList<>();
        final String sql = "SELECT ProductDetailID, SerialNumber, Status, OrderID, BranchID, WarehouseID, MovementDetailID, MovementHistory "
                + "FROM ProductDetailSerialNumber WHERE MovementDetailID = ? ORDER BY SerialNumber";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementDetailId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductDetailSerialNumber s = new ProductDetailSerialNumber();
                    s.setProductDetailID(rs.getInt("ProductDetailID"));
                    s.setSerialNumber(rs.getString("SerialNumber"));
                    s.setStatus((Boolean) rs.getObject("Status"));
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
     * 🔹 Kiểm tra xem một serial đã tồn tại trong phiếu nhập chưa
     */
    public boolean isSerialExists(int movementDetailId, String serialNumber) {
        String sql = """
            SELECT COUNT(*) 
            FROM ProductDetailSerialNumber 
            WHERE MovementDetailID = ? AND SerialNumber = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementDetailId);
            ps.setString(2, serialNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi kiểm tra serial tồn tại: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 🔹 Thêm serial mới (sau khi quét)
     */
    /**
     * Thêm 1 serial; sau khi INSERT thành công -> +1 QuantityScanned (không
     * vượt Quantity).
     */
    // ==== INSERT SERIAL: chặn trùng + +1 scanned (ISNULL) ====
    public boolean insertSerial(ProductDetailSerialNumber serial) {
        if (serial == null) {
            return false;
        }
        if (serial.getSerialNumber() == null || serial.getSerialNumber().trim().isEmpty()) {
            return false;
        }
        final String sn = serial.getSerialNumber().trim();
        final Integer mdId = (serial.getMovementDetailID() > 0) ? serial.getMovementDetailID() : null;

        // Cần movementDetailId để kiểm tra đủ số lượng
        if (mdId == null) {
            return false;
        }

        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // 1) INSERT có điều kiện (không trùng + dòng chưa đủ)
            final String ins = """
            INSERT INTO ProductDetailSerialNumber
                (ProductDetailID, SerialNumber, Status, OrderID, BranchID, WarehouseID, MovementDetailID, MovementHistory)
            SELECT ?, ?, ?, ?, ?, ?, ?, ?
            WHERE NOT EXISTS (SELECT 1 FROM ProductDetailSerialNumber x WHERE x.SerialNumber = ?)
              AND EXISTS (
                    SELECT 1
                    FROM StockMovementDetail d
                    WHERE d.MovementDetailID = ?
                      AND ISNULL(d.QuantityScanned,0) < d.Quantity
              )
            """;

            try (PreparedStatement ps = connection.prepareStatement(ins)) {
                // 1..8: giá trị insert
                if (serial.getProductDetailID() > 0) {
                    ps.setInt(1, serial.getProductDetailID());
                } else {
                    ps.setNull(1, Types.INTEGER);
                }
                ps.setString(2, sn);
                if (serial.getStatus() == null) {
                    ps.setNull(3, Types.BIT);
                } else {
                    ps.setBoolean(3, serial.getStatus());
                }
                if (serial.getOrderID() == null) {
                    ps.setNull(4, Types.INTEGER);
                } else {
                    ps.setInt(4, serial.getOrderID());
                }
                if (serial.getBranchID() == null) {
                    ps.setNull(5, Types.INTEGER);
                } else {
                    ps.setInt(5, serial.getBranchID());
                }
                if (serial.getWarehouseID() == null) {
                    ps.setNull(6, Types.INTEGER);
                } else {
                    ps.setInt(6, serial.getWarehouseID());
                }
                ps.setInt(7, mdId);
                ps.setString(8, serial.getMovementHistory());

                // 9..10: điều kiện
                ps.setString(9, sn);
                ps.setInt(10, mdId);

                int inserted = ps.executeUpdate();
                if (inserted == 0) { // không thoả điều kiện → rollback
                    connection.rollback();
                    connection.setAutoCommit(oldAutoCommit);
                    return false;
                }
            }

            // 2) Cộng scanned (vẫn có điều kiện để tránh vượt)
            final String up = """
            UPDATE StockMovementDetail
            SET QuantityScanned = ISNULL(QuantityScanned,0) + 1
            WHERE MovementDetailID = ?
              AND ISNULL(QuantityScanned,0) < Quantity
            """;
            try (PreparedStatement upst = connection.prepareStatement(up)) {
                upst.setInt(1, mdId);
                int affected = upst.executeUpdate();
                if (affected == 0) { // ai đó vừa đủ mất rồi → rollback chèn ở trên
                    connection.rollback();
                    connection.setAutoCommit(oldAutoCommit);
                    return false;
                }
            }

            connection.commit();
            connection.setAutoCommit(oldAutoCommit);
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            e.printStackTrace();
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException ignore) {
            }
            return false;
        }
    }

    /**
     * 🔹 Cập nhật trạng thái serial
     */
    public boolean updateSerialStatus(String serialNumber, boolean status) {
        String sql = """
            UPDATE ProductDetailSerialNumber
            SET Status = ?
            WHERE SerialNumber = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setString(2, serialNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi cập nhật trạng thái serial: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 🔹 Xóa tất cả serial theo chi tiết phiếu
     */
    public void deleteSerialsByMovementDetailId(int movementDetailId) {
        String sql = "DELETE FROM ProductDetailSerialNumber WHERE MovementDetailID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementDetailId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi xóa serial theo MovementDetailID: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xoá 1 serial theo MovementDetailID + SerialNumber; sau khi DELETE -> -1
     * QuantityScanned (không âm).
     */
    // ==== XOÁ 1 SERIAL: -1 scanned ====
    public boolean deleteSingleSerial(int movementDetailId, String serialNumber) {
        final String del = "DELETE FROM ProductDetailSerialNumber WHERE MovementDetailID = ? AND SerialNumber = ?";
        try (PreparedStatement ps = connection.prepareStatement(del)) {
            ps.setInt(1, movementDetailId);
            ps.setString(2, serialNumber);
            int ok = ps.executeUpdate();

            if (ok > 0) {
                final String down
                        = "UPDATE StockMovementDetail "
                        + "SET QuantityScanned = CASE WHEN ISNULL(QuantityScanned,0) > 0 "
                        + "                           THEN ISNULL(QuantityScanned,0) - 1 ELSE 0 END "
                        + "WHERE MovementDetailID = ?";
                try (PreparedStatement upst = connection.prepareStatement(down)) {
                    upst.setInt(1, movementDetailId);
                    upst.executeUpdate();
                }
            }

            return ok > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 🔹 Đếm số serial của một chi tiết phiếu
     */
    public int countSerialsByMovementDetailId(int movementDetailId) {
        String sql = "SELECT COUNT(*) AS Total FROM ProductDetailSerialNumber WHERE MovementDetailID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementDetailId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Total");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi đếm serials: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 🔹 Lấy một serial cụ thể theo mã serial (dùng cho kiểm tra hoặc chi tiết)
     */
    public ProductDetailSerialNumber getSerialByCode(String serialNumber) {
        String sql = """
            SELECT 
                ProductDetailID, SerialNumber, Status, OrderID, BranchID, WarehouseID,
                MovementDetailID, MovementHistory
            FROM ProductDetailSerialNumber
            WHERE SerialNumber = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, serialNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProductDetailSerialNumber s = new ProductDetailSerialNumber();
                    s.setProductDetailID(rs.getInt("ProductDetailID"));
                    s.setSerialNumber(rs.getString("SerialNumber"));
                    s.setStatus((Boolean) rs.getObject("Status"));
                    s.setOrderID((Integer) rs.getObject("OrderID"));
                    s.setBranchID((Integer) rs.getObject("BranchID"));
                    s.setWarehouseID((Integer) rs.getObject("WarehouseID"));
                    s.setMovementDetailID(rs.getInt("MovementDetailID"));
                    s.setMovementHistory(rs.getString("MovementHistory"));
                    return s;
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi lấy serial theo mã: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean existsSerial(String serial) {
        String sql = "SELECT 1 FROM ProductDetailSerialNumber WHERE SerialNumber = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, serial);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
