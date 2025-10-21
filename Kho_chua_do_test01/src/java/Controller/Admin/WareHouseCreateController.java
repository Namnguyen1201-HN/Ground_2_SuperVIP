package Controller.Admin;

import DAL.WarehouseDAO;
import Model.Warehouse;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "WareHouseCreateController", urlPatterns = {"/WareHouseCreate"})
public class WareHouseCreateController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị trang tạo mới
        request.getRequestDispatcher("/WEB-INF/jsp/admin/warehouse_create.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("warehouseName");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");

        // ⚙️ Kiểm tra dữ liệu đầu vào
        if (name == null || name.trim().isEmpty()
                || address == null || address.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()) {

            request.setAttribute("message", "Vui lòng nhập đầy đủ thông tin kho tổng!");
            request.setAttribute("msgType", "danger");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/warehouse_create.jsp").forward(request, response);
            return;
        }

        Warehouse w = new Warehouse();
        w.setWarehouseName(name.trim());
        w.setAddress(address.trim());
        w.setPhone(phone.trim());
        w.setActive(true); // Mặc định kho mới là hoạt động

        WarehouseDAO dao = new WarehouseDAO();
        boolean success = dao.insertWarehouse(w);

        if (success) {
            // ✅ Lưu thành công, chuyển hướng về trang danh sách
            request.getSession().setAttribute("flashMessage", "Tạo kho tổng mới thành công!");
            request.getSession().setAttribute("flashType", "success");
            response.sendRedirect("WareHouseManagement");
        } else {
            // ❌ Lưu thất bại
            request.setAttribute("message", "Không thể tạo kho tổng. Vui lòng thử lại!");
            request.setAttribute("msgType", "danger");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/warehouse_create.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Tạo kho tổng mới";
    }
}
