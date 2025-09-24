package controller;

import dao.DBContext;
import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    // Khi vào URL trực tiếp (GET) -> forward sang login.jsp
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("login.jsp").forward(req, resp);
    }

    // Xử lý submit form (POST)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password"); // lấy mật khẩu từ form

        try (Connection conn = DBContext.getConnection()) {
            UserDAO dao = new UserDAO(conn);
            User user = dao.login(username, password);

            if (user != null) {
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                resp.sendRedirect("success.jsp");
            } else {
                req.setAttribute("error", "Sai thông tin đăng nhập!");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
