package DAL;

import Model.ImportCartItem;
import Model.StockMovementDetail;
import Model.StockMovementsRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockMovementDAO extends DataBaseContext {

    private StockMovementsRequest mapHeader(ResultSet rs) throws SQLException {
        StockMovementsRequest r = new StockMovementsRequest();
        r.setMovementId(rs.getInt("MovementID"));
        Object fs = rs.getObject("FromSupplierID");
        if (fs != null) {
            r.setFromSupplierId((Integer) fs);
        }
        Object fb = rs.getObject("FromBranchID");
        if (fb != null) {
            r.setFromBranchId((Integer) fb);
        }
        Object fw = rs.getObject("FromWarehouseID");
        if (fw != null) {
            r.setFromWarehouseId((Integer) fw);
        }
        Object tb = rs.getObject("ToBranchID");
        if (tb != null) {
            r.setToBranchId((Integer) tb);
        }
        Object tw = rs.getObject("ToWarehouseID");
        if (tw != null) {
            r.setToWarehouseId((Integer) tw);
        }
        r.setMovementType(rs.getString("MovementType"));
        r.setCreatedBy(rs.getInt("CreatedBy"));
        r.setCreatedAt(rs.getTimestamp("CreatedAt"));
        r.setNote(rs.getString("Note"));
        return r;
    }

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

    public PagedMoves list(String type, Integer branchId, Integer warehouseId, Timestamp from, Timestamp to, int page, int pageSize) {
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
        String sql = "SELECT * FROM StockMovementsRequest" + where + " ORDER BY MovementID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

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
                    Object qs = rs.getObject("QuantityScanned");
                    if (qs != null) {
                        d.setQuantityScanned((Integer) qs);
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

    public int create(StockMovementsRequest r) throws SQLException {
        String sql = "INSERT INTO StockMovementsRequest (FromSupplierID, FromBranchID, FromWarehouseID, ToBranchID, ToWarehouseID, MovementType, CreatedBy, CreatedAt, Note) VALUES (?,?,?,?,?,?,?,GETDATE(),?)";
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
            if (newId <= 0) {
                connection.rollback();
                return -1;
            }

            if (r.getDetails() != null) {
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

    public List<Map<String, Object>> listImportOrders(Integer warehouseId, Timestamp from, Timestamp to, int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("""
        SELECT 
            r.MovementID,
            s.SupplierName,
            u.FullName AS CreatedByName,
            r.CreatedAt,
            r.Note,
            ISNULL(resp.ResponseStatus, 'pending') AS Status,
            SUM(p.CostPrice * d.Quantity) AS TotalAmount
        FROM StockMovementsRequest r
        LEFT JOIN Suppliers s ON r.FromSupplierID = s.SupplierID
        LEFT JOIN Users u ON r.CreatedBy = u.UserID
        LEFT JOIN StockMovementResponses resp ON r.MovementID = resp.MovementID
        LEFT JOIN StockMovementDetail d ON r.MovementID = d.MovementID
        LEFT JOIN ProductDetails pd ON d.ProductDetailID = pd.ProductDetailID
        LEFT JOIN Products p ON pd.ProductID = p.ProductID
        WHERE r.MovementType = 'Import' AND r.ToWarehouseID = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(warehouseId);
        if (from != null) {
            sql.append(" AND r.CreatedAt >= ?");
            params.add(from);
        }
        if (to != null) {
            sql.append(" AND r.CreatedAt <= ?");
            params.add(to);
        }
        sql.append("""
        GROUP BY r.MovementID, s.SupplierName, u.FullName, r.CreatedAt, r.Note, resp.ResponseStatus
        ORDER BY r.MovementID DESC
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """);
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                ps.setObject(idx++, p);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("MovementID", rs.getInt("MovementID"));
                row.put("SupplierName", rs.getString("SupplierName"));
                row.put("CreatedByName", rs.getString("CreatedByName"));
                row.put("CreatedAt", rs.getTimestamp("CreatedAt"));
                row.put("Note", rs.getString("Note"));
                row.put("Status", rs.getString("Status"));
                row.put("TotalAmount", rs.getBigDecimal("TotalAmount"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Create import request from supplier to warehouse
     * @param supplierID ID of supplier
     * @param toWarehouseID ID of destination warehouse
     * @param createdBy ID of user creating request
     * @param cartItems List of items to import
     * @param note Optional note
     * @return true if successful
     */
    public boolean createImportRequest(int supplierID, int toWarehouseID, int createdBy, 
                                      List<ImportCartItem> cartItems, String note) {
        System.out.println("=== [StockMovementDAO] createImportRequest called ===");
        System.out.println("SupplierID: " + supplierID);
        System.out.println("ToWarehouseID: " + toWarehouseID);
        System.out.println("CreatedBy: " + createdBy);
        System.out.println("Cart items: " + (cartItems != null ? cartItems.size() : "NULL"));
        System.out.println("Note: " + note);
        
        String headerSql = "INSERT INTO StockMovementsRequest (FromSupplierID, ToWarehouseID, MovementType, CreatedBy, CreatedAt, Note) " +
                          "VALUES (?, ?, 'Import', ?, GETDATE(), ?)";
        String detailSql = "INSERT INTO StockMovementDetail (MovementID, ProductDetailID, Quantity, QuantityScanned) " +
                          "VALUES (?, ?, ?, 0)";
        
        boolean autoCommit = false;
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            System.out.println("Transaction started, auto-commit disabled");
            
            int movementID = -1;
            
            // Insert header
            System.out.println("Inserting header record...");
            try (PreparedStatement ps = connection.prepareStatement(headerSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, supplierID);
                ps.setInt(2, toWarehouseID);
                ps.setInt(3, createdBy);
                ps.setString(4, note);
                int affectedRows = ps.executeUpdate();
                System.out.println("Header inserted, affected rows: " + affectedRows);
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        movementID = rs.getInt(1);
                        System.out.println("Generated MovementID: " + movementID);
                    }
                }
            }
            
            if (movementID <= 0) {
                System.err.println("❌ Failed to get MovementID, rolling back");
                connection.rollback();
                return false;
            }
            
            // Insert details
            System.out.println("Inserting detail records...");
            try (PreparedStatement ps = connection.prepareStatement(detailSql)) {
                for (ImportCartItem item : cartItems) {
                    System.out.println("  - Adding batch: ProductDetailID=" + item.getProductDetailID() + ", Quantity=" + item.getQuantity());
                    ps.setInt(1, movementID);
                    ps.setInt(2, item.getProductDetailID());
                    ps.setInt(3, item.getQuantity());
                    ps.addBatch();
                }
                int[] results = ps.executeBatch();
                System.out.println("Details inserted, batch results count: " + results.length);
            }
            
            connection.commit();
            System.out.println("✅ Transaction committed successfully");
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ SQL Error occurred: " + e.getMessage());
            try {
                connection.rollback();
                System.err.println("Transaction rolled back");
            } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException ignored) {}
        }
    }
    
    /**
     * Create export request from warehouse to branch
     * Used by branch managers to request products from warehouse
     */
//    public boolean createExportRequest(int fromWarehouseID, int toBranchID, int createdBy, 
//                                      List<Model.ExportCartItem> cartItems, String note) {
//        System.out.println("=== [StockMovementDAO] createExportRequest called ===");
//        System.out.println("FromWarehouseID: " + fromWarehouseID);
//        System.out.println("ToBranchID: " + toBranchID);
//        System.out.println("CreatedBy: " + createdBy);
//        System.out.println("Cart items: " + (cartItems != null ? cartItems.size() : "NULL"));
//        System.out.println("Note: " + note);
//        
//        String headerSql = "INSERT INTO StockMovementsRequest (FromWarehouseID, ToBranchID, MovementType, Status, CreatedBy, CreatedAt, Note) " +
//                          "VALUES (?, ?, 'Export', 'Pending', ?, GETDATE(), ?)";
//        String detailSql = "INSERT INTO StockMovementDetail (MovementID, ProductDetailID, Quantity, QuantityScanned) " +
//                          "VALUES (?, ?, ?, 0)";
//        
//        boolean autoCommit = false;
//        try {
//            autoCommit = connection.getAutoCommit();
//            connection.setAutoCommit(false);
//            System.out.println("Transaction started, auto-commit disabled");
//            
//            int movementID = -1;
//            
//            // Insert header
//            System.out.println("Inserting header record...");
//            try (PreparedStatement ps = connection.prepareStatement(headerSql, Statement.RETURN_GENERATED_KEYS)) {
//                ps.setInt(1, fromWarehouseID);
//                ps.setInt(2, toBranchID);
//                ps.setInt(3, createdBy);
//                ps.setString(4, note);
//                int affectedRows = ps.executeUpdate();
//                System.out.println("Header inserted, affected rows: " + affectedRows);
//                
//                try (ResultSet rs = ps.getGeneratedKeys()) {
//                    if (rs.next()) {
//                        movementID = rs.getInt(1);
//                        System.out.println("Generated MovementID: " + movementID);
//                    }
//                }
//            }
//            
//            if (movementID <= 0) {
//                System.err.println("❌ Failed to get MovementID, rolling back");
//                connection.rollback();
//                return false;
//            }
//            
//            // Insert details
//            System.out.println("Inserting detail records...");
//            try (PreparedStatement ps = connection.prepareStatement(detailSql)) {
//                for (Model.ExportCartItem item : cartItems) {
//                    System.out.println("  - Adding batch: ProductDetailID=" + item.getProductDetailID() + ", Quantity=" + item.getQuantity());
//                    ps.setInt(1, movementID);
//                    ps.setInt(2, item.getProductDetailID());
//                    ps.setInt(3, item.getQuantity());
//                    ps.addBatch();
//                }
//                int[] results = ps.executeBatch();
//                System.out.println("Details inserted, batch results count: " + results.length);
//            }
//            
//            connection.commit();
//            System.out.println("✅ Transaction committed successfully");
//            return true;
//            
//        } catch (SQLException e) {
//            System.err.println("❌ SQL Error occurred: " + e.getMessage());
//            try {
//                connection.rollback();
//                System.err.println("Transaction rolled back");
//            } catch (SQLException ignored) {}
//            e.printStackTrace();
//            return false;
//        } finally {
//            try {
//                connection.setAutoCommit(autoCommit);
//            } catch (SQLException ignored) {}
//        }
//    }
    
    /**
     * Get all import requests for admin
     */
//    public List<Model.StockMovementResponse> getAllImportRequests() {
//        List<Model.StockMovementResponse> list = new ArrayList<>();
//        String sql = "SELECT smr.MovementID, smr.FromSupplierID, s.SupplierName, " +
//                     "smr.ToWarehouseID, w.WareHouseName, smr.MovementType, " +
//                     "smr.Status, smr.CreatedBy, u.FullName as CreatorName, " +
//                     "smr.CreatedAt, smr.ApprovedBy, u2.FullName as ApproverName, " +
//                     "smr.ApprovedAt, smr.Note, " +
//                     "(SELECT COUNT(*) FROM StockMovementDetail WHERE MovementID = smr.MovementID) as TotalItems, " +
//                     "(SELECT SUM(Quantity) FROM StockMovementDetail WHERE MovementID = smr.MovementID) as TotalQuantity " +
//                     "FROM StockMovementsRequest smr " +
//                     "LEFT JOIN Suppliers s ON smr.FromSupplierID = s.SupplierID " +
//                     "LEFT JOIN Warehouses w ON smr.ToWarehouseID = w.WareHouseId " +
//                     "LEFT JOIN Users u ON smr.CreatedBy = u.UserID " +
//                     "LEFT JOIN Users u2 ON smr.ApprovedBy = u2.UserID " +
//                     "WHERE smr.MovementType = 'Import' " +
//                     "ORDER BY smr.CreatedAt DESC";
//        
//        try (PreparedStatement ps = connection.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                Model.StockMovementResponse response = new Model.StockMovementResponse();
//                response.setMovementID(rs.getInt("MovementID"));
//                response.setFromSupplierID(rs.getInt("FromSupplierID"));
//                response.setSupplierName(rs.getString("SupplierName"));
//                response.setToWarehouseID(rs.getInt("ToWarehouseID"));
//                response.setWarehouseName(rs.getString("WareHouseName"));
//                response.setMovementType(rs.getString("MovementType"));
//                response.setStatus(rs.getString("Status"));
//                response.setCreatedBy(rs.getInt("CreatedBy"));
//                response.setCreatorName(rs.getString("CreatorName"));
//                response.setCreatedAt(rs.getTimestamp("CreatedAt"));
//                response.setApprovedBy(rs.getInt("ApprovedBy"));
//                response.setApproverName(rs.getString("ApproverName"));
//                response.setApprovedAt(rs.getTimestamp("ApprovedAt"));
//                response.setNote(rs.getString("Note"));
//                response.setTotalItems(rs.getInt("TotalItems"));
//                response.setTotalQuantity(rs.getInt("TotalQuantity"));
//                list.add(response);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//    
//    /**
//     * Get all export requests for admin
//     */
//    public List<Model.StockMovementResponse> getAllExportRequests() {
//        List<Model.StockMovementResponse> list = new ArrayList<>();
//        String sql = "SELECT smr.MovementID, smr.FromWarehouseID, w.WareHouseName as FromWarehouseName, " +
//                     "smr.ToBranchID, b.BranchName, smr.MovementType, " +
//                     "smr.Status, smr.CreatedBy, u.FullName as CreatorName, " +
//                     "smr.CreatedAt, smr.ApprovedBy, u2.FullName as ApproverName, " +
//                     "smr.ApprovedAt, smr.Note, " +
//                     "(SELECT COUNT(*) FROM StockMovementDetail WHERE MovementID = smr.MovementID) as TotalItems, " +
//                     "(SELECT SUM(Quantity) FROM StockMovementDetail WHERE MovementID = smr.MovementID) as TotalQuantity " +
//                     "FROM StockMovementsRequest smr " +
//                     "LEFT JOIN Warehouses w ON smr.FromWarehouseID = w.WareHouseId " +
//                     "LEFT JOIN Branches b ON smr.ToBranchID = b.BranchID " +
//                     "LEFT JOIN Users u ON smr.CreatedBy = u.UserID " +
//                     "LEFT JOIN Users u2 ON smr.ApprovedBy = u2.UserID " +
//                     "WHERE smr.MovementType = 'Export' " +
//                     "ORDER BY smr.CreatedAt DESC";
//        
//        try (PreparedStatement ps = connection.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                Model.StockMovementResponse response = new Model.StockMovementResponse();
//                response.setMovementID(rs.getInt("MovementID"));
//                response.setFromWarehouseID(rs.getInt("FromWarehouseID"));
//                response.setWarehouseName(rs.getString("FromWarehouseName"));
//                response.setToBranchID(rs.getInt("ToBranchID"));
//                response.setBranchName(rs.getString("BranchName"));
//                response.setMovementType(rs.getString("MovementType"));
//                response.setStatus(rs.getString("Status"));
//                response.setCreatedBy(rs.getInt("CreatedBy"));
//                response.setCreatorName(rs.getString("CreatorName"));
//                response.setCreatedAt(rs.getTimestamp("CreatedAt"));
//                response.setApprovedBy(rs.getInt("ApprovedBy"));
//                response.setApproverName(rs.getString("ApproverName"));
//                response.setApprovedAt(rs.getTimestamp("ApprovedAt"));
//                response.setNote(rs.getString("Note"));
//                response.setTotalItems(rs.getInt("TotalItems"));
//                response.setTotalQuantity(rs.getInt("TotalQuantity"));
//                list.add(response);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//    
//    /**
//     * Get export requests by branch ID
//     */
//    public List<Model.StockMovementResponse> getExportRequestsByBranch(int branchId) {
//        List<Model.StockMovementResponse> list = new ArrayList<>();
//        String sql = "SELECT smr.MovementID, smr.FromWarehouseID, w.WareHouseName as FromWarehouseName, " +
//                     "smr.ToBranchID, b.BranchName, smr.MovementType, " +
//                     "smr.Status, smr.CreatedBy, u.FullName as CreatorName, " +
//                     "smr.CreatedAt, smr.ApprovedBy, u2.FullName as ApproverName, " +
//                     "smr.ApprovedAt, smr.Note, " +
//                     "(SELECT COUNT(*) FROM StockMovementDetail WHERE MovementID = smr.MovementID) as TotalItems, " +
//                     "(SELECT SUM(Quantity) FROM StockMovementDetail WHERE MovementID = smr.MovementID) as TotalQuantity " +
//                     "FROM StockMovementsRequest smr " +
//                     "LEFT JOIN Warehouses w ON smr.FromWarehouseID = w.WareHouseId " +
//                     "LEFT JOIN Branches b ON smr.ToBranchID = b.BranchID " +
//                     "LEFT JOIN Users u ON smr.CreatedBy = u.UserID " +
//                     "LEFT JOIN Users u2 ON smr.ApprovedBy = u2.UserID " +
//                     "WHERE smr.MovementType = 'Export' AND smr.ToBranchID = ? " +
//                     "ORDER BY smr.CreatedAt DESC";
//        
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setInt(1, branchId);
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    Model.StockMovementResponse response = new Model.StockMovementResponse();
//                    response.setMovementID(rs.getInt("MovementID"));
//                    response.setFromWarehouseID(rs.getInt("FromWarehouseID"));
//                    response.setWarehouseName(rs.getString("FromWarehouseName"));
//                    response.setToBranchID(rs.getInt("ToBranchID"));
//                    response.setBranchName(rs.getString("BranchName"));
//                    response.setMovementType(rs.getString("MovementType"));
//                    response.setStatus(rs.getString("Status"));
//                    response.setCreatedBy(rs.getInt("CreatedBy"));
//                    response.setCreatorName(rs.getString("CreatorName"));
//                    response.setCreatedAt(rs.getTimestamp("CreatedAt"));
//                    response.setApprovedBy(rs.getInt("ApprovedBy"));
//                    response.setApproverName(rs.getString("ApproverName"));
//                    response.setApprovedAt(rs.getTimestamp("ApprovedAt"));
//                    response.setNote(rs.getString("Note"));
//                    response.setTotalItems(rs.getInt("TotalItems"));
//                    response.setTotalQuantity(rs.getInt("TotalQuantity"));
//                    list.add(response);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//    
//    /**
//     * Get request details with product list
//     */
//    public Model.StockMovementResponse getRequestDetails(int movementId) {
//        Model.StockMovementResponse response = null;
//        String headerSql = "SELECT smr.MovementID, smr.FromSupplierID, s.SupplierName, " +
//                          "smr.FromWarehouseID, w1.WareHouseName as FromWarehouseName, " +
//                          "smr.ToWarehouseID, w2.WareHouseName as ToWarehouseName, " +
//                          "smr.ToBranchID, b.BranchName, smr.MovementType, " +
//                          "smr.Status, smr.CreatedBy, u.FullName as CreatorName, " +
//                          "smr.CreatedAt, smr.ApprovedBy, u2.FullName as ApproverName, " +
//                          "smr.ApprovedAt, smr.Note " +
//                          "FROM StockMovementsRequest smr " +
//                          "LEFT JOIN Suppliers s ON smr.FromSupplierID = s.SupplierID " +
//                          "LEFT JOIN Warehouses w1 ON smr.FromWarehouseID = w1.WareHouseId " +
//                          "LEFT JOIN Warehouses w2 ON smr.ToWarehouseID = w2.WareHouseId " +
//                          "LEFT JOIN Branches b ON smr.ToBranchID = b.BranchID " +
//                          "LEFT JOIN Users u ON smr.CreatedBy = u.UserID " +
//                          "LEFT JOIN Users u2 ON smr.ApprovedBy = u2.UserID " +
//                          "WHERE smr.MovementID = ?";
//        
//        String detailSql = "SELECT smd.DetailID, smd.ProductDetailID, pd.ProductCode, " +
//                          "pd.ProductNameUnsigned, smd.Quantity, smd.QuantityScanned, " +
//                          "p.CostPrice, p.RetailPrice " +
//                          "FROM StockMovementDetail smd " +
//                          "JOIN ProductDetails pd ON smd.ProductDetailID = pd.ProductDetailID " +
//                          "JOIN Products p ON pd.ProductID = p.ProductID " +
//                          "WHERE smd.MovementID = ?";
//        
//        try (PreparedStatement ps = connection.prepareStatement(headerSql)) {
//            ps.setInt(1, movementId);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    response = new Model.StockMovementResponse();
//                    response.setMovementID(rs.getInt("MovementID"));
//                    response.setFromSupplierID(rs.getInt("FromSupplierID"));
//                    response.setSupplierName(rs.getString("SupplierName"));
//                    response.setFromWarehouseID(rs.getInt("FromWarehouseID"));
//                    response.setWarehouseName(rs.getString("FromWarehouseName"));
//                    response.setToWarehouseID(rs.getInt("ToWarehouseID"));
//                    response.setToWarehouseName(rs.getString("ToWarehouseName"));
//                    response.setToBranchID(rs.getInt("ToBranchID"));
//                    response.setBranchName(rs.getString("BranchName"));
//                    response.setMovementType(rs.getString("MovementType"));
//                    response.setStatus(rs.getString("Status"));
//                    response.setCreatedBy(rs.getInt("CreatedBy"));
//                    response.setCreatorName(rs.getString("CreatorName"));
//                    response.setCreatedAt(rs.getTimestamp("CreatedAt"));
//                    response.setApprovedBy(rs.getInt("ApprovedBy"));
//                    response.setApproverName(rs.getString("ApproverName"));
//                    response.setApprovedAt(rs.getTimestamp("ApprovedAt"));
//                    response.setNote(rs.getString("Note"));
//                    
//                    // Get details
//                    List<Model.StockMovementDetail> details = new ArrayList<>();
//                    try (PreparedStatement ps2 = connection.prepareStatement(detailSql)) {
//                        ps2.setInt(1, movementId);
//                        try (ResultSet rs2 = ps2.executeQuery()) {
//                            while (rs2.next()) {
//                                Model.StockMovementDetail detail = new Model.StockMovementDetail();
//                                detail.setDetailID(rs2.getInt("DetailID"));
//                                detail.setProductDetailID(rs2.getInt("ProductDetailID"));
//                                detail.setProductCode(rs2.getString("ProductCode"));
//                                detail.setProductNameUnsigned(rs2.getString("ProductNameUnsigned"));
//                                detail.setQuantity(rs2.getInt("Quantity"));
//                                detail.setQuantityScanned(rs2.getInt("QuantityScanned"));
//                                detail.setCostPrice(rs2.getDouble("CostPrice"));
//                                detail.setRetailPrice(rs2.getDouble("RetailPrice"));
//                                details.add(detail);
//                            }
//                        }
//                    }
//                    response.setDetails(details);
//                    
//                    // Calculate totals
//                    int totalItems = details.size();
//                    int totalQuantity = details.stream().mapToInt(Model.StockMovementDetail::getQuantity).sum();
//                    double totalAmount = details.stream().mapToDouble(d -> d.getCostPrice() * d.getQuantity()).sum();
//                    response.setTotalItems(totalItems);
//                    response.setTotalQuantity(totalQuantity);
//                    response.setTotalAmount(totalAmount);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return response;
//    }
//    
    /**
     * Approve export request
     */
    public boolean approveRequest(int movementId, int approvedBy) {
        String sql = "UPDATE StockMovementsRequest " +
                    "SET Status = 'Approved', ApprovedBy = ?, ApprovedAt = GETDATE() " +
                    "WHERE MovementID = ? AND MovementType = 'Export' AND Status = 'Pending'";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, approvedBy);
            ps.setInt(2, movementId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reject export request
     */
    public boolean rejectRequest(int movementId, int rejectedBy) {
        String sql = "UPDATE StockMovementsRequest " +
                    "SET Status = 'Rejected', ApprovedBy = ?, ApprovedAt = GETDATE() " +
                    "WHERE MovementID = ? AND MovementType = 'Export' AND Status = 'Pending'";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rejectedBy);
            ps.setInt(2, movementId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all stock movements (both Import and Export) for admin view
     * @param movementType "Import", "Export", or null for all
     * @param page Page number (1-based)
     * @param pageSize Items per page
     * @return List of movements with pagination
     */
    public PagedMoves getAllMovements(String movementType, int page, int pageSize) {
        List<StockMovementsRequest> list = new ArrayList<>();
        int total = 0;
        int offset = (page - 1) * pageSize;
        
        StringBuilder sql = new StringBuilder(
            "SELECT SMR.*, " +
            "S.SupplierName as FromSupplierName, " +
            "FB.BranchName as FromBranchName, " +
            "FW.WarehouseName as FromWarehouseName, " +
            "TB.BranchName as ToBranchName, " +
            "TW.WarehouseName as ToWarehouseName, " +
            "U.FullName as CreatorName " +
            "FROM StockMovementsRequest SMR " +
            "LEFT JOIN Suppliers S ON SMR.FromSupplierID = S.SupplierID " +
            "LEFT JOIN Branches FB ON SMR.FromBranchID = FB.BranchID " +
            "LEFT JOIN Warehouses FW ON SMR.FromWarehouseID = FW.WarehouseID " +
            "LEFT JOIN Branches TB ON SMR.ToBranchID = TB.BranchID " +
            "LEFT JOIN Warehouses TW ON SMR.ToWarehouseID = TW.WarehouseID " +
            "LEFT JOIN Users U ON SMR.CreatedBy = U.UserID "
        );
        
        if (movementType != null && !movementType.isEmpty()) {
            sql.append("WHERE SMR.MovementType = ? ");
        }
        
        sql.append("ORDER BY SMR.CreatedAt DESC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        // Count query
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM StockMovementsRequest");
        if (movementType != null && !movementType.isEmpty()) {
            countSql.append(" WHERE MovementType = ?");
        }
        
        try {
            // Get total count
            try (PreparedStatement ps = connection.prepareStatement(countSql.toString())) {
                if (movementType != null && !movementType.isEmpty()) {
                    ps.setString(1, movementType);
                }
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
            
            // Get items
            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                if (movementType != null && !movementType.isEmpty()) {
                    ps.setString(paramIndex++, movementType);
                }
                ps.setInt(paramIndex++, offset);
                ps.setInt(paramIndex, pageSize);
                
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    StockMovementsRequest req = mapHeader(rs);
                    // Set joined fields for display
                    try {
                        String fromSupplierName = rs.getString("FromSupplierName");
                        if (fromSupplierName != null) req.setFromSupplierName(fromSupplierName);
                    } catch (Exception e) {}
                    
                    try {
                        String fromBranchName = rs.getString("FromBranchName");
                        if (fromBranchName != null) req.setFromBranchName(fromBranchName);
                    } catch (Exception e) {}
                    
                    try {
                        String fromWarehouseName = rs.getString("FromWarehouseName");
                        if (fromWarehouseName != null) req.setFromWarehouseName(fromWarehouseName);
                    } catch (Exception e) {}
                    
                    try {
                        String toBranchName = rs.getString("ToBranchName");
                        if (toBranchName != null) req.setToBranchName(toBranchName);
                    } catch (Exception e) {}
                    
                    try {
                        String toWarehouseName = rs.getString("ToWarehouseName");
                        if (toWarehouseName != null) req.setToWarehouseName(toWarehouseName);
                    } catch (Exception e) {}
                    
                    try {
                        String creatorName = rs.getString("CreatorName");
                        if (creatorName != null) req.setCreatorName(creatorName);
                    } catch (Exception e) {}
                    
                    list.add(req);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new PagedMoves(list, total, page, pageSize);
    }
    
    /**
     * Get stock movements filtered by branch (for branch manager)
     * @param branchId Branch ID to filter
     * @param page Page number
     * @param pageSize Items per page
     * @return List of movements for this branch
     */
    public PagedMoves getMovementsByBranch(int branchId, int page, int pageSize) {
        List<StockMovementsRequest> list = new ArrayList<>();
        int total = 0;
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT SMR.*, " +
                    "FW.WarehouseName as FromWarehouseName, " +
                    "TB.BranchName as ToBranchName, " +
                    "U.FullName as CreatorName " +
                    "FROM StockMovementsRequest SMR " +
                    "LEFT JOIN Warehouses FW ON SMR.FromWarehouseID = FW.WarehouseID " +
                    "LEFT JOIN Branches TB ON SMR.ToBranchID = TB.BranchID " +
                    "LEFT JOIN Users U ON SMR.CreatedBy = U.UserID " +
                    "WHERE SMR.ToBranchID = ? " +
                    "ORDER BY SMR.CreatedAt DESC " +
                    "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        String countSql = "SELECT COUNT(*) FROM StockMovementsRequest WHERE ToBranchID = ?";
        
        try {
            // Get total
            try (PreparedStatement ps = connection.prepareStatement(countSql)) {
                ps.setInt(1, branchId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
            
            // Get items
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, branchId);
                ps.setInt(2, offset);
                ps.setInt(3, pageSize);
                
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    list.add(mapHeader(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new PagedMoves(list, total, page, pageSize);
    }
    
    /**
     * Get movement by ID with all joined names
     * @param movementId Movement ID
     * @return StockMovementsRequest object or null
     */
    public StockMovementsRequest getMovementById(int movementId) {
        String sql = "SELECT SMR.*, " +
                    "S.SupplierName as FromSupplierName, " +
                    "FB.BranchName as FromBranchName, " +
                    "FW.WarehouseName as FromWarehouseName, " +
                    "TB.BranchName as ToBranchName, " +
                    "TW.WarehouseName as ToWarehouseName, " +
                    "U.FullName as CreatorName " +
                    "FROM StockMovementsRequest SMR " +
                    "LEFT JOIN Suppliers S ON SMR.FromSupplierID = S.SupplierID " +
                    "LEFT JOIN Branches FB ON SMR.FromBranchID = FB.BranchID " +
                    "LEFT JOIN Warehouses FW ON SMR.FromWarehouseID = FW.WarehouseID " +
                    "LEFT JOIN Branches TB ON SMR.ToBranchID = TB.BranchID " +
                    "LEFT JOIN Warehouses TW ON SMR.ToWarehouseID = TW.WarehouseID " +
                    "LEFT JOIN Users U ON SMR.CreatedBy = U.UserID " +
                    "WHERE SMR.MovementID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                StockMovementsRequest req = mapHeader(rs);
                // Set display names
                try {
                    req.setFromSupplierName(rs.getString("FromSupplierName"));
                    req.setFromBranchName(rs.getString("FromBranchName"));
                    req.setFromWarehouseName(rs.getString("FromWarehouseName"));
                    req.setToBranchName(rs.getString("ToBranchName"));
                    req.setToWarehouseName(rs.getString("ToWarehouseName"));
                    req.setCreatorName(rs.getString("CreatorName"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return req;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get movement details by movement ID
     * @param movementId Movement ID
     * @return List of detail items
     */
    public List<StockMovementDetail> getMovementDetails(int movementId) {
        List<StockMovementDetail> list = new ArrayList<>();
        
        String sql = "SELECT SMD.*, " +
                    "P.ProductName, " +
                    "PD.ProductCode, " +
                    "P.CostPrice " +
                    "FROM StockMovementDetail SMD " +
                    "JOIN ProductDetails PD ON SMD.ProductDetailID = PD.ProductDetailID " +
                    "JOIN Products P ON PD.ProductID = P.ProductID " +
                    "WHERE SMD.MovementID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                StockMovementDetail detail = new StockMovementDetail();
                detail.setMovementDetailId(rs.getInt("MovementDetailID"));
                detail.setMovementId(rs.getInt("MovementID"));
                detail.setProductDetailId(rs.getInt("ProductDetailID"));
                detail.setQuantity(rs.getInt("Quantity"));
                Object qs = rs.getObject("QuantityScanned");
                if (qs != null) {
                    detail.setQuantityScanned((Integer) qs);
                }
                // Set display fields
                detail.setProductName(rs.getString("ProductName"));
                detail.setProductCode(rs.getString("ProductCode"));
                detail.setCostPrice(rs.getDouble("CostPrice"));
                list.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Create export request from warehouse to branch
     * Used by branch managers to request products from warehouse
     */
    public boolean createExportRequest(int fromWarehouseID, int toBranchID, int createdBy, 
                                      List<Model.ExportCartItem> cartItems, String note) {
        System.out.println("=== [StockMovementDAO] createExportRequest called ===");
        System.out.println("FromWarehouseID: " + fromWarehouseID);
        System.out.println("ToBranchID: " + toBranchID);
        System.out.println("CreatedBy: " + createdBy);
        System.out.println("Cart items: " + (cartItems != null ? cartItems.size() : "NULL"));
        System.out.println("Note: " + note);
        
        String headerSql = "INSERT INTO StockMovementsRequest (FromWarehouseID, ToBranchID, MovementType, CreatedBy, CreatedAt, Note) " +
                          "VALUES (?, ?, 'Export', ?, GETDATE(), ?)";
        String detailSql = "INSERT INTO StockMovementDetail (MovementID, ProductDetailID, Quantity, QuantityScanned) " +
                          "VALUES (?, ?, ?, 0)";
        
        boolean autoCommit = false;
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            System.out.println("Transaction started, auto-commit disabled");
            
            int movementID = -1;
            
            // Insert header
            System.out.println("Inserting header record...");
            try (PreparedStatement ps = connection.prepareStatement(headerSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, fromWarehouseID);
                ps.setInt(2, toBranchID);
                ps.setInt(3, createdBy);
                ps.setString(4, note);
                int affectedRows = ps.executeUpdate();
                System.out.println("Header inserted, affected rows: " + affectedRows);
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        movementID = rs.getInt(1);
                        System.out.println("Generated MovementID: " + movementID);
                    }
                }
            }
            
            if (movementID <= 0) {
                System.err.println("❌ Failed to get MovementID, rolling back");
                connection.rollback();
                return false;
            }
            
            // Insert details
            System.out.println("Inserting detail records...");
            try (PreparedStatement ps = connection.prepareStatement(detailSql)) {
                for (Model.ExportCartItem item : cartItems) {
                    System.out.println("  - Adding batch: ProductDetailID=" + item.getProductDetailID() + ", Quantity=" + item.getQuantity());
                    ps.setInt(1, movementID);
                    ps.setInt(2, item.getProductDetailID());
                    ps.setInt(3, item.getQuantity());
                    ps.addBatch();
                }
                int[] results = ps.executeBatch();
                System.out.println("Details inserted, batch results count: " + results.length);
            }
            
            connection.commit();
            System.out.println("✅ Transaction committed successfully");
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ SQL Error occurred: " + e.getMessage());
            try {
                connection.rollback();
                System.err.println("Transaction rolled back");
            } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException ignored) {}
        }
    }

}
