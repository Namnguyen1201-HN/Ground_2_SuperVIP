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
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect("DashBoard");
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        System.out.println("========== LOGIN DEBUG ==========");
        System.out.println("Username input: " + username);
        System.out.println("Password input: " + password);

        // Validate input
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            System.out.println("ERROR: Empty username or password");
            request.setAttribute("error", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
            return;
        }

        try {
            System.out.println("Calling authenticateUser...");
            User user = userDAO.authenticateUser(username.trim(), password);
            
            System.out.println("User result: " + user);

            if (user != null) {
                System.out.println("User found - ID: " + user.getUserId() + ", Role: " + user.getRoleId() + ", Active: " + user.getIsActive());
                
                // Check user status
                if (user.getIsActive() != 1) {
                    System.out.println("ERROR: User not active. Status: " + user.getIsActive());
                    request.setAttribute("error", "Tài khoản của bạn không được kích hoạt!");
                    request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
                    return;
                }

                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);
                session.setAttribute("userID", user.getUserId());
                session.setAttribute("username", username.trim());
                session.setAttribute("roleName", user.getRoleName());
                session.setAttribute("roleID", user.getRoleId());
                session.setAttribute("branchID", user.getBranchId());
                session.setAttribute("warehouseID", user.getWarehouseId());
                session.setAttribute("fullName", user.getFullName());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("phone", user.getPhone());

                // Set session timeout
                if ("on".equals(remember)) {
                    session.setMaxInactiveInterval(30 * 24 * 60 * 60); // 30 days
                } else {
                    session.setMaxInactiveInterval(30 * 60); // 30 minutes
                }

                System.out.println("Session created successfully");
                System.out.println("Redirecting to DashBoard...");
                
                // Redirect by role
                redirectByRole(response, user.getRoleId());
            } else {
                System.out.println("ERROR: Authentication failed - User is null");
                request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
                request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("ERROR: Exception in login");
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
        }
        
        System.out.println("========== END LOGIN DEBUG ==========");
    }

    private void redirectByRole(HttpServletResponse response, int roleID) throws IOException {
        switch (roleID) {
            case 0:
                response.sendRedirect("TongQuan");
                break;
            case 1:
                response.sendRedirect("");
                break;
            case 2:
                response.sendRedirect("sale");
                break;
            case 3:
                response.sendRedirect("WareHouseProduct");
                break;           
        }
    }

    @Override
    public String getServletInfo() {
        return "Login Controller for WM System";
    }
}
