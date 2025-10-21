package DAL;

import Model.StockMovementDetail;
import Model.StockMovementsRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockMovementDAO extends DataBaseContext {

    /* ===== Helpers ===== */
    private StockMovementsRequest mapHeader(ResultSet rs) throws SQLException {
        StockMovementsRequest r = new StockMovementsRequest();
        r.setMovementId(rs.getInt("MovementID"));

        int v;
        v = rs.getInt("FromSupplierID");
        if (!rs.wasNull()) {
            r.setFromSupplierId(v);
        }
        v = rs.getInt("FromBranchID");
        if (!rs.wasNull()) {
            r.setFromBranchId(v);
        }
        v = rs.getInt("FromWarehouseID");
        if (!rs.wasNull()) {
            r.setFromWarehouseId(v);
        }
        v = rs.getInt("ToBranchID");
        if (!rs.wasNull()) {
            r.setToBranchId(v);
        }
        v = rs.getInt("ToWarehouseID");
        if (!rs.wasNull()) {
            r.setToWarehouseId(v);
        }

        r.setMovementType(rs.getString("MovementType"));
        r.setCreatedBy(rs.getInt("CreatedBy"));
        r.setCreatedAt(rs.getTimestamp("CreatedAt"));
        r.setNote(rs.getString("Note"));
        return r;
    }

    /* ===== Paging DTO ===== */
    public static class PagedMoves {

        public final List<StockMovementsRequest> items;
        public final int total;
        public final int page;
        public final int pageSize;

        public PagedMoves(List<StockMovementsRequest> items, int total, int page, int pageSize) {
            this.items = items;
            this.total = total;
            this.page = page;
            this.pageSize = pageSize;
        }
    }

    /* ===== Query list with filters ===== */
    public PagedMoves list(String type, Integer branchId, Integer warehouseId,
            Timestamp from, Timestamp to, int page, int pageSize) {
        List<StockMovementsRequest> list = new ArrayList<>();
        List<Object> ps = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        if (type != null && !type.isBlank()) {
            where.append(" AND MovementType = ?");
            ps.add(type);
        }
        if (branchId != null) {
            where.append(" AND (FromBranchID = ? OR ToBranchID = ?)");
            ps.add(branchId);
            ps.add(branchId);
        }
        if (warehouseId != null) {
            where.append(" AND (FromWarehouseID = ? OR ToWarehouseID = ?)");
            ps.add(warehouseId);
            ps.add(warehouseId);
        }
        if (from != null) {
            where.append(" AND CreatedAt >= ?");
            ps.add(from);
        }
        if (to != null) {
            where.append(" AND CreatedAt <= ?");
            ps.add(to);
        }

        int offset = Math.max(0, (page - 1) * pageSize);

        String countSql = "SELECT COUNT(*) FROM StockMovementsRequest" + where;
        String sql = "SELECT * FROM StockMovementsRequest" + where
                + " ORDER BY MovementID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        int total = 0;
        try (PreparedStatement cps = connection.prepareStatement(countSql)) {
            for (int i = 0; i < ps.size(); i++) {
                cps.setObject(i + 1, ps.get(i));
            }
            try (ResultSet rs = cps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            int idx = 1;
            for (Object p : ps) {
                psmt.setObject(idx++, p);
            }
            psmt.setInt(idx++, offset);
            psmt.setInt(idx, pageSize);
            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHeader(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PagedMoves(list, total, page, pageSize);
    }

    /* ===== Details ===== */
    public List<StockMovementDetail> getDetails(int movementId) {
        List<StockMovementDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM StockMovementDetail WHERE MovementID=? ORDER BY MovementDetailID";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockMovementDetail d = new StockMovementDetail();
                    d.setMovementDetailId(rs.getInt("MovementDetailID"));
                    d.setMovementId(rs.getInt("MovementID"));
                    d.setProductDetailId(rs.getInt("ProductDetailID"));
                    d.setQuantity(rs.getInt("Quantity"));
                    int qs = rs.getInt("QuantityScanned");
                    if (!rs.wasNull()) {
                        d.setQuantityScanned(qs);
                    }
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public StockMovementsRequest getById(int id) {
        String sql = "SELECT * FROM StockMovementsRequest WHERE MovementID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StockMovementsRequest r = mapHeader(rs);
                    r.setDetails(getDetails(id));
                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* ===== Update header: bản cũ (giữ lại nếu nơi khác đang dùng) ===== */
    public boolean updateHeader(int id, String movementType, String note) {
        String sql = "UPDATE StockMovementsRequest SET MovementType=?, Note=? WHERE MovementID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, movementType);
            ps.setString(2, note);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ===== Update header: bản đầy đủ ===== */
    public boolean updateHeaderFull(
            int id,
            Integer fromSupplierId,
            Integer fromBranchId,
            Integer fromWarehouseId,
            Integer toBranchId,
            Integer toWarehouseId,
            String movementType,
            String note
    ) {
        String sql = "UPDATE StockMovementsRequest "
                + "SET FromSupplierID=?, FromBranchID=?, FromWarehouseID=?, "
                + "    ToBranchID=?, ToWarehouseID=?, MovementType=?, Note=? "
                + "WHERE MovementID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (fromSupplierId != null) {
                ps.setInt(1, fromSupplierId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            if (fromBranchId != null) {
                ps.setInt(2, fromBranchId);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            if (fromWarehouseId != null) {
                ps.setInt(3, fromWarehouseId);
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            if (toBranchId != null) {
                ps.setInt(4, toBranchId);
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            if (toWarehouseId != null) {
                ps.setInt(5, toWarehouseId);
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, movementType);
            ps.setString(7, note);
            ps.setInt(8, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ===== (Tuỳ chọn) Thay toàn bộ details ===== */
    public boolean replaceDetails(int movementId, List<StockMovementDetail> details) throws SQLException {
        String del = "DELETE FROM StockMovementDetail WHERE MovementID=?";
        String ins = "INSERT INTO StockMovementDetail (MovementID, ProductDetailID, Quantity) VALUES (?,?,?)";
        boolean auto = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement d = connection.prepareStatement(del)) {
                d.setInt(1, movementId);
                d.executeUpdate();
            }
            if (details != null && !details.isEmpty()) {
                try (PreparedStatement i = connection.prepareStatement(ins)) {
                    for (StockMovementDetail s : details) {
                        i.setInt(1, movementId);
                        i.setInt(2, s.getProductDetailId());
                        i.setInt(3, s.getQuantity());
                        i.addBatch();
                    }
                    i.executeBatch();
                }
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(auto);
        }
    }

    /* ===== Delete ===== */
    public boolean delete(int id) {
        String delDetails = "DELETE FROM StockMovementDetail WHERE MovementID=?";
        String delHeader = "DELETE FROM StockMovementsRequest WHERE MovementID=?";
        boolean auto = false;
        try {
            auto = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement a = connection.prepareStatement(delDetails)) {
                a.setInt(1, id);
                a.executeUpdate();
            }
            try (PreparedStatement b = connection.prepareStatement(delHeader)) {
                b.setInt(1, id);
                b.executeUpdate();
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(auto);
            } catch (SQLException ignored) {
            }
        }
        return false;
    }

    /* ===== Create (transaction) ===== */
    public int create(StockMovementsRequest r) throws SQLException {
        String sql = "INSERT INTO StockMovementsRequest "
                + "(FromSupplierID, FromBranchID, FromWarehouseID, ToBranchID, ToWarehouseID, "
                + " MovementType, CreatedBy, CreatedAt, Note) "
                + "VALUES (?,?,?,?,?,?,?,GETDATE(),?)";
        boolean auto = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            int newId = -1;
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (r.getFromSupplierId() != null) {
                    ps.setInt(1, r.getFromSupplierId());
                } else {
                    ps.setNull(1, Types.INTEGER);
                }
                if (r.getFromBranchId() != null) {
                    ps.setInt(2, r.getFromBranchId());
                } else {
                    ps.setNull(2, Types.INTEGER);
                }
                if (r.getFromWarehouseId() != null) {
                    ps.setInt(3, r.getFromWarehouseId());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                if (r.getToBranchId() != null) {
                    ps.setInt(4, r.getToBranchId());
                } else {
                    ps.setNull(4, Types.INTEGER);
                }
                if (r.getToWarehouseId() != null) {
                    ps.setInt(5, r.getToWarehouseId());
                } else {
                    ps.setNull(5, Types.INTEGER);
                }
                ps.setString(6, r.getMovementType());
                ps.setInt(7, r.getCreatedBy());
                ps.setString(8, r.getNote());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        newId = rs.getInt(1);
                    }
                }
            }

            if (r.getDetails() != null && !r.getDetails().isEmpty()) {
                String dsql = "INSERT INTO StockMovementDetail (MovementID, ProductDetailID, Quantity) VALUES (?,?,?)";
                try (PreparedStatement ps = connection.prepareStatement(dsql)) {
                    for (StockMovementDetail d : r.getDetails()) {
                        ps.setInt(1, newId);
                        ps.setInt(2, d.getProductDetailId());
                        ps.setInt(3, d.getQuantity());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            connection.commit();
            return newId;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(auto);
        }
    }

    // 📦 Lấy danh sách đơn nhập hàng (Import)
    public List<Map<String, Object>> listImportOrders(
            Integer warehouseId, Timestamp from, Timestamp to, int page, int pageSize) {

        List<Map<String, Object>> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT 
                smr.MovementID,
                s.SupplierName,
                u.FullName AS CreatedByName,
                smr.CreatedAt,
                ISNULL(SUM(smd.Quantity * p.CostPrice), 0) AS TotalAmount,
                COALESCE(resp.ResponseStatus, 'pending') AS Status
            FROM StockMovementsRequest smr
            LEFT JOIN Suppliers s ON smr.FromSupplierID = s.SupplierID
            LEFT JOIN Users u ON smr.CreatedBy = u.UserID
            LEFT JOIN StockMovementDetail smd ON smr.MovementID = smd.MovementID
            LEFT JOIN ProductDetails pd ON smd.ProductDetailID = pd.ProductDetailID
            LEFT JOIN Products p ON pd.ProductID = p.ProductID
            LEFT JOIN (
                SELECT MovementID, ResponseStatus,
                       ROW_NUMBER() OVER (PARTITION BY MovementID ORDER BY ResponseAt DESC) AS rn
                FROM StockMovementResponses
            ) resp ON smr.MovementID = resp.MovementID AND resp.rn = 1
            WHERE smr.MovementType = 'Import'
              AND smr.ToWarehouseID = ?
        """);

        List<Object> params = new ArrayList<>();
        params.add(warehouseId);

        if (from != null) {
            sql.append(" AND smr.CreatedAt >= ?");
            params.add(from);
        }

        if (to != null) {
            sql.append(" AND smr.CreatedAt <= ?");
            params.add(to);
        }

        sql.append("""
            GROUP BY smr.MovementID, s.SupplierName, u.FullName, smr.CreatedAt, resp.ResponseStatus
            ORDER BY smr.CreatedAt DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """);

        params.add(offset);
        params.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("MovementID", rs.getInt("MovementID"));
                    row.put("SupplierName", rs.getString("SupplierName"));
                    row.put("CreatedByName", rs.getString("CreatedByName"));
                    row.put("CreatedAt", rs.getTimestamp("CreatedAt"));
                    row.put("TotalAmount", rs.getBigDecimal("TotalAmount"));
                    row.put("Status", rs.getString("Status"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //Xuất hàng
    public List<Map<String, Object>> listExportOrders(Integer warehouseId, Timestamp from, Timestamp to, String status, int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT smr.MovementID,
               b.BranchName AS ToBranchName,
               u.FullName AS CreatedByName,
               smr.CreatedAt,
               COALESCE(r.ResponseStatus, 'pending') AS Status,
               smr.Note
        FROM StockMovementsRequest smr
        LEFT JOIN Branches b ON smr.ToBranchID = b.BranchID
        LEFT JOIN Users u ON smr.CreatedBy = u.UserID
        LEFT JOIN (
            SELECT MovementID, ResponseStatus,
                   ROW_NUMBER() OVER (PARTITION BY MovementID ORDER BY ResponseAt DESC) AS rn
            FROM StockMovementResponses
        ) r ON smr.MovementID = r.MovementID AND r.rn = 1
        WHERE smr.MovementType = 'Export'
          AND smr.FromWarehouseID = ?
    """);

        // Thêm điều kiện lọc ngày
        if (from != null) {
            sql.append(" AND smr.CreatedAt >= ?");
        }
        if (to != null) {
            sql.append(" AND smr.CreatedAt <= ?");
        }
        if (status != null && !status.equalsIgnoreCase("Tất cả")) {
            sql.append(" AND COALESCE(r.ResponseStatus,'pending') = ?");
        }

        sql.append(" ORDER BY smr.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, warehouseId);
            if (from != null) {
                ps.setTimestamp(idx++, from);
            }
            if (to != null) {
                ps.setTimestamp(idx++, to);
            }
            if (status != null && !status.equalsIgnoreCase("Tất cả")) {
                ps.setString(idx++, status);
            }
            ps.setInt(idx++, (page - 1) * pageSize);
            ps.setInt(idx++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("MovementID", rs.getInt("MovementID"));
                    row.put("ToBranchName", rs.getString("ToBranchName"));
                    row.put("CreatedByName", rs.getString("CreatedByName"));
                    row.put("CreatedAt", rs.getTimestamp("CreatedAt"));
                    row.put("Status", rs.getString("Status"));
                    row.put("Note", rs.getString("Note"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}
