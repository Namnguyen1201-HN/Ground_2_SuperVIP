package Controller.WareHouse;

import DAL.StockMovementDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@WebServlet(name = "XuatHangController", urlPatterns = {"/XuatHang"})
public class XuatHangController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);

        Integer warehouseId = (session != null) ? (Integer) session.getAttribute("warehouseId") : null;
        if (warehouseId == null) {
            request.setAttribute("error", "Không xác định được kho, vui lòng đăng nhập lại!");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
            return;
        }

        // Nhận tham số lọc
        String fromStr = request.getParameter("fromDate");
        String toStr = request.getParameter("toDate");
        String status = request.getParameter("status");
        String pageStr = request.getParameter("page");

        Timestamp from = null, to = null;
        try {
            if (fromStr != null && !fromStr.isEmpty()) {
                from = Timestamp.valueOf(fromStr + " 00:00:00");
            }
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

        // Gọi DAO
        StockMovementDAO dao = new StockMovementDAO();
        List<Map<String, Object>> exportOrders = dao.listExportOrders(warehouseId, from, to, status, page, 10);

        // Gửi dữ liệu sang JSP
        request.setAttribute("exportOrders", exportOrders);
        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/XuatHang.jsp").forward(request, response);
    }
    
}
