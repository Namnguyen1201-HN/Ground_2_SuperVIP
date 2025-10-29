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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        WarehouseDAO dao = new WarehouseDAO();
        List<Warehouse> warehouses = dao.getAllWarehouses();
        req.setAttribute("warehouses", warehouses);

        req.getRequestDispatcher("/WEB-INF/jsp/admin/warehouse_management.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        WarehouseDAO dao = new WarehouseDAO();
        String action = req.getParameter("action");

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
