package Controller.WareHouse;

import DAL.StockMovementDetailDAO;
import DAL.StockMovementsRequestDAO;
import DAL.StockMovementResponseDAO;
import Model.StockMovementResponse;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Date;
import java.sql.*;

/**
 * Xử lý khi quản lý kho xác nhận hoàn tất phiếu nhập hàng URL:
 * /wh-complete-request?id=...
 */
@WebServlet(name = "WHCompleteRequestController", urlPatterns = {"/wh-complete-request"})
public class WHCompleteRequestController extends HttpServlet {

    private final StockMovementsRequestDAO requestDAO = new StockMovementsRequestDAO();
    private final StockMovementResponseDAO responseDAO = new StockMovementResponseDAO();

    @Override   
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String idParam = request.getParameter("id");
            String movementType = request.getParameter("movementType");
            if (movementType == null || movementType.isEmpty()) {
                movementType = "import";
            }
            
            if (idParam == null || idParam.isBlank()) {
                if ("export".equalsIgnoreCase(movementType)) {
                    response.sendRedirect("warehouse-export-orders");
                } else {
                    response.sendRedirect("wh-import");
                }
                return;
            }
            int movementId = Integer.parseInt(idParam);

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userID") == null) {
                response.sendRedirect("login"); return;
            }
            int userId = (int) session.getAttribute("userID");

            // Chưa quét đủ thì chặn
            StockMovementDetailDAO detailDAO = new StockMovementDetailDAO();
            if (!detailDAO.isAllDetailsCompleted(movementId)) {
                request.getSession().setAttribute("errorMessage",
                        "Chưa đủ serial cho tất cả sản phẩm.");
                String redirectUrl = "serial-check?id=" + movementId + "&movementType=" + movementType;
                response.sendRedirect(redirectUrl);
                return;
            }

            // Đã completed rồi thì thôi
            if (responseDAO.hasCompletedStatus(movementId)) {
                String msg = "export".equalsIgnoreCase(movementType) 
                    ? "Đơn xuất #" + movementId + " đã hoàn thành trước đó."
                    : "Đơn nhập #" + movementId + " đã hoàn thành trước đó.";
                request.getSession().setAttribute("successMessage", msg);
                String redirectUrl = "serial-check?id=" + movementId + "&movementType=" + movementType;
                response.sendRedirect(redirectUrl);
                return;
            }

            // Ghi 1 bản ghi completed vào responses
            StockMovementResponse smr = new StockMovementResponse();
            smr.setMovementId(movementId);
            smr.setResponsedBy(userId);
            smr.setResponseAt(new Date());
            smr.setResponseStatus("Hoàn thành");
            smr.setNote("export".equalsIgnoreCase(movementType) 
                ? "Kho xác nhận hoàn tất xuất." 
                : "Kho xác nhận hoàn tất nhập.");
            boolean ok = new StockMovementResponseDAO().insertStockMovementResponse(smr);

            if (ok) {
                DAL.DataBaseContext db = new DAL.DataBaseContext();
                Connection conn = db.connection;
                
                if ("export".equalsIgnoreCase(movementType)) {
                    // EXPORT: Update serials, decrease warehouse stock, increase branch inventory
                    handleExportCompletion(conn, movementId);
                    request.getSession().setAttribute("successMessage", "✅ Đơn xuất hàng #" + movementId + " đã hoàn tất!");
                } else {
                    // IMPORT: Update serials to destination warehouse
                    Integer toWarehouseId = requestDAO.getToWarehouseIdByMovementId(movementId);
                    if (toWarehouseId != null) {
                        final String sql =
                            "UPDATE s " +
                            "SET s.WarehouseID = ?, " +
                            "    s.BranchID    = NULL, " +
                            "    s.OrderID     = NULL, " +
                            "    s.Status      = 1 " +
                            "FROM ProductDetailSerialNumber s " +
                            "JOIN StockMovementDetail d ON s.MovementDetailID = d.MovementDetailID " +
                            "WHERE d.MovementID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, toWarehouseId);
                            ps.setInt(2, movementId);
                            ps.executeUpdate();
                        }
                    }
                    request.getSession().setAttribute("successMessage", "✅ Đơn nhập hàng #" + movementId + " đã hoàn tất!");
                }
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể lưu phản hồi hoàn tất đơn hàng.");
            }

            String redirectUrl = "serial-check?id=" + movementId + "&movementType=" + movementType;
            response.sendRedirect(redirectUrl);

        } catch (Exception ex) {
            ex.printStackTrace();
            String movementType = request.getParameter("movementType");
            request.getSession().setAttribute("errorMessage", "Lỗi khi hoàn tất: " + ex.getMessage());
            if ("export".equalsIgnoreCase(movementType)) {
                response.sendRedirect("warehouse-export-orders");
            } else {
                response.sendRedirect("wh-import");
            }
        }
    }
    
    /**
     * Handle export completion: update serials, decrease warehouse stock, increase branch inventory
     */
    private void handleExportCompletion(Connection conn, int movementId) throws SQLException {
        // Get movement request details
        String getMovementSql = "SELECT FromWarehouseID, ToBranchID FROM StockMovementsRequest WHERE MovementID = ?";
        Integer fromWarehouseId = null;
        Integer toBranchId = null;
        
        try (PreparedStatement ps = conn.prepareStatement(getMovementSql)) {
            ps.setInt(1, movementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    fromWarehouseId = (Integer) rs.getObject("FromWarehouseID");
                    toBranchId = (Integer) rs.getObject("ToBranchID");
                }
            }
        }
        
        if (fromWarehouseId == null) {
            throw new SQLException("Không tìm thấy kho xuất cho đơn #" + movementId);
        }
        
        // Get all movement details
        String getDetailsSql = """
            SELECT smd.MovementDetailID, smd.ProductDetailID, smd.Quantity
            FROM StockMovementDetail smd
            WHERE smd.MovementID = ?
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(getDetailsSql)) {
            ps.setInt(1, movementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int movementDetailId = rs.getInt("MovementDetailID");
                    int productDetailId = rs.getInt("ProductDetailID");
                    int quantity = rs.getInt("Quantity");
                    
                    // 1. Update serials: set WarehouseID = NULL, set BranchID = toBranchId, update MovementHistory
                    String updateSerialSql = """
                        UPDATE s
                        SET s.WarehouseID = NULL,
                            s.BranchID = ?,
                            s.MovementHistory = CONCAT(ISNULL(s.MovementHistory, ''), 
                                CHAR(13) + CHAR(10) + CONVERT(VARCHAR(19), GETDATE(), 120) + 
                                ': Xuất từ Kho #' + CAST(? AS VARCHAR) + ' đến Chi nhánh #' + CAST(? AS VARCHAR))
                        FROM ProductDetailSerialNumber s
                        WHERE s.MovementDetailID = ?
                    """;
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSerialSql)) {
                        updatePs.setObject(1, toBranchId);
                        updatePs.setInt(2, fromWarehouseId);
                        updatePs.setObject(3, toBranchId);
                        updatePs.setInt(4, movementDetailId);
                        updatePs.executeUpdate();
                    }
                    
                    // 2. Decrease WarehouseProducts quantity
                    String decreaseWarehouseSql = """
                        UPDATE WarehouseProducts
                        SET Quantity = Quantity - ?
                        WHERE WarehouseID = ? AND ProductDetailID = ?
                    """;
                    try (PreparedStatement decPs = conn.prepareStatement(decreaseWarehouseSql)) {
                        decPs.setInt(1, quantity);
                        decPs.setInt(2, fromWarehouseId);
                        decPs.setInt(3, productDetailId);
                        decPs.executeUpdate();
                    }
                    
                    // 3. Increase InventoryProducts quantity (if exporting to branch)
                    if (toBranchId != null) {
                        // Get or create Inventory for branch
                        String getInventorySql = "SELECT InventoryID FROM Inventory WHERE BranchID = ?";
                        Integer inventoryId = null;
                        try (PreparedStatement invPs = conn.prepareStatement(getInventorySql)) {
                            invPs.setInt(1, toBranchId);
                            try (ResultSet invRs = invPs.executeQuery()) {
                                if (invRs.next()) {
                                    inventoryId = invRs.getInt("InventoryID");
                                } else {
                                    // Create inventory for branch
                                    String createInventorySql = "INSERT INTO Inventory (BranchID) OUTPUT INSERTED.InventoryID VALUES (?)";
                                    try (PreparedStatement createPs = conn.prepareStatement(createInventorySql)) {
                                        createPs.setInt(1, toBranchId);
                                        try (ResultSet createRs = createPs.executeQuery()) {
                                            if (createRs.next()) {
                                                inventoryId = createRs.getInt("InventoryID");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        if (inventoryId != null) {
                            // Update or insert InventoryProducts
                            String updateInventorySql = """
                                IF EXISTS (SELECT 1 FROM InventoryProducts WHERE InventoryID = ? AND ProductDetailID = ?)
                                    UPDATE InventoryProducts SET Quantity = Quantity + ? 
                                    WHERE InventoryID = ? AND ProductDetailID = ?
                                ELSE
                                    INSERT INTO InventoryProducts (InventoryID, ProductDetailID, Quantity) 
                                    VALUES (?, ?, ?)
                            """;
                            try (PreparedStatement invUpdPs = conn.prepareStatement(updateInventorySql)) {
                                invUpdPs.setInt(1, inventoryId);
                                invUpdPs.setInt(2, productDetailId);
                                invUpdPs.setInt(3, quantity);
                                invUpdPs.setInt(4, inventoryId);
                                invUpdPs.setInt(5, productDetailId);
                                invUpdPs.setInt(6, inventoryId);
                                invUpdPs.setInt(7, productDetailId);
                                invUpdPs.setInt(8, quantity);
                                invUpdPs.executeUpdate();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Xác nhận hoàn tất đơn nhập/xuất hàng tại kho";
    }
}
