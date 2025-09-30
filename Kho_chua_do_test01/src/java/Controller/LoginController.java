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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/Login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            request.getRequestDispatcher("/WEB-INF/jsp/Login.jsp").forward(request, response);
            return;
        }

        try {
            User user = userDAO.authenticateUser(username, password);
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);
                session.setAttribute("username", user.getUsername());

                if ("on".equals(remember)) {
                    session.setMaxInactiveInterval(30 * 24 * 60 * 60); 
                } else {
                    session.setMaxInactiveInterval(30 * 60);
                }

                response.sendRedirect("DashBoard"); 
            } else {
                request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
                request.getRequestDispatcher("/WEB-INF/jsp/Login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra trong quá trình đăng nhập!");
            request.getRequestDispatcher("/WEB-INF/jsp/Login.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Login Controller for WM System";
    }
}