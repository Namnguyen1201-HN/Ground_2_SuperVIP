package Controller.WareHouse;

import DAL.StockMovementDAO;
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

        HttpSession session = request.getSession(false);
        Integer warehouseId = (session != null) ? (Integer) session.getAttribute("warehouseId") : null;

        if (warehouseId == null) {
            request.setAttribute("error", "Không xác định được kho. Vui lòng đăng nhập lại!");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
            return;
        }

        // Lọc thời gian
        String fromStr = request.getParameter("fromDate");
        String toStr = request.getParameter("toDate");
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

        StockMovementDAO dao = new StockMovementDAO();
        List<Map<String, Object>> importOrders = dao.listImportOrders(warehouseId, from, to, page, 10);

        request.setAttribute("importOrders", importOrders);
        request.setAttribute("warehouseId", warehouseId);

        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/NhapHang.jsp").forward(request, response);
    }
    
}
