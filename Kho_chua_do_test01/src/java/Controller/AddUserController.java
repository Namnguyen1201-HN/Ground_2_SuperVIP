package Controller;

import DAL.UserDAO;
import DAL.BranchDAO;
import DAL.DepartmentDAO;
import DAL.RoleDAO;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

@WebServlet(name="AddUserController", urlPatterns={"/AddUser"})
public class AddUserController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final BranchDAO branchDAO = new BranchDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // load dropdown data
        request.setAttribute("branches", branchDAO.getAllBranches());
        request.setAttribute("departments", departmentDAO.getAllDepartments());
        request.setAttribute("roles", roleDAO.getAllRoles());

        request.getRequestDispatcher("/WEB-INF/jsp/admin/AddUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        User u = new User();
        u.setFullName(request.getParameter("fullName"));
        u.setUsername(request.getParameter("username"));
        u.setEmail(request.getParameter("email"));
        u.setPhone(request.getParameter("phone"));
        u.setIdentifierCode(request.getParameter("identifierCode"));
        u.setDepartmentId(Integer.parseInt(request.getParameter("departmentId")));
        u.setRoleId(Integer.parseInt(request.getParameter("roleId")));
        u.setActive("1".equals(request.getParameter("isActive")));
        u.setCreatedAt(new Timestamp(new Date().getTime()));

        boolean created = userDAO.insertUser(u);
        if (created) {
            response.sendRedirect("NhanVien");
        } else {
            request.setAttribute("error", "Không thể thêm nhân viên!");
            doGet(request, response);
        }
    }
}
