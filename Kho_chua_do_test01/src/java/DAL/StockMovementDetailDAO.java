package DAL;

import java.sql.*;
import java.util.*;
import Model.StockMovementDetail;
import Model.ProductDetailSerialNumber;

public class StockMovementDetailDAO extends DataBaseContext {

    public void insertMovementDetail(String dbName, int movementId, int productDetailId, int quantity) throws SQLException {
        String sql = """
            INSERT INTO StockMovementDetail (
            MovementID, ProductDetailID, Quantity, QuantityScanned
        ) VALUES (?, ?, ?, 0);
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            ps.setInt(2, productDetailId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        }
    }

    // 🔹 Lấy danh sách chi tiết sản phẩm trong 1 đơn (import/export)
    public List<StockMovementDetail> getMovementDetailsByMovementId(int movementId) {
        List<StockMovementDetail> details = new ArrayList<>();

        final String sql
                = "SELECT "
                + "   d.MovementDetailID, "
                + "   d.MovementID, "
                + "   d.ProductDetailID, "
                + "   pd.ProductID, "
                + "   pd.ProductCode           AS ProductCode, "
                + // ✅ lấy mã từ ProductDetails
                "   p.ProductName            AS ProductName, "
                + "   d.Quantity, "
                + "   ISNULL(d.QuantityScanned,0) AS Scanned "
                + "FROM dbo.StockMovementDetail d "
                + "JOIN dbo.ProductDetails pd ON d.ProductDetailID = pd.ProductDetailID "
                + "JOIN dbo.Products p        ON pd.ProductID      = p.ProductID "
                + "WHERE d.MovementID = ? "
                + "ORDER BY p.ProductName, pd.ProductCode";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockMovementDetail row = new StockMovementDetail();
                    row.setMovementDetailId(rs.getInt("MovementDetailID"));
                    row.setMovementId(rs.getInt("MovementID"));
                    row.setProductDetailId(rs.getInt("ProductDetailID"));
                    row.setProductId(rs.getInt("ProductID"));
                    row.setProductCode(rs.getString("ProductCode"));
                    row.setProductName(rs.getString("ProductName"));
                    row.setQuantity(rs.getInt("Quantity"));
                    row.setScanned(rs.getInt("Scanned")); // sẽ luôn = 0
                    // row.setSerials(Collections.emptyList()); // nếu không dùng serial, có thể set rỗng
                    details.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return details;
    }

    // 🔹 Lấy danh sách serial numbers cho 1 chi tiết sản phẩm
    private List<ProductDetailSerialNumber> getSerialNumbersByDetailId(int movementDetailId) {
        List<ProductDetailSerialNumber> serials = new ArrayList<>();

        String sql = """
        SELECT 
            ProductDetailID, SerialNumber, Status, OrderID, BranchID, WarehouseID, 
            MovementDetailID, MovementHistory
        FROM ProductDetailSerialNumber
        WHERE MovementDetailID = ?
        ORDER BY SerialNumber
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementDetailId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductDetailSerialNumber serial = new ProductDetailSerialNumber();

                    serial.setProductDetailID(rs.getInt("ProductDetailID"));
                    serial.setSerialNumber(rs.getString("SerialNumber"));
                    serial.setStatus((Boolean) rs.getObject("Status"));
                    serial.setOrderID((Integer) rs.getObject("OrderID"));
                    serial.setBranchID((Integer) rs.getObject("BranchID"));
                    serial.setWarehouseID((Integer) rs.getObject("WarehouseID"));
                    serial.setMovementDetailID(rs.getInt("MovementDetailID"));
                    serial.setMovementHistory(rs.getString("MovementHistory"));

                    serials.add(serial);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi lấy danh sách serial numbers: " + e.getMessage());
            e.printStackTrace();
        }

