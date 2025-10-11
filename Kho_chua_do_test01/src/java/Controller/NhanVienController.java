package Controller;

import DAL.BranchDAO;
import DAL.RoleDAO;
import DAL.UserDAO;
import Model.User;
import Model.Branch;
import Model.Role;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "NhanVienController", urlPatterns = {"/NhanVien"})
public class NhanVienController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ========== NHẬN THAM SỐ TỪ GIAO DIỆN ==========
        String search = request.getParameter("search");
        String statusParam = request.getParameter("status");
        String roleParam = request.getParameter("role");
        String branchParam = request.getParameter("branch");

        // Giá trị mặc định
        String status = (statusParam == null) ? "all" : statusParam;
        String role = (roleParam == null) ? "None" : roleParam;
        String branch = (branchParam == null) ? "all" : branchParam;

        // ========== GỌI DAO LẤY DỮ LIỆU ==========
        BranchDAO branchDAO = new BranchDAO();
        RoleDAO roleDAO = new RoleDAO();

        // Dữ liệu lọc
        List<User> users = userDAO.getFilteredUsers(branch, role, status, search);

        // Danh sách chi nhánh và vai trò để hiển thị trong bộ lọc
        List<Branch> branches = branchDAO.getAllBranches();
        List<Role> roles = roleDAO.getAllRoles();

        // ========== PHÂN TRANG ==========
        int page = 1;
        int pageSize = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        int totalUsers = users.size();
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

        int fromIndex = Math.max((page - 1) * pageSize, 0);
        int toIndex = Math.min(fromIndex + pageSize, totalUsers);
        List<User> paginatedUsers = users.subList(fromIndex, toIndex);

        // ========== GỬI DỮ LIỆU SANG JSP ==========
        request.setAttribute("users", paginatedUsers);
        request.setAttribute("branches", branches);
        request.setAttribute("roles", roles);

        request.setAttribute("selectedStatus", status);
        request.setAttribute("selectedRole", role);
        request.setAttribute("selectedBranch", branch);
        request.setAttribute("searchKeyword", search);

        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/NhanVien.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
