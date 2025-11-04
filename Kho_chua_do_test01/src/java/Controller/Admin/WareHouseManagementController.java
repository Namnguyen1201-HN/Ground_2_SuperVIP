package Controller.Admin;

import DAL.WarehouseDAO;
import Model.Warehouse;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "WareHouseManagementController", urlPatterns = {"/WareHouseManagement"})
public class WareHouseManagementController extends HttpServlet {

    private static final int PAGE_SIZE = 5; // chỉnh theo ý bạn
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        WarehouseDAO dao = new WarehouseDAO();
        
        // Lấy page hiện tại
        int page = 1;
        String pageParam = req.getParameter("page");
        if (pageParam != null) {
            try { page = Math.max(1, Integer.parseInt(pageParam)); } catch (NumberFormatException ignored) {}
        }

        int totalItems = dao.countWarehouses();
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        List<Warehouse> warehouses = dao.getWarehousesPaged(page, PAGE_SIZE);

        req.setAttribute("warehouses", warehouses);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("pageSize", PAGE_SIZE);
        req.setAttribute("totalItems", totalItems);

        req.getRequestDispatcher("/WEB-INF/jsp/admin/warehouse_management.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        WarehouseDAO dao = new WarehouseDAO();
        String action = req.getParameter("action");

        // Lấy page để redirect về đúng trang
        String page = req.getParameter("page");
        String pageSuffix = (page != null && !page.isBlank()) ? "&page=" + page : "";
        
        if (action == null || action.isEmpty()) {
            resp.sendRedirect("WareHouseManagement");
            return;
        }

        try {
            switch (action) {
                case "delete": {
                    int id = Integer.parseInt(req.getParameter("warehouseId")); // name="warehouseId" trong JSP
                    boolean ok = dao.deleteWarehouse(id);
                    resp.sendRedirect("WareHouseManagement?" + (ok ? "success=delete" : "error=delete_failed"));
                    break;
                }
                case "update": {
                    int id = Integer.parseInt(req.getParameter("warehouseId"));
                    String name = req.getParameter("warehouseName");
                    String address = req.getParameter("address");
                    String phone = req.getParameter("phone");
                    boolean active = req.getParameter("isActive") != null;

                    if (name == null || address == null || phone == null
                            || name.trim().isEmpty() || address.trim().isEmpty() || phone.trim().isEmpty()) {
                        resp.sendRedirect("WareHouseManagement?error=empty_fields");
                        return;
                    }

                    // SĐT: bắt đầu bằng 0, tổng 9–11 số
                    if (!phone.matches("^0\\d{8,10}$")) {
                        resp.sendRedirect("WareHouseManagement?error=invalid_phone");
                        return;
                    }

                    Warehouse old = dao.getWarehouseById(id);
                    if (old != null && !old.getPhone().equals(phone) && dao.isPhoneExists(phone)) {
                        resp.sendRedirect("WareHouseManagement?error=duplicate_phone");
                        return;
                    }

                    Warehouse w = new Warehouse();
                    w.setWarehouseId(id);
                    w.setWarehouseName(name.trim());
                    w.setAddress(address.trim());
                    w.setPhone(phone.trim());
                    w.setActive(active);

                    boolean ok = dao.updateWarehouse(w);
                    resp.sendRedirect("WareHouseManagement?" + (ok ? "success=update" : "error=update_failed"));
                    break;
                }
                default:
                    resp.sendRedirect("WareHouseManagement?error=invalid_action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("WareHouseManagement?error=exception");
        }
    }
}
