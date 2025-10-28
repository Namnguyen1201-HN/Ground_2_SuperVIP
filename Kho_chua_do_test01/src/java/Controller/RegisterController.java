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
        request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String identificationId = request.getParameter("identificationId");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String terms = request.getParameter("terms");

        // Validate input
        if (fullName == null || identificationId == null || phone == null || email == null ||
            password == null || confirmPassword == null || terms == null ||
            fullName.trim().isEmpty() || identificationId.trim().isEmpty() || phone.trim().isEmpty() ||
            email.trim().isEmpty() || password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin!");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
            return;
        }

        // Validate password match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
            return;
        }

        // Validate password length
        if (password.length() < 6) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
            return;
        }

        // Validate phone format
        String cleanPhone = phone.replaceAll("\\s+", "");
        if (!cleanPhone.matches("\\d{10,11}")) {
            request.setAttribute("error", "Số điện thoại không hợp lệ (10-11 chữ số)!");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            request.setAttribute("error", "Email không hợp lệ!");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
            return;
        }

        // Validate identification ID format (9 or 12 digits)
        String cleanId = identificationId.replaceAll("\\s+", "");
        if (!cleanId.matches("\\d{9}|\\d{12}")) {
            request.setAttribute("error", "Căn cước công dân không hợp lệ (9 hoặc 12 chữ số)!");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
            return;
        }

        // Validate terms agreement
        if (!"on".equals(terms)) {
            request.setAttribute("error", "Vui lòng đồng ý với điều khoản dịch vụ!");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
            return;
        }

        try {
            // Check if phone exists
            if (userDAO.isPhoneExists(cleanPhone)) {
                request.setAttribute("error", "Số điện thoại đã được sử dụng!");
                request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
                return;
            }

            // Create new user
            User newUser = new User();
            newUser.setFullName(fullName.trim());
            newUser.setIdentificationId(cleanId);
            newUser.setPhone(cleanPhone);
            newUser.setEmail(email.trim());
            newUser.setPasswordHash(password);
            newUser.setRoleId(2); // Default role: Nhân viên bán hàng
            newUser.setIsActive(2); // Active by default

            // Insert user
            boolean success = userDAO.insertUser(newUser);
            if (success) {
                request.setAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
                HttpSession session = request.getSession();
                
                
                // Redirect to login after 2 seconds (via JavaScript)
                request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Có lỗi xảy ra trong quá trình đăng ký!");
                request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi hệ thống xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Register Controller for WM System";
    }
}
