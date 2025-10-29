package Controller.Admin;

import DAL.WarehouseDAO;
import Model.Warehouse;
import java.io.IOException;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "WareHouseCreateController", urlPatterns = {"/WareHouseCreate"})
public class WareHouseCreateController extends HttpServlet {

    private static final Pattern PHONE_REGEX = Pattern.compile("^0\\d{8,10}$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/admin/warehouse_create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String name = safe(req.getParameter("warehouseName"));
        String address = safe(req.getParameter("address"));
        String phone = safe(req.getParameter("phone"));
        boolean active = req.getParameter("isActive") != null;

        WarehouseDAO dao = new WarehouseDAO();
        
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            setMsg(req, "Vui lòng nhập đầy đủ thông tin!", "warning");
            forward(req, resp);
            return;
        }

        if (!PHONE_REGEX.matcher(phone).matches()) {
            setMsg(req, "Số điện thoại không hợp lệ! (Bắt đầu bằng 0 và 9–11 số)", "danger");
            forward(req, resp);
            return;
        }

        // ✅ Kiểm tra trùng số điện thoại
        if (dao.isPhoneExists(phone)) {
            setMsg(req, "📞 Số điện thoại đã tồn tại, vui lòng nhập số khác!", "danger");
            forward(req, resp);
            return;
        }

        Warehouse w = new Warehouse();
        w.setWarehouseName(name);
        w.setAddress(address);
        w.setPhone(phone);
        w.setActive(active);

        
        boolean success = dao.insertWarehouse(w);
        if (success) {
            resp.sendRedirect("WareHouseManagement?success=create");
        } else {
            setMsg(req, "❌ Thêm kho thất bại. Vui lòng thử lại!", "danger");
            forward(req, resp);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private void setMsg(HttpServletRequest req, String msg, String type) {
        req.setAttribute("message", msg);
        req.setAttribute("msgType", type);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/admin/warehouse_create.jsp").forward(req, resp);
    }
}
