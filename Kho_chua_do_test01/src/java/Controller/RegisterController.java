package Controller;

import DAL.UserDAO;
import Model.User;
import java.io.IOException;
import java.util.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "RegisterController", urlPatterns = {"/Register"})
public class RegisterController extends HttpServlet {
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
        request.getRequestDispatcher("/WEB-INF/jsp/Register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String username = request.getParameter("username");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String terms = request.getParameter("terms");

        if (fullName == null || username == null || phone == null || email == null ||
            password == null || confirmPassword == null || terms == null ||
            fullName.trim().isEmpty() || username.trim().isEmpty() || phone.trim().isEmpty() ||
            email.trim().isEmpty() || password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin!");
            request.getRequestDispatcher("/WEB-INF/jsp/Register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("/WEB-INF/jsp/Register.jsp").forward(request, response);
            return;
        }

        String cleanPhone = phone.replaceAll("\\s+", "");
        if (!cleanPhone.matches("\\d{10,11}")) {
            request.setAttribute("error", "Số điện thoại không hợp lệ!");
            request.getRequestDispatcher("/WEB-INF/jsp/Register.jsp").forward(request, response);
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            request.setAttribute("error", "Email không hợp lệ!");
            request.getRequestDispatcher("/WEB-INF/jsp/Register.jsp").forward(request, response);
            return;
        }

        if (!"on".equals(terms)) {
            request.setAttribute("error", "Vui lòng đồng ý với điều khoản dịch vụ!");
            request.getRequestDispatcher("/WEB-INF/jsp/Register.jsp").forward(request, response);
            return;
        }

        try {
            if (userDAO.isPhoneExists(cleanPhone)) {
                request.setAttribute("error", "Số điện thoại đã được sử dụng!");
                request.getRequestDispatcher("/WEB-INF/jsp/Register.jsp").forward(request, response);
                return;
            }

            // Create new user
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setUsername(username);
            newUser.setPhone(cleanPhone);
            newUser.setEmail(email);
            newUser.setActive(true);
            newUser.setCreatedAt(new Date());

            boolean success = userDAO.createUser(newUser);
            if (success) {
                userDAO.updateUserPassword(newUser.getUserId(), password);
                HttpSession session = request.getSession();
                session.setAttribute("registeredUser", newUser);
                session.setAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
                response.sendRedirect("Login");
            } else {
                request.setAttribute("error", "Có lỗi xảy ra trong quá trình đăng ký!");
                request.getRequestDispatcher("/WEB-INF/jsp/Register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi hệ thống xảy ra!");
            request.getRequestDispatcher("/WEB-INF/jsp/Register.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Register Controller for WM System";
    }
}