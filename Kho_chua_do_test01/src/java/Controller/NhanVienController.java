package Controller;

import DAL.UserDAO;
import Model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "NhanVien_controller", urlPatterns = {"/NhanVien"})
public class NhanVienController extends HttpServlet {

    private final UserDAO dao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String search = request.getParameter("search"); 
        String statusParam = request.getParameter("status");
        String roleParam = request.getParameter("role");

        List<User> users = dao.getAllUsers();
        List<User> result;

        if (search != null && !search.trim().isEmpty()) {
            // ===== TÌM KIẾM =====
            String keyword = search.trim().toLowerCase();
            result = users.stream()
                    .filter(u -> String.valueOf(u.getUserId()).equals(keyword)
                              || u.getFullName().toLowerCase().contains(keyword))
                    .toList();

            request.setAttribute("searchKeyword", search);
        } else {
            // ===== LỌC =====
            final String status = (statusParam == null) ? "all" : statusParam;
            final String role = (roleParam == null) ? "None" : roleParam;

            result = users.stream()
                    .filter(u -> {
                        if ("active".equalsIgnoreCase(status) && !u.isActive()) return false;
                        if ("inactive".equalsIgnoreCase(status) && u.isActive()) return false;
                        return true;
                    })
                    .filter(u -> {
                        if (!"None".equalsIgnoreCase(role) && !role.equalsIgnoreCase(u.getRoleName())) return false;
                        return true;
                    })
                    .toList();

            request.setAttribute("selectedStatus", status);
            request.setAttribute("selectedRole", role);
        }

        request.setAttribute("users", result);
        request.getRequestDispatcher("/WEB-INF/jsp/Nhan_vien.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
