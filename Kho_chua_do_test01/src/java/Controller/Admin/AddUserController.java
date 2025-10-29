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
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "AddUserController", urlPatterns = {"/AddUser"})
public class AddUserController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final BranchDAO branchDAO = new BranchDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

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

        try {
            // 🔹 1. Thu thập dữ liệu từ form
            User u = new User();
            u.setFullName(request.getParameter("fullName"));
            u.setEmail(request.getParameter("email"));
            u.setPasswordHash(request.getParameter("password"));
            u.setPhone(request.getParameter("phone"));
            u.setIdentificationId(request.getParameter("identificationId"));
            u.setAddress(request.getParameter("address"));
            u.setAvaUrl(request.getParameter("avaUrl"));

            String gender = request.getParameter("gender");
            if (gender != null && !gender.isEmpty()) {
                u.setGender("1".equals(gender)); // 1 = Nam, 0 = Nữ
            }

            String dob = request.getParameter("dob");
            if (dob != null && !dob.isEmpty()) {
                u.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
            }

            // Optional IDs
            String branchParam = request.getParameter("branchId");
            String warehouseParam = request.getParameter("warehouseId");
            if (branchParam != null && !branchParam.isEmpty()) {
                u.setBranchId(Integer.parseInt(branchParam));
            }
            if (warehouseParam != null && !warehouseParam.isEmpty()) {
                u.setWarehouseId(Integer.parseInt(warehouseParam));
            }

            String roleId = request.getParameter("roleId");
            if (roleId != null && !roleId.isEmpty()) {
                u.setRoleId(Integer.parseInt(roleId));
            }

            // isActive
            String isActiveParam = request.getParameter("isActive");
            u.setIsActive((isActiveParam != null && !isActiveParam.isEmpty())
                    ? Integer.parseInt(isActiveParam) : 2);

            // 🔹 2. Validate dữ liệu
            Map<String, String> errors = validateUser(u);
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("user", u);
                loadDropdownData(request);
                request.getRequestDispatcher("/WEB-INF/jsp/admin/AddUser.jsp").forward(request, response);
                return;
            }

            // 🔹 3. Lưu vào DB
            boolean created = userDAO.insertUser(u);
            if (created) {
                response.sendRedirect("NhanVien?success=add");
            } else {
                request.setAttribute("error", "Không thể thêm nhân viên!");
                loadDropdownData(request);
                request.getRequestDispatcher("/WEB-INF/jsp/admin/AddUser.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi xử lý dữ liệu!");
            loadDropdownData(request);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/AddUser.jsp").forward(request, response);
        }
    }

    // 🔧 Load danh sách branch, role, warehouse cho form
    private void loadDropdownData(HttpServletRequest request) {
        request.setAttribute("branches", branchDAO.getAllBranches());
        request.setAttribute("roles", roleDAO.getAllRoles());
        request.setAttribute("warehouses", warehouseDAO.getAllWarehouses());
    }

    // 🔎 Validate đầu vào người dùng
    private Map<String, String> validateUser(User u) {
        Map<String, String> errors = new HashMap<>();

        if (u.getFullName() == null || u.getFullName().trim().isEmpty()) {
            errors.put("fullName", "Họ tên không được để trống.");
        }

        if (u.getEmail() == null || u.getEmail().trim().isEmpty()) {
            errors.put("email", "Email không được để trống.");
        } else if (!u.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errors.put("email", "Email không hợp lệ.");
        } else if (userDAO.isEmailExists(u.getEmail())) {
            errors.put("email", "Email đã tồn tại trong hệ thống.");
        }

        if (u.getPhone() == null || u.getPhone().trim().isEmpty()) {
            errors.put("phone", "Số điện thoại không được để trống.");
        } else if (!u.getPhone().matches("^0\\d{9}$")) {
            errors.put("phone", "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0.");
        } else if (userDAO.isPhoneExists(u.getPhone())) {
            errors.put("phone", "Số điện thoại đã tồn tại trong hệ thống.");
        }

        if (u.getIdentificationId() == null || u.getIdentificationId().trim().isEmpty()) {
            errors.put("identificationId", "CMND/CCCD không được để trống.");
        } else if (!u.getIdentificationId().matches("^\\d{9,12}$")) {
            errors.put("identificationId", "CMND/CCCD phải là 9–12 chữ số.");
        } else if (userDAO.isIdentificationIdExists(u.getIdentificationId())) {
            errors.put("identificationId", "CMND/CCCD đã tồn tại trong hệ thống.");
        }

        if (u.getPasswordHash() == null || u.getPasswordHash().trim().isEmpty()) {
            errors.put("password", "Mật khẩu không được để trống.");
        } else if (u.getPasswordHash().length() < 6) {
            errors.put("password", "Mật khẩu phải có ít nhất 6 ký tự.");
        }

        return errors;
    }
}
