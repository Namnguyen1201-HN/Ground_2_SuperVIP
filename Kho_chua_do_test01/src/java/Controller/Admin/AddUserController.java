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

@WebServlet(name = "AddUserController", urlPatterns = {"/AddUser"})
public class AddUserController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final BranchDAO branchDAO = new BranchDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("branches", branchDAO.getAllBranches());
        request.setAttribute("roles", roleDAO.getAllRoles());
        request.setAttribute("warehouses", warehouseDAO.getAllWarehouses());
        request.getRequestDispatcher("/WEB-INF/jsp/admin/AddUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            User u = new User();
            u.setFullName(request.getParameter("fullName"));
            u.setEmail(request.getParameter("email"));
            u.setPasswordHash(request.getParameter("password"));
            u.setPhone(request.getParameter("phone"));
            u.setIdentificationId(request.getParameter("identificationId"));
            u.setAddress(request.getParameter("address"));
            u.setAvaUrl(request.getParameter("avaUrl"));
            u.setRoleId(Integer.parseInt(request.getParameter("roleId")));

            // ⚙️ Xử lý trạng thái (0,1,2)
            String isActiveParam = request.getParameter("isActive");
            if (isActiveParam != null && !isActiveParam.isEmpty()) {
                u.setIsActive(Integer.parseInt(isActiveParam));
            } else {
                // Mặc định nếu không chọn thì là "Chờ phê duyệt"
                u.setIsActive(2);
            }

            // branch và warehouse có thể null
            String branchParam = request.getParameter("branchId");
            String warehouseParam = request.getParameter("warehouseId");
            if (branchParam != null && !branchParam.isEmpty()) {
                u.setBranchId(Integer.parseInt(branchParam));
            }
            if (warehouseParam != null && !warehouseParam.isEmpty()) {
                u.setWarehouseId(Integer.parseInt(warehouseParam));
            }

            // gender và dob
            String gender = request.getParameter("gender");
            if (gender != null && !gender.isEmpty()) {
                u.setGender("1".equals(gender)); // 1 = Nam, 0 = Nữ
            }
            String dob = request.getParameter("dob");
            if (dob != null && !dob.isEmpty()) {
                u.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
            }

            boolean created = userDAO.insertUser(u);
            if (created) {
                response.sendRedirect("NhanVien?success=add");
            } else {
                request.setAttribute("error", "Không thể thêm nhân viên!");
                doGet(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi dữ liệu đầu vào!");
            doGet(request, response);
        }
    }
}
