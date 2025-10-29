package Controller.Admin;

import DAL.BranchDAO;
import Model.Branch;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.regex.Pattern;

@WebServlet(name = "BranchCreateController", urlPatterns = {"/BranchCreate"})
public class BranchCreateController extends HttpServlet {

    private static final Pattern PHONE_REGEX = Pattern.compile("^0\\d{8,10}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/admin/branch_create.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        BranchDAO dao = new BranchDAO();

        String name = safe(request.getParameter("branchName"));
        String address = safe(request.getParameter("address"));
        String phone = safe(request.getParameter("phone"));

        // Validate cơ bản
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            setMsg(request, "Vui lòng nhập đầy đủ thông tin!", "warning");
            forward(request, response);
            return;
        }

        // Validate định dạng SĐT
        if (!PHONE_REGEX.matcher(phone).matches()) {
            setMsg(request, "Số điện thoại không hợp lệ! (9–11 chữ số và bắt đầu bằng số 0)", "danger");
            forward(request, response);
            return;
        }

        if (dao.isPhoneExists(phone)) {
            setMsg(request, "📞 Số điện thoại đã tồn tại, vui lòng nhập số khác!", "danger");
            forward(request, response);
            return;
        }

        // Check trùng
        if (dao.isBranchNameExists(name)) {
            setMsg(request, "Tên chi nhánh đã tồn tại!", "danger");
            forward(request, response);
            return;
        }

        if (dao.isPhoneExists(phone)) {
            setMsg(request, "Số điện thoại đã tồn tại trong hệ thống!", "danger");
            forward(request, response);
            return;
        }

        // Tạo mới chi nhánh
        Branch b = new Branch();
        b.setBranchName(name);
        b.setAddress(address);
        b.setPhone(phone);

        boolean success = dao.insertBranch(b);
        if (success) {
            response.sendRedirect("BranchManagement?success=create");
        } else {
            setMsg(request, "Thêm chi nhánh thất bại. Vui lòng thử lại!", "danger");
            forward(request, response);
        }
    }

    // === Helper ===
    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private void setMsg(HttpServletRequest req, String msg, String type) {
        req.setAttribute("message", msg);
        req.setAttribute("msgType", type);
    }

    private void forward(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/admin/branch_create.jsp").forward(req, res);
    }
}
