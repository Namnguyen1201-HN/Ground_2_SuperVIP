package DAL;

import Model.StockMovementDetail;
import Model.StockMovementsRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockMovementDAO extends DataBaseContext {

    private StockMovementsRequest mapHeader(ResultSet rs) throws SQLException {
        StockMovementsRequest r = new StockMovementsRequest();
        r.setMovementId(rs.getInt("MovementID"));
        Object fs = rs.getObject("FromSupplierID"); if (fs!=null) r.setFromSupplierId((Integer)fs);
        Object fb = rs.getObject("FromBranchID"); if (fb!=null) r.setFromBranchId((Integer)fb);
        Object fw = rs.getObject("FromWarehouseID"); if (fw!=null) r.setFromWarehouseId((Integer)fw);
        Object tb = rs.getObject("ToBranchID"); if (tb!=null) r.setToBranchId((Integer)tb);
        Object tw = rs.getObject("ToWarehouseID"); if (tw!=null) r.setToWarehouseId((Integer)tw);
        r.setMovementType(rs.getString("MovementType"));
        r.setCreatedBy(rs.getInt("CreatedBy"));
        r.setCreatedAt(rs.getTimestamp("CreatedAt"));
        r.setNote(rs.getString("Note"));
        return r;
    }

    public static class PagedMoves {
        public final List<StockMovementsRequest> items; public final int total; public final int page; public final int pageSize;
        public PagedMoves(List<StockMovementsRequest> items, int total, int page, int pageSize){ this.items=items; this.total=total; this.page=page; this.pageSize=pageSize; }
    }

    public PagedMoves list(String type, Integer branchId, Integer warehouseId, Timestamp from, Timestamp to, int page, int pageSize) {
        List<StockMovementsRequest> list = new ArrayList<>();
        List<Object> ps = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        if (type!=null && !type.isBlank()) { where.append(" AND MovementType = ?"); ps.add(type); }
        if (branchId != null) { where.append(" AND (FromBranchID = ? OR ToBranchID = ?)"); ps.add(branchId); ps.add(branchId); }
        if (warehouseId != null) { where.append(" AND (FromWarehouseID = ? OR ToWarehouseID = ?)"); ps.add(warehouseId); ps.add(warehouseId); }
        if (from!=null) { where.append(" AND CreatedAt >= ?"); ps.add(from); }
        if (to!=null) { where.append(" AND CreatedAt <= ?"); ps.add(to); }

        int offset = Math.max(0,(page-1)*pageSize);
        String countSql = "SELECT COUNT(*) FROM StockMovementsRequest" + where;
        String sql = "SELECT * FROM StockMovementsRequest" + where + " ORDER BY MovementID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        int total = 0;
        try (PreparedStatement cps = connection.prepareStatement(countSql)) {
            for (int i=0;i<ps.size();i++) cps.setObject(i+1, ps.get(i));
            try (ResultSet rs = cps.executeQuery()) { if (rs.next()) total = rs.getInt(1); }
        } catch (SQLException e) { e.printStackTrace(); }

        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            int idx=1; for (Object p: ps) psmt.setObject(idx++, p);
            psmt.setInt(idx++, offset); psmt.setInt(idx, pageSize);
            try (ResultSet rs = psmt.executeQuery()) { while (rs.next()) list.add(mapHeader(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return new PagedMoves(list,total,page,pageSize);
    }

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
                    Object qs = rs.getObject("QuantityScanned"); if (qs!=null) d.setQuantityScanned((Integer)qs);
                    list.add(d);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
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
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateHeader(int id, String movementType, String note) {
        String sql = "UPDATE StockMovementsRequest SET MovementType=?, Note=? WHERE MovementID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, movementType);
            ps.setString(2, note);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String delDetails = "DELETE FROM StockMovementDetail WHERE MovementID=?";
        String delHeader = "DELETE FROM StockMovementsRequest WHERE MovementID=?";
        boolean auto = false;
        try {
            auto = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement a = connection.prepareStatement(delDetails)) { a.setInt(1, id); a.executeUpdate(); }
            try (PreparedStatement b = connection.prepareStatement(delHeader)) { b.setInt(1, id); b.executeUpdate(); }
            connection.commit();
            return true;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
        } finally {
            try { connection.setAutoCommit(auto); } catch (SQLException ignored) {}
        }
        return false;
    }

    public int create(StockMovementsRequest r) throws SQLException {
        String sql = "INSERT INTO StockMovementsRequest (FromSupplierID, FromBranchID, FromWarehouseID, ToBranchID, ToWarehouseID, MovementType, CreatedBy, CreatedAt, Note) VALUES (?,?,?,?,?,?,?,?,GETDATE())";
        boolean auto = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            int newId = -1;
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (r.getFromSupplierId()!=null) ps.setInt(1, r.getFromSupplierId()); else ps.setNull(1, Types.INTEGER);
                if (r.getFromBranchId()!=null) ps.setInt(2, r.getFromBranchId()); else ps.setNull(2, Types.INTEGER);
                if (r.getFromWarehouseId()!=null) ps.setInt(3, r.getFromWarehouseId()); else ps.setNull(3, Types.INTEGER);
                if (r.getToBranchId()!=null) ps.setInt(4, r.getToBranchId()); else ps.setNull(4, Types.INTEGER);
                if (r.getToWarehouseId()!=null) ps.setInt(5, r.getToWarehouseId()); else ps.setNull(5, Types.INTEGER);
                ps.setString(6, r.getMovementType());
                ps.setInt(7, r.getCreatedBy());
                ps.setString(8, r.getNote());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) newId = rs.getInt(1); }
            }
            if (newId <= 0) { connection.rollback(); return -1; }

            if (r.getDetails()!=null) {
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
}


