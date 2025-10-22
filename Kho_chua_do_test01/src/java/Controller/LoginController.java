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

@WebServlet(name = "LoginController", urlPatterns = {"/Login"})
public class LoginController extends HttpServlet {

    private String hashSHA256(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        System.err.println("[DEBUG] LoginController initialized. userDAO = " + (userDAO != null));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.err.println("[DEBUG] LoginController.doGet() called");

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            System.err.println("[DEBUG] User already logged in, redirecting to DashBoard");
            response.sendRedirect("DashBoard");
            return;
        }

        System.err.println("[DEBUG] Forwarding to Login.jsp");
        request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        System.err.println("========== LOGIN DEBUG ==========");
        System.err.println("[DEBUG] Received POST /Login");
        System.err.println("[DEBUG] Username: " + username);
        System.err.println("[DEBUG] Password (raw): " + password);
        System.err.println("[DEBUG] Remember me: " + remember);

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            System.err.println("[ERROR] Empty username or password");
            request.setAttribute("error", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
            return;
        }

        try {
            System.err.println("[DEBUG] Calling authenticateUser...");

            String hashedPassword = hashSHA256(password.trim());
            System.err.println("[DEBUG] Password (hashed): " + hashedPassword);

            System.err.println("[DEBUG] Calling userDAO.authenticateUser...");
            User user = userDAO.authenticateUser(username.trim(), hashedPassword);
            System.err.println("[DEBUG] userDAO.authenticateUser() returned: " + user);

            if (user != null) {
                System.err.println("[DEBUG] User found - ID: " + user.getUserId()
                        + ", Role: " + user.getRoleId() + ", Active: " + user.getIsActive());

                if (user.getIsActive() != 1) {
                    System.err.println("[ERROR] User not active. Status: " + user.getIsActive());
                    request.setAttribute("error", "Tài khoản của bạn không được kích hoạt!");
                    request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
                    return;
                }

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

                if ("on".equals(remember)) {
                    session.setMaxInactiveInterval(30 * 24 * 60 * 60); // 30 days
                } else {
                    session.setMaxInactiveInterval(30 * 60 * 60); // 30 minutes
                }

                System.err.println("[DEBUG] Session created successfully");
                System.err.println("[DEBUG] Redirecting to DashBoard...");
                redirectByRole(response, user.getRoleId());
            } else {
                System.err.println("[ERROR] Authentication failed - User is null");
                request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
                request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in login: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
        }

        System.err.println("========== END LOGIN DEBUG ==========");
    }

    private void redirectByRole(HttpServletResponse response, int roleID) throws IOException {
        System.err.println("[DEBUG] Redirecting by roleID = " + roleID);
        switch (roleID) {
            case 0:
                System.err.println("[DEBUG] Redirect to TongQuan");
                response.sendRedirect("TongQuan");
                break;
            case 1:
                System.err.println("[DEBUG] Redirect to Admin");
                response.sendRedirect("TongQuan");
                break;
            case 2:
                System.err.println("[DEBUG] Redirect to Sale");
                response.sendRedirect("sale");
                break;
            case 3:
                System.err.println("[DEBUG] Redirect to WareHouseProduct");
                response.sendRedirect("WareHouseProduct");
                break;
            default:
                System.err.println("[ERROR] Unknown roleID: " + roleID);
                response.sendRedirect("Login");
                break;
        }
    }

    @Override
    public String getServletInfo() {
        return "Login Controller for WM System";
    }
}
