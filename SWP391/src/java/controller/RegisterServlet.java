package controller;

import dao.DBContext;
import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    // Khi vào URL trực tiếp (GET) -> forward sang register.jsp
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("register.jsp").forward(req, resp);
    }

    // Xử lý submit form (POST)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String fullname = req.getParameter("fullname");
        String phone = req.getParameter("phone");
        String country = req.getParameter("country");
        String region = req.getParameter("region");
        String captcha = req.getParameter("captcha");
        String captchaInput = req.getParameter("captchaInput");

        if (!captcha.equals(captchaInput)) {
            req.setAttribute("error", "Mã xác thực không đúng!");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
            return;
        }

        try (Connection conn = DBContext.getConnection()) {
            UserDAO dao = new UserDAO(conn);

            User user = new User();
            user.setFullName(fullname);
            user.setUsername(phone);
            user.setPasswordHash("123456"); // mật khẩu mặc định
            user.setPhone(phone);
            user.setRoleId(2); // ví dụ StoreManager

            if (dao.register(user)) {
                resp.sendRedirect("login");
            } else {
                req.setAttribute("error", "Đăng ký thất bại!");
                req.getRequestDispatcher("register.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