        return serials;
    }

    // 🔹 Cập nhật số lượng đã quét
    /**
     * Cập nhật trực tiếp số đã quét (ít dùng nếu bạn cập nhật qua thêm/xoá
     * serial).
     */
    public void updateScannedQuantity(int movementDetailId, int scannedQty) {
        final String sql = "UPDATE StockMovementDetail SET QuantityScanned = ? WHERE MovementDetailID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, scannedQty);
            ps.setInt(2, movementDetailId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Kiểm tra xem tất cả sản phẩm trong đơn đã hoàn thành chưa
    /**
     * Điều kiện hoàn tất: tất cả dòng đã quét đủ (QuantityScanned == Quantity).
     */
    public boolean isAllDetailsCompleted(int movementId) {
        final String sql = """
            SELECT COUNT(*) AS IncompleteCount
            FROM StockMovementDetail
            WHERE MovementID = ? AND ISNULL(QuantityScanned, 0) < Quantity
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("IncompleteCount") == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy danh sách chi tiết của 1 Movement; trả về 'Scanned' = QuantityScanned
     * hiện tại.
     */
    public List<StockMovementDetail> getDetailsByMovementId(int movementId) {
        final String sql = """
            SELECT d.MovementDetailID, d.MovementID, d.ProductDetailID,
                   d.Quantity,
                   ISNULL(d.QuantityScanned, 0) AS Scanned,
                   pd.ProductCode,
                   p.ProductName
            FROM StockMovementDetail d
            JOIN ProductDetails pd ON d.ProductDetailID = pd.ProductDetailID
            JOIN Products p        ON pd.ProductID      = p.ProductID
            WHERE d.MovementID = ?
            ORDER BY p.ProductName, pd.ProductCode
        """;
        List<StockMovementDetail> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockMovementDetail it = new StockMovementDetail();
                    it.setMovementDetailId(rs.getInt("MovementDetailID"));
                    it.setMovementId(rs.getInt("MovementID"));
                    it.setProductDetailId(rs.getInt("ProductDetailID"));
                    it.setQuantity(rs.getInt("Quantity"));
                    it.setScanned(rs.getInt("Scanned")); // map vào field Scanned của model
                    it.setProductCode(rs.getString("ProductCode"));
                    it.setProductName(rs.getString("ProductName"));
                    list.add(it);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 🔹 Xóa toàn bộ serial khi cần reset chi tiết
    public void deleteSerialsByDetailId(int movementDetailId) {
        String sql = "DELETE FROM ProductDetailSerialNumber WHERE MovementDetailID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementDetailId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi xóa serial numbers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Integer getProductDetailIdByMovementDetailId(int movementDetailId) {
        String sql = "SELECT ProductDetailID FROM StockMovementDetail WHERE MovementDetailID = ?";
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
        return null;
    }

    // Trả về true nếu dòng đã đủ: Scanned >= Quantity
    public boolean isDetailCompleted(int movementDetailId) {
        String sql = "SELECT CASE WHEN ISNULL(QuantityScanned,0) >= Quantity THEN 1 ELSE 0 END "
                + "FROM StockMovementDetail WHERE MovementDetailID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementDetailId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String computeStatusByScanned(int movementId) {
        final String sql = """
        SELECT
            SUM(ISNULL(QuantityScanned,0)) AS total_scanned,
            SUM(Quantity)                  AS total_qty,
            SUM(CASE WHEN ISNULL(QuantityScanned,0) >= Quantity THEN 1 ELSE 0 END) AS done_lines,
            COUNT(*) AS total_lines
        FROM StockMovementDetail
        WHERE MovementID = ?
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int totalScanned = rs.getInt("total_scanned");
                    int totalQty = rs.getInt("total_qty");
                    int doneLines = rs.getInt("done_lines");
                    int totalLines = rs.getInt("total_lines");

                    if (totalLines == 0) {
                        return "pending";                 // không có dòng, xem là pending
                    }
                    if (doneLines == totalLines) {
                        return "completed";        // tất cả đã đủ
                    }
                    if (totalScanned == 0) {
                        return "pending";               // chưa quét gì
                    }
                    return "processing";                                   // đang xử lý
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "pending";
    }

}
