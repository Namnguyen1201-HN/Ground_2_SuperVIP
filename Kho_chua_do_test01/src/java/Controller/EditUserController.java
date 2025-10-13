package Controller;

import DAL.UserDAO;
import DAL.RoleDAO;
import DAL.WarehouseDAO;
import DAL.ShiftDAO;
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
            WarehouseDAO warehouseDAO = new WarehouseDAO();
            ShiftDAO shiftDAO = new ShiftDAO();

            User user = userDAO.getUserById(userId);
            List<Role> roles = roleDAO.getAllRoles();
            List<Warehouse> warehouses = warehouseDAO.getAllWarehouses();
            List<Shift> shifts = shiftDAO.getAll();

            if (user == null) {
                response.sendRedirect("NhanVien");
                return;
            }

            request.setAttribute("user", user);
            request.setAttribute("roles", roles);
            request.setAttribute("warehouses", warehouses);
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
                String warehouseParam = request.getParameter("warehouseID");
                String isActiveParam = request.getParameter("isActive");

                User user = userDAO.getUserById(userId);
                if (user == null) {
                    response.sendRedirect("NhanVien");
                    return;
                }

                user.setFullName(fullName);
                user.setEmail(email);
                user.setPhone(phone);
                user.setAddress(address);

                if (genderParam != null) {
                    user.setGender("Nam".equalsIgnoreCase(genderParam));
                } else {
                    user.setGender(null);
                }

                if (dobParam != null && !dobParam.isEmpty()) {
                    user.setDob(java.sql.Date.valueOf(dobParam));
                } else {
                    user.setDob(null);
                }

                if (roleParam != null && !roleParam.isEmpty()) {
                    user.setRoleId(Integer.parseInt(roleParam));
                }

                if (warehouseParam != null && !warehouseParam.isEmpty()) {
                    user.setWarehouseId(Integer.parseInt(warehouseParam));
                } else {
                    user.setWarehouseId(null);
                }

                user.setActive("1".equals(isActiveParam));

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
