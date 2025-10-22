package Controller.Admin;

import DAL.WarehouseDAO;
import Model.Warehouse;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "WareHouseManagementController", urlPatterns = {"/WareHouseManagement"})
public class WareHouseManagementController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        WarehouseDAO dao = new WarehouseDAO();
        List<Warehouse> list = dao.getAllWarehouses();
        request.setAttribute("warehouses", list);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/warehouse_management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        WarehouseDAO dao = new WarehouseDAO();

        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("warehouseId"));
            boolean ok = dao.deleteWarehouse(id);
            request.setAttribute("message", ok ? "Xóa kho tổng thành công!" : "Không thể xóa kho tổng!");
            request.setAttribute("msgType", ok ? "success" : "danger");

        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("warehouseId"));
            String name = request.getParameter("warehouseName");
            String address = request.getParameter("address");
            String phone = request.getParameter("phone");
            boolean isActive = request.getParameter("isActive") != null;

            Warehouse w = new Warehouse(id, name, address, phone, isActive);
            boolean ok = dao.updateWarehouse(w);
            request.setAttribute("message", ok ? "Cập nhật thông tin kho thành công!" : "Cập nhật thất bại!");
            request.setAttribute("msgType", ok ? "success" : "danger");
        }

        List<Warehouse> list = dao.getAllWarehouses();
        request.setAttribute("warehouses", list);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/warehouse_management.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Quản lý kho tổng (hiển thị, sửa, xóa)";
    }
}
