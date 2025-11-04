package Controller.Admin;

import DAL.*;
import Model.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet(name = "EditUserController", urlPatterns = {"/EditUser"})
public class EditUserController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final BranchDAO branchDAO = new BranchDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();
    private final ShiftDAO shiftDAO = new ShiftDAO();

    // Regex chuẩn hóa
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^0\\d{9}$");   // 10 số, bắt đầu bằng 0
    private static final Pattern CCCD_REGEX = Pattern.compile("^\\d{12}$");   // đúng 12 số

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

        // nạp dữ liệu cho form
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

        switch (action == null ? "" : action) {
            case "delete":
                handleDelete(req, res);
                break;
            case "update":
                handleUpdate(req, res);
                break;
            default:
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
        User current = userDAO.getUserById(id);
        if (current == null) {
            res.sendRedirect("NhanVien?error=user_not_found");
            return;
        }

        // --- Lấy input & chuẩn hóa ---
        String name = safe(req.getParameter("fullName"));
        String email = safe(req.getParameter("email"));
        String phone = safe(req.getParameter("phone"));
        String addr = safe(req.getParameter("address"));
        String gender = safe(req.getParameter("gender"));       // "1"/"0" hoặc "Nam"/"Nữ"
        String dobStr = safe(req.getParameter("dob"));
        String cccd = safe(req.getParameter("identificationId"));

        Integer roleId = parseNullable(req.getParameter("roleID"));
        Integer isActive = parseNullable(req.getParameter("isActive"));
        Integer branchId = parseNullable(req.getParameter("branchID"));
        Integer warehouseId = parseNullable(req.getParameter("warehouseID"));
        Integer shiftId = parseNullable(req.getParameter("shiftID"));

        List<String> errors = new ArrayList<>();

        // --- VALIDATE: bắt buộc ---
        if (name.isEmpty()) {
            errors.add("Họ và tên không được để trống.");
        }
        if (email.isEmpty()) {
            errors.add("Email không được để trống.");
        }
        if (phone.isEmpty()) {
            errors.add("Số điện thoại không được để trống.");
        }

        // --- VALIDATE: định dạng ---
        if (!email.isEmpty() && !EMAIL_REGEX.matcher(email).matches()) {
            errors.add("Email không hợp lệ.");
        }
        if (!phone.isEmpty() && !PHONE_REGEX.matcher(phone).matches()) {
            errors.add("Số điện thoại phải có 10 chữ số và bắt đầu bằng 0.");
        }

        // CCCD (nếu bạn muốn bắt buộc thì thêm điều kiện isEmpty bên trên)
        if (!cccd.isEmpty() && !CCCD_REGEX.matcher(cccd).matches()) {
            errors.add("CCCD phải gồm đúng 12 chữ số.");
        }

        // --- VALIDATE: trùng lặp (chỉ check nếu khác giá trị cũ) ---
        if (!email.isEmpty()
                && !email.equalsIgnoreCase(current.getEmail())
                && userDAO.isEmailExists(email)) {
            errors.add("Email đã tồn tại trong hệ thống.");
        }

        if (!phone.isEmpty()
                && !phone.equals(current.getPhone())
                && userDAO.isPhoneExists(phone)) {
            errors.add("Số điện thoại đã tồn tại trong hệ thống.");
        }

        if (!cccd.isEmpty()) {
            String oldCccd = safe(current.getIdentificationId());
            if (!cccd.equals(oldCccd) && userDAO.isIdentificationIdExists(cccd)) {
                errors.add("CCCD đã tồn tại trong hệ thống.");
            }
        }

        // --- Nếu có lỗi -> trả về form ---
        if (!errors.isEmpty()) {
            req.setAttribute("error", String.join("<br/>", errors));

            // giữ lại giá trị người dùng vừa nhập
            current.setFullName(name);
            current.setEmail(email);
            current.setPhone(phone);
            current.setAddress(addr);
            if (!gender.isEmpty()) {
                current.setGender(parseGender(gender));
            }
            if (!dobStr.isEmpty()) {
                safeSetDob(current, dobStr);
            }
            if (roleId != null) {
                current.setRoleId(roleId);
            }
            if (isActive != null) {
                current.setIsActive(isActive);
            }

            // logic branch/kho theo role (giữ nhất quán UI của bạn)
            if (roleId != null && roleId == 3) { // 3 = Quản lý kho
                current.setWarehouseId(warehouseId);
                current.setBranchId(null);
            } else {
                current.setBranchId(branchId);
                current.setWarehouseId(null);
            }
            // CCCD
            current.setIdentificationId(cccd);

            reloadForm(req, res, current);
            return;
        }

        int effectiveRoleId = (roleId != null) ? roleId : current.getRoleId();

        // --- Gán dữ liệu hợp lệ vào current ---
        current.setFullName(name);
        current.setEmail(email);
        current.setPhone(phone);
        current.setAddress(addr);
        current.setIdentificationId(cccd);
        if (!gender.isEmpty()) {
            current.setGender(parseGender(gender));
        }
        if (!dobStr.isEmpty()) {
            safeSetDob(current, dobStr);
        }

        if (roleId != null) {
            current.setRoleId(roleId);
        }
        if (isActive != null) {
            current.setIsActive(isActive);
        }

        // Quy tắc Branch/Kho theo role hiệu lực
        if (effectiveRoleId == 3) { // 3 = Quản lý kho
            current.setWarehouseId(warehouseId); // dùng biến đã parse
            current.setBranchId(null);
        } else {
            current.setBranchId(branchId);
            current.setWarehouseId(null);
        }

        boolean updated = userDAO.updateUser(current);

        // --- Cập nhật ca làm (nếu có) ---
        if (shiftId != null && shiftId > 0) {
            userDAO.updateUserShift(id, shiftId);
        } else {
            userDAO.deleteUserShift(id);
        }

        if (updated) {
            res.sendRedirect("NhanVien?success=update");
        } else {
            req.setAttribute("error", "Cập nhật thất bại! Vui lòng thử lại.");
            reloadForm(req, res, current);
        }
    }

    // ----------------- Helper -----------------
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private static Integer parseNullable(String s) {
        try {
            return (s == null || s.trim().isEmpty()) ? null : Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static Boolean parseGender(String g) {
        // chấp nhận "1"/"0" hoặc "Nam"/"Nữ"
        if ("1".equals(g) || "Nam".equalsIgnoreCase(g)) {
            return true;
        }
        if ("0".equals(g) || "Nữ".equalsIgnoreCase(g)) {
            return false;
        }
        return null;
    }

    private static void safeSetDob(User u, String yyyyMMdd) {
        try {
            u.setDob(Date.valueOf(yyyyMMdd));
        } catch (Exception ignore) {
        }
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
        return "EditUserController optimized with full validation & dedup checks";
    }
}
