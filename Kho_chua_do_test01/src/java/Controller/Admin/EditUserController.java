package Controller.Admin;

import DAL.*;
import Model.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet(name = "EditUserController", urlPatterns = {"/EditUser"})
public class EditUserController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final BranchDAO branchDAO = new BranchDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();
    private final ShiftDAO shiftDAO = new ShiftDAO();

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^\\d{9,11}$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        int id = parseInt(req.getParameter("userId"));
        if (id <= 0) {
            res.sendRedirect("NhanVien?error=invalid_id");
            return;
        }

        User user = userDAO.getUserById(id);
        if (user == null) {
            res.sendRedirect("NhanVien?error=user_not_found");
            return;
        }

        user.setShiftID(userDAO.getShiftIdByUserId(id));
        req.setAttribute("user", user);
        req.setAttribute("roles", roleDAO.getAllRoles());
        req.setAttribute("branches", branchDAO.getAllBranches());
        req.setAttribute("warehouses", warehouseDAO.getAllWarehouses());
        req.setAttribute("shifts", shiftDAO.getAll());

        req.getRequestDispatcher("/WEB-INF/jsp/admin/EditUser.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = safe(req.getParameter("action"));

        if ("delete".equals(action)) {
            handleDelete(req, res);
        } else if ("update".equals(action)) {
            handleUpdate(req, res);
        } else {
            res.sendRedirect("NhanVien?error=invalid_action");
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int id = parseInt(req.getParameter("userId"));
        boolean ok = (id > 0) && userDAO.deleteUser(id);
        res.sendRedirect(ok ? "NhanVien?success=delete" : "NhanVien?error=delete_failed");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        int id = parseInt(req.getParameter("userID"));
        User u = userDAO.getUserById(id);
        if (u == null) {
            res.sendRedirect("NhanVien?error=user_not_found");
            return;
        }

        String name = safe(req.getParameter("fullName"));
        String email = safe(req.getParameter("email"));
        String phone = safe(req.getParameter("phone"));

        // --- VALIDATE ---
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập đầy đủ thông tin!");
            reloadForm(req, res, u);
            return;
        }
        if (!EMAIL_REGEX.matcher(email).matches()) {
            req.setAttribute("error", "Email không hợp lệ!");
            reloadForm(req, res, u);
            return;
        }
        if (!PHONE_REGEX.matcher(phone).matches()) {
            req.setAttribute("error", "Số điện thoại không hợp lệ!");
            reloadForm(req, res, u);
            return;
        }

        // --- Validate trùng lặp ---
        if (userDAO.isEmailExists(email) && !email.equalsIgnoreCase(u.getEmail())) {
            req.setAttribute("error", "Email đã tồn tại!");
            reloadForm(req, res, u);
            return;
        }
        if (userDAO.isPhoneExists(phone) && !phone.equals(u.getPhone())) {
            req.setAttribute("error", "Số điện thoại đã tồn tại!");
            reloadForm(req, res, u);
            return;
        }

        // --- GÁN DỮ LIỆU ---
        u.setFullName(name);
        u.setEmail(email);
        u.setPhone(phone);
        u.setAddress(safe(req.getParameter("address")));
        u.setGender("Nam".equalsIgnoreCase(req.getParameter("gender")));

        String dob = safe(req.getParameter("dob"));
        if (!dob.isEmpty()) {
            u.setDob(Date.valueOf(dob));
        }

        u.setRoleId(parseInt(req.getParameter("roleID")));
        u.setIsActive(parseInt(req.getParameter("isActive")));

        if (u.getRoleId() == 3) { // Quản lý kho
            u.setWarehouseId(parseNullable(req.getParameter("warehouseID")));
            u.setBranchId(null);
        } else {
            u.setBranchId(parseNullable(req.getParameter("branchID")));
            u.setWarehouseId(null);
        }

        boolean updated = userDAO.updateUser(u);

        String shift = safe(req.getParameter("shiftID"));
        if (!shift.isEmpty()) {
            userDAO.updateUserShift(id, parseInt(shift));
        } else {
            userDAO.deleteUserShift(id);
        }

        if (updated) {
            res.sendRedirect("NhanVien?success=update");
        } else {
            req.setAttribute("error", "Cập nhật thất bại! Vui lòng thử lại.");
            reloadForm(req, res, u);
        }
    }

    // --- Helper ---
    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private Integer parseNullable(String s) {
        try {
            return (s == null || s.trim().isEmpty()) ? null : Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private void redirect(HttpServletResponse res, String err) throws IOException {
        res.sendRedirect("NhanVien?error=" + err);
    }

    private void reloadForm(HttpServletRequest req, HttpServletResponse res, User user)
            throws ServletException, IOException {

        req.setAttribute("user", user);
        req.setAttribute("roles", roleDAO.getAllRoles());
        req.setAttribute("branches", branchDAO.getAllBranches());
        req.setAttribute("warehouses", warehouseDAO.getAllWarehouses());
        req.setAttribute("shifts", shiftDAO.getAll());

        req.getRequestDispatcher("/WEB-INF/jsp/admin/EditUser.jsp").forward(req, res);
    }

    @Override
    public String getServletInfo() {
        return "EditUserController optimized with input validation";
    }
}
