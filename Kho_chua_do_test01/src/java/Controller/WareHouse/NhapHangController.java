package Controller.WareHouse;

import DAL.StockMovementDAO;
import DAL.StockMovementDAO.PagedMoves;
import Model.StockMovementsRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@WebServlet(name = "NhapHangController", urlPatterns = {"/NhapHang"})
public class NhapHangController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();

        // ✅ Giả sử đã đăng nhập (test tạm)
        Integer warehouseId = (Integer) session.getAttribute("warehouseId");
        if (warehouseId == null) {
            warehouseId = 1;
        }

        String fromStr = request.getParameter("fromDate");
        String toStr = request.getParameter("toDate");
        String status = request.getParameter("status");
        String pageStr = request.getParameter("page");

        Timestamp from = null, to = null;
        try {
            if (fromStr != null && !fromStr.isEmpty()) {
                from = Timestamp.valueOf(fromStr + " 00:00:00");
            }
        } catch (Exception ignored) {
        }
        try {
            if (toStr != null && !toStr.isEmpty()) {
                to = Timestamp.valueOf(toStr + " 23:59:59");
            }
        } catch (Exception ignored) {
        }
        int page = 1;
        try {
            page = Integer.parseInt(pageStr);
        } catch (Exception ignored) {
        }

        StockMovementDAO dao = new StockMovementDAO();
        // chỉ lấy những đơn nhập hàng (MovementType = 'Import')
        PagedMoves result = dao.list("Import", null, warehouseId, from, to, page, 10);
        List<Map<String, Object>> importOrders = dao.listImportOrders(warehouseId, from, to, page, 10);
        
        request.setAttribute("result", result);
        request.setAttribute("fromDate", fromStr);
        request.setAttribute("toDate", toStr);
        request.setAttribute("importOrders", importOrders);
        
        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/NhapHang.jsp").forward(request, response);
    }
    
}
