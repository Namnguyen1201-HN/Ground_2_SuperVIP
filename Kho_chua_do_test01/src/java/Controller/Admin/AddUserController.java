package Controller.Admin;

import DAL.UserDAO;
import DAL.BranchDAO;
import DAL.RoleDAO;
import DAL.WarehouseDAO;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet(name = "AddUserController", urlPatterns = {"/AddUser"})
public class AddUserController extends HttpServlet {

    // DAOs
    private final UserDAO userDAO = new UserDAO();
    private final BranchDAO branchDAO = new BranchDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

    // Regex / formats
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PHONE_REGEX = "^0\\d{9}$";      // 10 số, bắt đầu bằng 0
    private static final String CCCD_REGEX  = "^\\d{12}$";      // đúng 12 số
    private static final String DOB_FMT     = "yyyy-MM-dd";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadDropdownData(request);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/AddUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 1) Lấy dữ liệu từ form -> User (đã trim & parse sẵn)
        User u = buildUserFromRequest(request);

        // 2) Validate
        Map<String, String> errors = validateUser(u);

        // 3) Nếu lỗi -> forward về form, giữ dữ liệu + dropdown
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("user", u);
            loadDropdownData(request);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/AddUser.jsp").forward(request, response);
            return;
        }

        // 4) Lưu DB
        boolean created = userDAO.insertUser(u);
        if (created) {
            response.sendRedirect("NhanVien?success=add");
        } else {
            request.setAttribute("error", "Không thể thêm nhân viên!");
            request.setAttribute("user", u);
            loadDropdownData(request);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/AddUser.jsp").forward(request, response);
        }
    }

    // ---------- Helpers ----------

    private User buildUserFromRequest(HttpServletRequest req) {
        User u = new User();
        u.setFullName(trim(req.getParameter("fullName")));
        u.setEmail(trim(req.getParameter("email")));
        u.setPasswordHash(trim(req.getParameter("password"))); 
        u.setPhone(trim(req.getParameter("phone")));
        u.setIdentificationId(trim(req.getParameter("identificationId")));
        u.setAddress(trim(req.getParameter("address")));
        u.setAvaUrl(trim(req.getParameter("avaUrl")));

        // Gender: "1" = Nam, "0" = Nữ
        String gender = trim(req.getParameter("gender"));
        if (gender != null) u.setGender("1".equals(gender));

        // DOB yyyy-MM-dd
        u.setDob(parseDate(trim(req.getParameter("dob")), DOB_FMT));

        // FK (nullable)
        u.setBranchId(parseInt(trim(req.getParameter("branchId"))));
        u.setWarehouseId(parseInt(trim(req.getParameter("warehouseId"))));
        Integer roleId = parseInt(trim(req.getParameter("roleId")));
        if (roleId != null) u.setRoleId(roleId);

        // isActive: default 2 (chờ phê duyệt)
        Integer isActive = parseInt(trim(req.getParameter("isActive")));
        u.setIsActive(isActive != null ? isActive : 2);

        return u;
    }

    private Map<String, String> validateUser(User u) {
        Map<String, String> errors = new LinkedHashMap<>();

        // Full name
        if (isBlank(u.getFullName())) {
            errors.put("fullName", "Họ tên không được để trống.");
        } else if (!u.getFullName().matches("^[\\p{L} .'-]{2,100}$")) {
            errors.put("fullName", "Họ tên không hợp lệ (chỉ gồm chữ và khoảng trắng, 2–100 ký tự).");
        }

        // Email
        if (isBlank(u.getEmail())) {
            errors.put("email", "Email không được để trống.");
        } else if (!u.getEmail().matches(EMAIL_REGEX)) {
            errors.put("email", "Email không hợp lệ.");
        } else if (userDAO.isEmailExists(u.getEmail())) {
            errors.put("email", "Email đã tồn tại trong hệ thống.");
        }

        // Phone
        if (isBlank(u.getPhone())) {
            errors.put("phone", "Số điện thoại không được để trống.");
        } else if (!u.getPhone().matches(PHONE_REGEX)) {
            errors.put("phone", "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0.");
        } else if (userDAO.isPhoneExists(u.getPhone())) {
            errors.put("phone", "Số điện thoại đã tồn tại trong hệ thống.");
        }

        // CCCD: đúng 12 số
        if (isBlank(u.getIdentificationId())) {
            errors.put("identificationId", "CMND/CCCD không được để trống.");
        } else if (!u.getIdentificationId().matches(CCCD_REGEX)) {
            errors.put("identificationId", "CCCD phải gồm đúng 12 chữ số.");
        } else if (userDAO.isIdentificationIdExists(u.getIdentificationId())) {
            errors.put("identificationId", "CMND/CCCD đã tồn tại trong hệ thống.");
        }

        // Password
        if (isBlank(u.getPasswordHash())) {
            errors.put("password", "Mật khẩu không được để trống.");
        } else if (u.getPasswordHash().length() < 6) {
            errors.put("password", "Mật khẩu phải có ít nhất 6 ký tự.");
        }

        // (Tuỳ chọn) kiểm tra tồn tại FK nếu muốn:
        // - branchDAO.getById(u.getBranchId()) != null ...
        // - warehouseDAO.getWarehouseById(u.getWarehouseId()) != null ...
        // - roleDAO.getById(u.getRoleId()) != null ...

        return errors;
    }

    private void loadDropdownData(HttpServletRequest request) {
        request.setAttribute("branches", branchDAO.getAllBranches());
        request.setAttribute("roles", roleDAO.getAllRoles());
        request.setAttribute("warehouses", warehouseDAO.getAllWarehouses());
    }

    // --- small utils ---
    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    private static Integer parseInt(String s) {
        if (isBlank(s)) return null;
        try { return Integer.valueOf(s); } catch (NumberFormatException e) { return null; }
    }
    private static Date parseDate(String s, String pattern) {
        if (isBlank(s)) return null;
        try { return new SimpleDateFormat(pattern).parse(s); }
        catch (ParseException e) { return null; }
    }
}
