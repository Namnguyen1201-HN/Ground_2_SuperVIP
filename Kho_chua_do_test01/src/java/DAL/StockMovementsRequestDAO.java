/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import Model.MovementHeader;
import Model.StockMovementsRequest;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockMovementsRequestDAO extends DataBaseContext {

//Được sử dụng khi export tạo ra một MovementRequest
    public int insertExportMovementRequest(
            String dbName,
            int fromBranchId,
            int toWarehouseId,
            String movementType,
            String note,
            int createdBy
    ) throws SQLException {
        String sql = """
        INSERT INTO StockMovementsRequest (
            FromSupplierID, FromBranchID, FromWarehouseID, 
            ToBranchID, ToWarehouseID, 
            MovementType, Note, CreatedBy, CreatedAt
        ) VALUES (NULL, ?, NULL, NULL, ?, ?, ?, ?, GETDATE());
    """;

        try (
                PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, fromBranchId);     // FromBranchID
            ps.setInt(2, toWarehouseId);    // ToWarehouseID
            ps.setString(3, movementType);  // "export"
            ps.setString(4, note);
            ps.setInt(5, createdBy);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // MovementID
                }
            }
        }

        throw new SQLException("Không thể lấy MovementID sau khi insert StockMovementsRequest.");
    }

    public void insertMovementResponse(
            String dbName,
            int movementId,
            int userId,
            String status,
            String note
    ) throws SQLException {
        String sql = """
        INSERT INTO StockMovementResponses (
            MovementID, ResponsedBy, ResponseAt, ResponseStatus, Note
        ) VALUES (?, ?, GETDATE(), ?, ?);
    """;

        try (
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, movementId);
            ps.setInt(2, userId);
            ps.setString(3, status);
            if (note != null) {
                ps.setString(4, note);
            } else {
                ps.setNull(4, java.sql.Types.NVARCHAR);
            }

            ps.executeUpdate();
        }
    }

    // Lấy danh sách đơn nhập hàng
    public List<StockMovementsRequest> getImportRequestsWithFilter(
            String warehouseId, String fromDate, String toDate,
            String branchId, String supplierId, String status,
            int page, int pageSize) {

        List<StockMovementsRequest> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT 
                smr.MovementID,
                smr.FromSupplierID,
                s.SupplierName AS FromSupplierName,
                smr.FromWarehouseID,
                smr.ToBranchID,
                smr.ToWarehouseID,
                smr.MovementType,
                smr.CreatedAt,
                smr.CreatedBy,
                u.FullName AS CreatedByName,
                smr.Note,
                ISNULL(SUM(smd.Quantity * p.CostPrice), 0) AS TotalAmount,
                COALESCE(smrsp.ResponseStatus, 'pending') AS ResponseStatus
            FROM StockMovementsRequest smr
            LEFT JOIN Suppliers s ON smr.FromSupplierID = s.SupplierID
            LEFT JOIN Users u ON smr.CreatedBy = u.UserID
            LEFT JOIN StockMovementDetail smd ON smr.MovementID = smd.MovementID
            LEFT JOIN ProductDetails pd ON smd.ProductDetailID = pd.ProductDetailID
            LEFT JOIN Products p ON pd.ProductID = p.ProductID
            LEFT JOIN (
                SELECT MovementID, ResponseStatus,
                       ROW_NUMBER() OVER (PARTITION BY MovementID ORDER BY ResponseAt DESC) rn
                FROM StockMovementResponses
            ) smrsp ON smr.MovementID = smrsp.MovementID AND smrsp.rn = 1
            WHERE smr.ToWarehouseID = ?
              AND smr.MovementType = 'import'
        """);

        List<Object> params = new ArrayList<>();
        params.add(warehouseId);

        if (fromDate != null && !fromDate.isBlank()) {
            sql.append(" AND CAST(smr.CreatedAt AS DATE) >= ?");
            params.add(fromDate);
        }
        if (toDate != null && !toDate.isBlank()) {
            sql.append(" AND CAST(smr.CreatedAt AS DATE) <= ?");
            params.add(toDate);
        }
        if (branchId != null && !branchId.isBlank()) {
            sql.append(" AND smr.ToBranchID = ?");
            params.add(branchId);
        }
        if (supplierId != null && !supplierId.isBlank()) {
            sql.append(" AND smr.FromSupplierID = ?");
            params.add(supplierId);
        }

        sql.append("""
            GROUP BY 
                smr.MovementID, smr.FromSupplierID, s.SupplierName,
                smr.FromWarehouseID, smr.ToBranchID, smr.ToWarehouseID,
                smr.MovementType, smr.CreatedAt, smr.CreatedBy, u.FullName, smr.Note,
                smrsp.ResponseStatus
        """);

        if (status != null && !status.isBlank()) {
            sql.append(" HAVING COALESCE(smrsp.ResponseStatus, 'pending') = ?");
            params.add(status);
        }

        sql.append(" ORDER BY smr.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockMovementsRequest smr = new StockMovementsRequest();

                    smr.setMovementId(rs.getInt("MovementID"));
                    smr.setFromSupplierId((Integer) rs.getObject("FromSupplierID"));
                    smr.setFromSupplierName(rs.getString("FromSupplierName"));
                    smr.setMovementType(rs.getString("MovementType"));

                    Timestamp ts = rs.getTimestamp("CreatedAt");
                    if (ts != null) {
                        smr.setCreatedAt(new Date(ts.getTime()));
                    }

                    smr.setCreatedBy(rs.getInt("CreatedBy"));
                    smr.setCreatedByName(rs.getString("CreatedByName"));
                    smr.setNote(rs.getString("Note"));
                    smr.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                    smr.setResponseStatus(rs.getString("ResponseStatus"));

                    list.add(smr);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Đếm tổng số đơn nhập
    public int getImportRequestsCount(String warehouseId, String fromDate, String toDate,
            String branchId, String supplierId, String status) {
        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT COUNT(DISTINCT smr.MovementID)
            FROM StockMovementsRequest smr
            LEFT JOIN (
                SELECT MovementID, ResponseStatus,
                       ROW_NUMBER() OVER (PARTITION BY MovementID ORDER BY ResponseAt DESC) rn
                FROM StockMovementResponses
            ) smrsp ON smr.MovementID = smrsp.MovementID AND smrsp.rn = 1
            WHERE smr.ToWarehouseID = ?
              AND smr.MovementType = 'import'
        """);

        List<Object> params = new ArrayList<>();
        params.add(warehouseId);

        if (fromDate != null && !fromDate.isBlank()) {
            sql.append(" AND CAST(smr.CreatedAt AS DATE) >= ?");
            params.add(fromDate);
        }
        if (toDate != null && !toDate.isBlank()) {
            sql.append(" AND CAST(smr.CreatedAt AS DATE) <= ?");
            params.add(toDate);
        }
        if (branchId != null && !branchId.isBlank()) {
            sql.append(" AND smr.ToBranchID = ?");
            params.add(branchId);
        }
        if (supplierId != null && !supplierId.isBlank()) {
            sql.append(" AND smr.FromSupplierID = ?");
            params.add(supplierId);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND COALESCE(smrsp.ResponseStatus, 'pending') = ?");
            params.add(status);
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

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

    // Lấy header chi tiết đơn
    public MovementHeader getMovementHeader(int movementId) {
        String sql = """
            SELECT TOP 1
                smr.MovementID, smr.MovementType, smr.CreatedAt, smr.CreatedBy, u.FullName AS CreatedByName,
                smr.Note, s.SupplierName,
                COALESCE(lastsp.ResponseStatus, COALESCE(smr.ResponseStatus, 'pending')) AS ResponseStatus
            FROM StockMovementsRequest smr
            LEFT JOIN Users u ON smr.CreatedBy = u.UserID
            LEFT JOIN Suppliers s ON smr.FromSupplierID = s.SupplierID
            LEFT JOIN (
                SELECT MovementID, ResponseStatus
                FROM (
                    SELECT MovementID, ResponseStatus,
                           ROW_NUMBER() OVER (PARTITION BY MovementID ORDER BY ResponseAt DESC) rn
                    FROM StockMovementResponses
                ) x WHERE rn = 1
            ) lastsp ON lastsp.MovementID = smr.MovementID
            WHERE smr.MovementID = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MovementHeader m = new MovementHeader();
                    m.setMovementID(rs.getInt("MovementID"));
                    m.setMovementType(rs.getString("MovementType"));

                    Timestamp ts = rs.getTimestamp("CreatedAt");
                    if (ts != null) {
                        m.setCreatedAt(ts.toLocalDateTime());
                    }

                    m.setCreatedBy(rs.getInt("CreatedBy"));
                    m.setCreatedByName(rs.getString("CreatedByName"));
                    m.setNote(rs.getString("Note"));
                    m.setSupplierName(rs.getString("SupplierName"));
                    m.setResponseStatus(rs.getString("ResponseStatus"));
                    return m;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Cập nhật trạng thái đơn nhập
    public void updateMovementResponseStatus(int movementId, String status) {
        String sql = "UPDATE StockMovementsRequest SET ResponseStatus = ? WHERE MovementID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, movementId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Lấy 1 phiếu nhập cụ thể theo ID
    public StockMovementsRequest getImportRequestById(int movementId) {
        String sql = """
        SELECT 
            smr.MovementID,
            smr.FromSupplierID,
            s.SupplierName AS FromSupplierName,
            smr.FromWarehouseID,
            smr.ToBranchID,
            smr.ToWarehouseID,
            smr.MovementType,
            smr.CreatedAt,
            smr.CreatedBy,
            u.FullName AS CreatedByName,
            smr.Note,
            COALESCE(smrsp.ResponseStatus, 'pending') AS ResponseStatus
        FROM StockMovementsRequest smr
        LEFT JOIN Suppliers s ON smr.FromSupplierID = s.SupplierID
        LEFT JOIN Users u ON smr.CreatedBy = u.UserID
        LEFT JOIN (
            SELECT MovementID, ResponseStatus,
                   ROW_NUMBER() OVER (PARTITION BY MovementID ORDER BY ResponseAt DESC) rn
            FROM StockMovementResponses
        ) smrsp ON smr.MovementID = smrsp.MovementID AND smrsp.rn = 1
        WHERE smr.MovementID = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StockMovementsRequest smr = new StockMovementsRequest();
                    smr.setMovementId(rs.getInt("MovementID"));
                    smr.setFromSupplierId((Integer) rs.getObject("FromSupplierID"));
                    smr.setFromSupplierName(rs.getString("FromSupplierName"));
                    smr.setMovementType(rs.getString("MovementType"));
                    smr.setCreatedBy(rs.getInt("CreatedBy"));
                    smr.setCreatedByName(rs.getString("CreatedByName"));
                    smr.setNote(rs.getString("Note"));
                    smr.setResponseStatus(rs.getString("ResponseStatus"));
                    return smr;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Ghi một lần vào StockMovementResponses
    public boolean updateMovementStatus(int movementId, String status, int userId, String note) {
        String sql = "INSERT INTO StockMovementResponses (MovementID, ResponsedBy, ResponseStatus, Note) "
                + "VALUES (?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            ps.setInt(2, userId);
            ps.setString(3, status); // "processing" | "completed" ...
            ps.setString(4, note);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy ToWarehouseID của phiếu nhập; trả về null nếu không có.
     */
    public Integer getToWarehouseIdByMovementId(int movementId) {
        final String sql = "SELECT ToWarehouseID FROM StockMovementsRequest WHERE MovementID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // dùng getObject để nhận được null khi DB là NULL
                    return (Integer) rs.getObject(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * (Tuỳ chọn) Chuyển quyền sở hữu serial về kho đích sau khi hoàn tất phiếu.
     */
    public int moveSerialsToDestinationWarehouse(int movementId, int toWarehouseId) {
        final String sql
                = "UPDATE s "
                + "SET s.WarehouseID = ?, "
                + "    s.BranchID     = NULL, "
                + "    s.OrderID      = NULL, "
                + "    s.Status       = 1 "
                + // 1 = đang nằm ở kho
                "FROM ProductDetailSerialNumber s "
                + "JOIN StockMovementDetail d ON s.MovementDetailID = d.MovementDetailID "
                + "WHERE d.MovementID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, toWarehouseId);
            ps.setInt(2, movementId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<StockMovementsRequest> listImportsWithComputedStatus(
            String statusFilter, java.sql.Date from, java.sql.Date to) {

        // Map filter JSP -> SQL
        String where = "WHERE smr.MovementType = 'import'";
        List<Object> params = new ArrayList<>();

        // Ngày tạo
        if (from != null) {
            where += " AND smr.CreatedAt >= ?";
            params.add(from);
        }
        if (to != null) {
            where += " AND smr.CreatedAt <  DATEADD(day, 1, ?)";
            params.add(to);
        }

        // Tính state dựa trên scanned
        String base = """
       SELECT
         smr.MovementID,
         smr.FromSupplierID,
         s.SupplierName,
         smr.CreatedAt,
         smr.CreatedBy,
         u.FullName AS CreatedByName,
         smr.Note,
         -- TÍNH STATUS:
         CASE
           WHEN COUNT(d.MovementDetailID) = 0 THEN 'pending'  -- không có dòng
           WHEN SUM(CASE WHEN ISNULL(d.QuantityScanned,0) >= d.Quantity THEN 1 ELSE 0 END) = COUNT(d.MovementDetailID)
                THEN 'completed'
           WHEN SUM(ISNULL(d.QuantityScanned,0)) = 0 THEN 'pending'
           ELSE 'processing'
         END AS ResponseStatus,
         -- Có thể có tổng tiền nếu bạn join/compute ở nơi khác
         NULL AS TotalAmount
       FROM StockMovementsRequest smr
       LEFT JOIN StockMovementDetail d ON d.MovementID = smr.MovementID
       LEFT JOIN Suppliers s ON s.SupplierID = smr.FromSupplierID
       LEFT JOIN Users u ON u.UserID = smr.CreatedBy
       """ + where + """
       GROUP BY smr.MovementID, smr.FromSupplierID, s.SupplierName, smr.CreatedAt, smr.CreatedBy, u.FullName, smr.Note
       ORDER BY smr.CreatedAt DESC
    """;

        // Bọc thêm filter theo status (sau khi tính)
        String sql = base;
        if (statusFilter != null && !statusFilter.isBlank()) {
            sql = "SELECT * FROM (" + base + ") x WHERE LOWER(x.ResponseStatus) = ?";
        }

        List<StockMovementsRequest> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;
            for (Object p : params) {
                if (p instanceof java.sql.Date) {
                    ps.setDate(idx++, (java.sql.Date) p);
                } else {
                    ps.setObject(idx++, p);
                }
            }
            if (statusFilter != null && !statusFilter.isBlank()) {
                ps.setString(idx++, statusFilter.toLowerCase().trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockMovementsRequest m = new StockMovementsRequest();
                    m.setMovementId(rs.getInt("MovementID"));
                    m.setFromSupplierId((Integer) rs.getObject("FromSupplierID"));
                    m.setFromSupplierName(rs.getString("SupplierName"));
                    m.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    m.setCreatedBy(rs.getInt("CreatedBy"));
                    m.setCreatedByName(rs.getString("CreatedByName"));
                    m.setNote(rs.getString("Note"));
                    m.setResponseStatus(rs.getString("ResponseStatus")); // <-- trạng thái đã tính
                    // m.setTotalAmount(rs.getBigDecimal("TotalAmount")); // nếu có
                    out.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    // ========== EXPORT ORDERS METHODS ==========
    
    /**
     * Lấy danh sách đơn xuất hàng với bộ lọc (cho warehouse manager)
     */
    public List<StockMovementsRequest> getExportRequestsWithFilter(
            int warehouseId, String fromDate, String toDate,
            String productId, String status,
            int page, int pageSize) {

        List<StockMovementsRequest> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT 
                smr.MovementID,
                smr.FromWarehouseID,
                w.WarehouseName AS FromWarehouseName,
                smr.FromBranchID,
                b.BranchName AS FromBranchName,
                smr.ToBranchID,
                smr.ToWarehouseID,
                smr.MovementType,
                smr.CreatedAt,
                smr.CreatedBy,
                u.FullName AS CreatedByName,
                smr.Note,
                COALESCE(smrsp.ResponseStatus, 'Chờ xử lý') AS ResponseStatus
            FROM StockMovementsRequest smr
            LEFT JOIN Warehouses w ON smr.FromWarehouseID = w.WarehouseID
            LEFT JOIN Branches b ON smr.FromBranchID = b.BranchID
            LEFT JOIN Users u ON smr.CreatedBy = u.UserID
            LEFT JOIN (
                SELECT MovementID, ResponseStatus,
                       ROW_NUMBER() OVER (PARTITION BY MovementID ORDER BY ResponseAt DESC) rn
                FROM StockMovementResponses
            ) smrsp ON smr.MovementID = smrsp.MovementID AND smrsp.rn = 1
            WHERE smr.FromWarehouseID = ?
              AND smr.MovementType = 'export'
        """);

        List<Object> params = new ArrayList<>();
        params.add(warehouseId);

        if (fromDate != null && !fromDate.isBlank()) {
            sql.append(" AND CAST(smr.CreatedAt AS DATE) >= ?");
            params.add(fromDate);
        }
        if (toDate != null && !toDate.isBlank()) {
            sql.append(" AND CAST(smr.CreatedAt AS DATE) <= ?");
            params.add(toDate);
        }
        if (productId != null && !productId.isBlank()) {
            sql.append(" AND EXISTS (SELECT 1 FROM StockMovementDetail smd WHERE smd.MovementID = smr.MovementID AND smd.ProductDetailID = ?)");
            params.add(productId);
        }

        sql.append(" GROUP BY smr.MovementID, smr.FromWarehouseID, w.WarehouseName, smr.FromBranchID, b.BranchName, smr.ToBranchID, smr.ToWarehouseID, smr.MovementType, smr.CreatedAt, smr.CreatedBy, u.FullName, smr.Note, smrsp.ResponseStatus");

        if (status != null && !status.isBlank() && !status.equals("Tất cả")) {
            sql.append(" HAVING COALESCE(smrsp.ResponseStatus, 'Chờ xử lý') = ?");
            params.add(status);
        }

        sql.append(" ORDER BY smr.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockMovementsRequest smr = new StockMovementsRequest();

                    smr.setMovementId(rs.getInt("MovementID"));
                    smr.setFromWarehouseId((Integer) rs.getObject("FromWarehouseID"));
                    smr.setFromWarehouseName(rs.getString("FromWarehouseName"));
                    smr.setFromBranchId((Integer) rs.getObject("FromBranchID"));
                    smr.setFromBranchName(rs.getString("FromBranchName"));
                    smr.setToBranchId((Integer) rs.getObject("ToBranchID"));
                    smr.setMovementType(rs.getString("MovementType"));

                    Timestamp ts = rs.getTimestamp("CreatedAt");
                    if (ts != null) {
                        smr.setCreatedAt(new Date(ts.getTime()));
                    }

                    smr.setCreatedBy(rs.getInt("CreatedBy"));
                    smr.setCreatedByName(rs.getString("CreatedByName"));
                    smr.setNote(rs.getString("Note"));
                    smr.setResponseStatus(rs.getString("ResponseStatus"));

                    list.add(smr);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Đếm tổng số đơn xuất
     */
    public int getExportRequestsCount(int warehouseId, String fromDate, String toDate,
            String productId, String status) {
        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT COUNT(DISTINCT smr.MovementID)
            FROM StockMovementsRequest smr
            LEFT JOIN (
                SELECT MovementID, ResponseStatus,
                       ROW_NUMBER() OVER (PARTITION BY MovementID ORDER BY ResponseAt DESC) rn
                FROM StockMovementResponses
            ) smrsp ON smr.MovementID = smrsp.MovementID AND smrsp.rn = 1
            WHERE smr.FromWarehouseID = ?
              AND smr.MovementType = 'export'
        """);

        List<Object> params = new ArrayList<>();
        params.add(warehouseId);

        if (fromDate != null && !fromDate.isBlank()) {
            sql.append(" AND CAST(smr.CreatedAt AS DATE) >= ?");
            params.add(fromDate);
        }
        if (toDate != null && !toDate.isBlank()) {
            sql.append(" AND CAST(smr.CreatedAt AS DATE) <= ?");
            params.add(toDate);
        }
        if (productId != null && !productId.isBlank()) {
            sql.append(" AND EXISTS (SELECT 1 FROM StockMovementDetail smd WHERE smd.MovementID = smr.MovementID AND smd.ProductDetailID = ?)");
            params.add(productId);
        }
        if (status != null && !status.isBlank() && !status.equals("Tất cả")) {
            sql.append(" AND COALESCE(smrsp.ResponseStatus, 'Chờ xử lý') = ?");
            params.add(status);
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

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

    /**
     * Lấy 1 phiếu xuất cụ thể theo ID
     */
    public StockMovementsRequest getExportRequestById(int movementId) {
        String sql = """
        SELECT 
            smr.MovementID,
            smr.FromWarehouseID,
            w.WarehouseName AS FromWarehouseName,
            smr.FromBranchID,
            b.BranchName AS FromBranchName,
            smr.ToBranchID,
            smr.ToWarehouseID,
            smr.MovementType,
            smr.CreatedAt,
            smr.CreatedBy,
            u.FullName AS CreatedByName,
            smr.Note,
            COALESCE(smrsp.ResponseStatus, 'Chờ xử lý') AS ResponseStatus
        FROM StockMovementsRequest smr
        LEFT JOIN Warehouses w ON smr.FromWarehouseID = w.WarehouseID
        LEFT JOIN Branches b ON smr.FromBranchID = b.BranchID
        LEFT JOIN Users u ON smr.CreatedBy = u.UserID
        LEFT JOIN (
            SELECT MovementID, ResponseStatus,
                   ROW_NUMBER() OVER (PARTITION BY MovementID ORDER BY ResponseAt DESC) rn
            FROM StockMovementResponses
        ) smrsp ON smr.MovementID = smrsp.MovementID AND smrsp.rn = 1
        WHERE smr.MovementID = ?
          AND smr.MovementType = 'export'
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StockMovementsRequest smr = new StockMovementsRequest();
                    smr.setMovementId(rs.getInt("MovementID"));
                    smr.setFromWarehouseId((Integer) rs.getObject("FromWarehouseID"));
                    smr.setFromWarehouseName(rs.getString("FromWarehouseName"));
                    smr.setFromBranchId((Integer) rs.getObject("FromBranchID"));
                    smr.setFromBranchName(rs.getString("FromBranchName"));
                    smr.setToBranchId((Integer) rs.getObject("ToBranchID"));
                    smr.setMovementType(rs.getString("MovementType"));
                    
                    Timestamp ts = rs.getTimestamp("CreatedAt");
                    if (ts != null) {
                        smr.setCreatedAt(new Date(ts.getTime()));
                    }
                    
                    smr.setCreatedBy(rs.getInt("CreatedBy"));
                    smr.setCreatedByName(rs.getString("CreatedByName"));
                    smr.setNote(rs.getString("Note"));
                    smr.setResponseStatus(rs.getString("ResponseStatus"));
                    return smr;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
