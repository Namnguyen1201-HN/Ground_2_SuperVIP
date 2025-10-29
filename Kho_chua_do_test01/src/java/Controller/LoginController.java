package Controller;

import DAL.UserDAO;
import Model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@WebServlet(name = "LoginController", urlPatterns = {"/Login"})
public class LoginController extends HttpServlet {

    // --- Constants for better readability and maintenance ---
    private static final int ROLE_ADMIN = 0;
    private static final int ROLE_MANAGER = 1;
    private static final int ROLE_SALES_STAFF = 2;
    private static final int ROLE_WAREHOUSE_STAFF = 3;

    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_UNVERIFIED = 2;

    private static final int SESSION_TIMEOUT_DEFAULT_SECONDS = 30 * 60; // 30 minutes
    private static final int SESSION_TIMEOUT_REMEMBER_ME_SECONDS = 30 * 24 * 60 * 60; // 30 days

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // false: do not create a new session if one does not exist

        // If user is already logged in, redirect to the dashboard
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect("Tongquan");
            return;
        }

        // Otherwise, show the login page
        request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            forwardToLoginWithError(request, response, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }

        try {
            String hashedPassword = hashSHA256(password.trim());
            User user = userDAO.authenticateUser(username.trim(), hashedPassword);

            if (user == null) {
                forwardToLoginWithError(request, response, "Tên đăng nhập hoặc mật khẩu không chính xác!");
                return;
            }

            // Check user status
            if (user.getIsActive() == STATUS_UNVERIFIED) {
                forwardToLoginWithError(request, response, "Tài khoản của bạn chưa được kích hoạt, vui lòng liên hệ hỗ trợ.");
                return;
            }

            if (user.getIsActive() != STATUS_ACTIVE) {
                forwardToLoginWithError(request, response, "Tài khoản của bạn đã bị khóa hoặc không hoạt động.");
                return;
            }

            // --- Authentication successful, create session ---
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);
            session.setAttribute("userID", user.getUserId());
            session.setAttribute("username", username.trim());
            session.setAttribute("roleName", user.getRoleName());
            session.setAttribute("roleID", user.getRoleId());
            session.setAttribute("branchID", user.getBranchId());
            session.setAttribute("warehouseId", user.getWarehouseId()); // ✅ dùng key này
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("phone", user.getPhone());

            // Set session timeout based on "Remember Me"
            if ("on".equals(remember)) {
                session.setMaxInactiveInterval(SESSION_TIMEOUT_REMEMBER_ME_SECONDS);
            } else {
                session.setMaxInactiveInterval(SESSION_TIMEOUT_DEFAULT_SECONDS);
            }

            redirectByRole(response, user.getRoleId());

        } catch (Exception e) {
            // Log the exception for debugging purposes (optional, using a proper logger is better)
            e.printStackTrace();
            forwardToLoginWithError(request, response, "Đã có lỗi xảy ra trong quá trình đăng nhập. Vui lòng thử lại.");
        }
    }

    /**
     * Redirects the user based on their role ID.
     */
    private void redirectByRole(HttpServletResponse response, int roleID) throws IOException {
        switch (roleID) {
            case ROLE_ADMIN:

            case ROLE_MANAGER:
                response.sendRedirect("TongQuan");
                break;
            case ROLE_SALES_STAFF:
                response.sendRedirect("sale");
                break;
            case ROLE_WAREHOUSE_STAFF:
                response.sendRedirect("WareHouseProduct");
                break;
            default:
                // Redirect to login for unknown roles as a fallback
                response.sendRedirect("Login");
                break;
        }
    }

    /**
     * Helper method to forward back to the login page with an error message.
     */
    private void forwardToLoginWithError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
    }

    /**
     * Hashes a string using SHA-256.
     */
    private String hashSHA256(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // In a real application, this should be handled more gracefully
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles user authentication.";
    }
}
