package Controller;

import DAL.BranchDAO;
import DAL.RoleDAO;
import DAL.UserDAO;
import Model.User;
import Model.Branch;
import Model.Role;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
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

        // ====== NHẬN THAM SỐ ======
        String search = request.getParameter("search");
        String statusParam = request.getParameter("status");
        String roleParam = request.getParameter("role");
        String branchParam = request.getParameter("branch");

        String status = (statusParam == null) ? "all" : statusParam;
        String role = (roleParam == null) ? "None" : roleParam;
        String branch = (branchParam == null) ? "all" : branchParam;

        // ====== LẤY DỮ LIỆU ======
        BranchDAO branchDAO = new BranchDAO();
        RoleDAO roleDAO = new RoleDAO();

        List<Branch> branches = branchDAO.getAllBranches();
        List<Role> roles = roleDAO.getAllRoles();
        List<User> allUsers = userDAO.getAllUsers();

        // ====== LỌC DỮ LIỆU ======
        List<User> filteredUsers = allUsers.stream()
                .filter(u -> {
                    if (!"all".equalsIgnoreCase(branch)) {
                        return branch.equalsIgnoreCase(u.getBranchName());
                    }
                    return true;
                })
                .filter(u -> {
                    if (!"None".equalsIgnoreCase(role)) {
                        return role.equalsIgnoreCase(u.getRoleName());
                    }
                    return true;
                })
                .filter(u -> {
                    if (!"all".equalsIgnoreCase(status)) {
                        if ("active".equalsIgnoreCase(status)) {
                            return u.isActive();
                        } else if ("inactive".equalsIgnoreCase(status)) {
                            return !u.isActive();
                        }
                    }
                    return true;
                })
                .filter(u -> {
                    if (search != null && !search.trim().isEmpty()) {
                        String keyword = search.trim().toLowerCase();
                        if (keyword.matches("\\d+")) {
                            // tìm theo mã nhân viên chính xác
                            int id = Integer.parseInt(keyword);
                            return u.getUserId() == id;
                        } else {
                            // tìm theo tên, email, sđt
                            return (u.getFullName() != null && u.getFullName().toLowerCase().contains(keyword))
                                    || (u.getEmail() != null && u.getEmail().toLowerCase().contains(keyword))
                                    || (u.getPhone() != null && u.getPhone().toLowerCase().contains(keyword));
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // ====== PHÂN TRANG ======
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

        int totalUsers = filteredUsers.size();
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

        int fromIndex = Math.max((page - 1) * pageSize, 0);
        int toIndex = Math.min(fromIndex + pageSize, totalUsers);
        List<User> paginatedUsers = filteredUsers.subList(fromIndex, toIndex);

        // ====== TRUYỀN DỮ LIỆU SANG JSP ======
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
