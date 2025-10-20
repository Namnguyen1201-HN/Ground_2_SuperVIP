package Controller.Admin;

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
            WarehouseDAO warehouseDAO = new WarehouseDAO();
            ShiftDAO shiftDAO = new ShiftDAO();

            User user = userDAO.getUserById(userId);
            if (user == null) {
                response.sendRedirect("NhanVien");
                return;
            }

            // 🔹 Lấy danh sách ca làm + ca hiện tại
            List<Shift> shifts = shiftDAO.getAll();
            Integer currentShiftId = userDAO.getShiftIdByUserId(userId);
            user.setShiftID(currentShiftId);

            // 🔹 Lấy danh sách role, branch, warehouse
            List<Role> roles = roleDAO.getAllRoles();
            List<Branch> branches = branchDAO.getAllBranches();
            List<Warehouse> warehouses = warehouseDAO.getAllWarehouses();

            // 🔹 Đẩy dữ liệu sang JSP
            request.setAttribute("user", user);
            request.setAttribute("roles", roles);
            request.setAttribute("branches", branches);
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
            if ("delete".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                boolean deleted = userDAO.deleteUser(userId);
                response.sendRedirect(deleted ? "NhanVien?success=delete" : "NhanVien?error=delete_failed");
                return;
            }

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
                String shiftParam = request.getParameter("shiftID");
                String isActiveParam = request.getParameter("isActive");

                User user = userDAO.getUserById(userId);
                if (user == null) {
                    response.sendRedirect("NhanVien");
                    return;
                }

                // --- Cập nhật thông tin cơ bản ---
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPhone(phone);
                user.setAddress(address);

                if (genderParam != null) {
                    user.setGender("Nam".equalsIgnoreCase(genderParam));
                }

                user.setDob((dobParam != null && !dobParam.isEmpty()) ? java.sql.Date.valueOf(dobParam) : null);

                if (roleParam != null && !roleParam.isEmpty()) {
                    user.setRoleId(Integer.parseInt(roleParam));
                }

                // ⚙️ Sửa lại: set theo kiểu int (0,1,2)
                if (isActiveParam != null && !isActiveParam.isEmpty()) {
                    user.setIsActive(Integer.parseInt(isActiveParam));
                }

                // --- Cập nhật chi nhánh / kho theo vai trò ---
                if (user.getRoleId() == 3) { // Quản lý kho
                    user.setWarehouseId(warehouseParam != null && !warehouseParam.isEmpty()
                            ? Integer.parseInt(warehouseParam) : null);
                    user.setBranchId(null);
                } else { // ✅ Các vai trò còn lại đều thuộc chi nhánh
                    user.setBranchId(branchParam != null && !branchParam.isEmpty()
                            ? Integer.parseInt(branchParam) : null);
                    user.setWarehouseId(null);
                }

                // --- Cập nhật thông tin ---
                boolean updated = userDAO.updateUser(user);

                // --- Cập nhật ca làm ---
                if (shiftParam != null && !shiftParam.isEmpty()) {
                    int newShiftID = Integer.parseInt(shiftParam);
                    userDAO.updateUserShift(userId, newShiftID);
                } else {
                    userDAO.deleteUserShift(userId);
                }

                response.sendRedirect(updated ? "NhanVien?success=update" : "NhanVien?error=update_failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("NhanVien?error=server");
        }
    }

    @Override
    public String getServletInfo() {
        return "Hiển thị, cập nhật và xóa nhân viên, với logic vai trò và ca làm";
    }
}
