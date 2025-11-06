package Controller.Admin;

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

@WebServlet(name = "StaffManagementController", urlPatterns = {"/StaffManagement"})
public class StaffManagementController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // ====== VALIDATE & GET PARAMETERS ======
        String search = validateAndTrim(request.getParameter("search"));
        String statusParam = validateStatus(request.getParameter("status"));
        String roleParam = validateRole(request.getParameter("role"));
        String branchParam = validateBranch(request.getParameter("branch"));

        String status = (statusParam == null || statusParam.isEmpty()) ? "all" : statusParam;
        String role = (roleParam == null || roleParam.isEmpty()) ? "all" : roleParam;
        String branch = (branchParam == null || branchParam.isEmpty()) ? "all" : branchParam;

        // ====== GET DATA ======
        BranchDAO branchDAO = new BranchDAO();
        RoleDAO roleDAO = new RoleDAO();

        List<Branch> branches = branchDAO.getAllBranches();
        List<Role> roles = roleDAO.getAllRoles();
        List<User> allUsers = userDAO.getAllUsers();

        // Exclude current logged-in user
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser != null) {
            allUsers = allUsers.stream()
                    .filter(u -> u.getUserId() != currentUser.getUserId())
                    .collect(Collectors.toList());
        }

        // ====== FILTER DATA ======
        List<User> filteredUsers = allUsers.stream()
                // Filter by branch
                .filter(u -> {
                    if (!"all".equalsIgnoreCase(branch)) {
                        String userBranch = (u.getBranchName() != null) ? u.getBranchName() : "";
                        return branch.equalsIgnoreCase(userBranch);
                    }
                    return true;
                })
                // Filter by role
                .filter(u -> {
                    if (!"all".equalsIgnoreCase(role)) {
                        String userRole = (u.getRoleName() != null) ? u.getRoleName() : "";
                        return role.equalsIgnoreCase(userRole);
                    }
                    return true;
                })
                // Filter by status
                .filter(u -> {
                    if (!"all".equalsIgnoreCase(status)) {
                        switch (status.toLowerCase()) {
                            case "active":       // Đang làm việc
                                return u.getIsActive() == 1;
                            case "inactive":     // Nghỉ việc
                                return u.getIsActive() == 0;
                            case "pending":      // Chờ phê duyệt
                                return u.getIsActive() == 2;
                            default:
                                return true;
                        }
                    }
                    return true;
                })
                // Filter by search
                .filter(u -> {
                    if (search != null && !search.trim().isEmpty()) {
                        String keyword = search.trim().toLowerCase();
                        // Search by ID (numeric)
                        if (keyword.matches("\\d+")) {
                            try {
                                int id = Integer.parseInt(keyword);
                                return u.getUserId() == id;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        } else {
                            // Search by name, email, phone
                            boolean matchesName = (u.getFullName() != null && 
                                    u.getFullName().toLowerCase().contains(keyword));
                            boolean matchesEmail = (u.getEmail() != null && 
                                    u.getEmail().toLowerCase().contains(keyword));
                            boolean matchesPhone = (u.getPhone() != null && 
                                    u.getPhone().toLowerCase().contains(keyword));
                            return matchesName || matchesEmail || matchesPhone;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // ====== PAGINATION ======
        int page = 1;
        int pageSize = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                int parsedPage = Integer.parseInt(pageParam.trim());
                if (parsedPage > 0) {
                    page = parsedPage;
                }
            } catch (NumberFormatException e) {
                // Invalid page parameter, use default
                page = 1;
            }
        }

        int totalUsers = filteredUsers.size();
        int totalPages = (totalUsers > 0) ? (int) Math.ceil((double) totalUsers / pageSize) : 1;
        
        // Ensure page is within valid range
        if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }
        if (page < 1) {
            page = 1;
        }

        int fromIndex = Math.max((page - 1) * pageSize, 0);
        int toIndex = Math.min(fromIndex + pageSize, totalUsers);
        
        List<User> paginatedUsers;
        if (fromIndex >= totalUsers) {
            paginatedUsers = List.of(); // Empty list
        } else {
            paginatedUsers = filteredUsers.subList(fromIndex, toIndex);
        }

        // Calculate display range
        int displayFrom = (totalUsers > 0) ? fromIndex + 1 : 0;
        int displayTo = Math.min(toIndex, totalUsers);

        // ====== SET ATTRIBUTES ======
        request.setAttribute("users", paginatedUsers);
        request.setAttribute("branches", branches);
        request.setAttribute("roles", roles);

        request.setAttribute("selectedStatus", status);
        request.setAttribute("selectedRole", role);
        request.setAttribute("selectedBranch", branch);
        request.setAttribute("searchKeyword", search);

        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("displayFrom", displayFrom);
        request.setAttribute("displayTo", displayTo);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/staff_management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // ====== VALIDATION METHODS ======
    
    private String validateAndTrim(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        // Limit search length to prevent abuse
        if (trimmed.length() > 100) {
            return trimmed.substring(0, 100);
        }
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        String normalized = status.trim().toLowerCase();
        // Only allow valid status values
        if (normalized.equals("all") || normalized.equals("active") || 
            normalized.equals("inactive") || normalized.equals("pending")) {
            return normalized;
        }
        return null;
    }

    private String validateRole(String role) {
        if (role == null || role.trim().isEmpty() || role.trim().equalsIgnoreCase("all")) {
            return null;
        }
        // Validate role exists (will be checked against database)
        return role.trim();
    }

    private String validateBranch(String branch) {
        if (branch == null || branch.trim().isEmpty() || branch.trim().equalsIgnoreCase("all")) {
            return null;
        }
        // Validate branch exists (will be checked against database)
        return branch.trim();
    }
}

