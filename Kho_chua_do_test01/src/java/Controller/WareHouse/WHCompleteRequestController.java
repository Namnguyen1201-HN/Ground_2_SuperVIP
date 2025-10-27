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
            if (idParam == null || idParam.isBlank()) {
                response.sendRedirect("wh-import");
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
                response.sendRedirect("wh-import-export-detail?id=" + movementId);
                return;
            }

            // Đã completed rồi thì thôi
            if (responseDAO.hasCompletedStatus(movementId)) {
                request.getSession().setAttribute("successMessage",
                        "Đơn nhập #" + movementId + " đã hoàn thành trước đó.");
                response.sendRedirect("wh-import-export-detail?id=" + movementId);
                return;
            }

            // Ghi 1 bản ghi completed vào responses
            StockMovementResponse smr = new StockMovementResponse();
            smr.setMovementId(movementId);
            smr.setResponsedBy(userId);
            smr.setResponseAt(new Date());
            smr.setResponseStatus("completed");
            smr.setNote("Kho xác nhận hoàn tất nhập.");
            boolean ok = new StockMovementResponseDAO().insertStockMovementResponse(smr);

            if (ok) {
                // Gán WarehouseID cho tất cả serial thuộc phiếu => về kho đích
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
                    try (PreparedStatement ps = new DAL.DataBaseContext().connection.prepareStatement(sql)) {
                        ps.setInt(1, toWarehouseId);
                        ps.setInt(2, movementId);
                        ps.executeUpdate();
                    }
                }
                request.getSession().setAttribute("successMessage", "✅ Đơn nhập hàng #" + movementId + " đã hoàn tất!");
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể lưu phản hồi hoàn tất đơn hàng.");
            }

            response.sendRedirect("wh-import-export-detail?id=" + movementId);

        } catch (Exception ex) {
            ex.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Lỗi khi hoàn tất: " + ex.getMessage());
            response.sendRedirect("wh-import");
        }
    }

    @Override
    public String getServletInfo() {
        return "Xác nhận hoàn tất đơn nhập hàng tại kho";
    }
}
