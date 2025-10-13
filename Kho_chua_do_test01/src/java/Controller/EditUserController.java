package Controller;

import DAL.UserDAO;
import DAL.RoleDAO;
import DAL.WarehouseDAO;
import DAL.ShiftDAO;
import DAL.BranchDAO;
import Model.Branch;
import Model.User;
import Model.Role;
import Model.Warehouse;
import Model.Shift;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "EditUserController", urlPatterns = {"/EditUser"})
public class EditUserController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdParam = request.getParameter("userId");
        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            response.sendRedirect("NhanVien");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdParam);

            UserDAO userDAO = new UserDAO();
            RoleDAO roleDAO = new RoleDAO();
            BranchDAO branchDAO = new BranchDAO();
            ShiftDAO shiftDAO = new ShiftDAO();

            User user = userDAO.getUserById(userId);
            List<Role> roles = roleDAO.getAllRoles();
            List<Branch> branches = branchDAO.getAllBranches();
            List<Shift> shifts = shiftDAO.getAll();

            if (user == null) {
                response.sendRedirect("NhanVien");
                return;
            }

            request.setAttribute("user", user);
            request.setAttribute("roles", roles);
            request.setAttribute("branches", branches);
            request.setAttribute("shifts", shifts);

            request.getRequestDispatcher("/WEB-INF/jsp/admin/EditUser.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("NhanVien");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        UserDAO userDAO = new UserDAO();

        try {
            // ========== SA THẢI NHÂN VIÊN ==========
            if ("delete".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                boolean deleted = userDAO.deleteUser(userId);

                if (deleted) {
                    response.sendRedirect("NhanVien?success=delete");
                } else {
                    response.sendRedirect("NhanVien?error=delete_failed");
                }
                return;
            }

            // ========== CẬP NHẬT NHÂN VIÊN ==========
            if ("update".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userID"));
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");
                String address = request.getParameter("address");
                String genderParam = request.getParameter("gender");
                String dobParam = request.getParameter("dob");
                String roleParam = request.getParameter("roleID");
                String branchParam = request.getParameter("branchID");
                String warehouseParam = request.getParameter("warehouseID");
                String isActiveParam = request.getParameter("isActive");

                User user = userDAO.getUserById(userId);
                if (user == null) {
                    response.sendRedirect("NhanVien");
                    return;
                }

                // --- Gán dữ liệu mới ---
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPhone(phone);
                user.setAddress(address);

                // Giới tính
                if (genderParam != null) {
                    user.setGender("Nam".equalsIgnoreCase(genderParam));
                } else {
                    user.setGender(null);
                }

                // Ngày sinh
                if (dobParam != null && !dobParam.isEmpty()) {
                    user.setDob(java.sql.Date.valueOf(dobParam));
                } else {
                    user.setDob(null);
                }

                // Vai trò
                if (roleParam != null && !roleParam.isEmpty()) {
                    user.setRoleId(Integer.parseInt(roleParam));
                }

                // Chi nhánh
                if (branchParam != null && !branchParam.isEmpty()) {
                    user.setBranchId(Integer.parseInt(branchParam));
                } else {
                    user.setBranchId(null);
                }

                // Kho
                if (warehouseParam != null && !warehouseParam.isEmpty()) {
                    user.setWarehouseId(Integer.parseInt(warehouseParam));
                } else {
                    user.setWarehouseId(null);
                }

                // Trạng thái
                user.setActive("1".equals(isActiveParam));

                // --- Cập nhật ---
                boolean updated = userDAO.updateUser(user);

                if (updated) {
                    response.sendRedirect("NhanVien?success=update");
                } else {
                    response.sendRedirect("NhanVien?error=update_failed");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("NhanVien?error=server");
        }
    }

    @Override
    public String getServletInfo() {
        return "Hiển thị, lưu và xóa nhân viên";
    }
}
