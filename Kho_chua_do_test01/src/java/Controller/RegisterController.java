package Controller;

import DAL.UserDAO;
import Model.User;
import java.io.IOException;
import java.security.MessageDigest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "RegisterController", urlPatterns = {"/Register"})
public class RegisterController extends HttpServlet {

    // --- Constants for clarity and easier maintenance ---
    private static final int DEFAULT_ROLE_ID = 2; // Default role: Sales Staff
    private static final int STATUS_UNVERIFIED = 2; // Status for newly registered accounts needing verification/activation

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
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

        // --- Centralized Validation ---
        String validationError = validateInput(fullName, identificationId, phone, email, password, confirmPassword, terms);
        if (validationError != null) {
            forwardToRegisterWithError(request, response, validationError);
            return;
        }

        String cleanPhone = phone.replaceAll("\\s+", "");
        String cleanId = identificationId.replaceAll("\\s+", "");
        
        try {
            if (userDAO.isIdentificationIdExists(cleanId)) {
                forwardToRegisterWithError(request, response, "Số CCCD này đã được sử dụng!");
                return;
            }

            // Check if phone or email already exists
            if (userDAO.isPhoneExists(cleanPhone)) {
                forwardToRegisterWithError(request, response, "Số điện thoại này đã được sử dụng!");
                return;
            }
            if (userDAO.isEmailExists(email.trim())) {
                forwardToRegisterWithError(request, response, "Email này đã được sử dụng!");
                return;
            }

            // Create new user object
            User newUser = new User();
            newUser.setFullName(fullName.trim());
            newUser.setIdentificationId(identificationId.replaceAll("\\s+", ""));
            newUser.setPhone(cleanPhone);
            newUser.setEmail(email.trim());

            newUser.setPasswordHash(password);

            // Use constants for role and status
            newUser.setRoleId(DEFAULT_ROLE_ID);
            newUser.setIsActive(STATUS_UNVERIFIED);

            // Insert user into the database
            boolean success = userDAO.insertUser(newUser);

            if (success) {
                // 3. Use Post-Redirect-Get pattern for better UX
                HttpSession session = request.getSession();
                session.setAttribute("successMessage", "Đăng ký thành công! Vui lòng chờ quản trị viên kích hoạt tài khoản.");
                response.sendRedirect(request.getContextPath() + "/Login");
            } else {
                forwardToRegisterWithError(request, response, "Đã có lỗi xảy ra trong quá trình đăng ký. Vui lòng thử lại.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Should be replaced with a proper logger
            forwardToRegisterWithError(request, response, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    /**
     * Validates all registration form inputs.
     *
     * @return An error message string if validation fails, otherwise null.
     */
    private String validateInput(String fullName, String id, String phone, String email, String pass, String confirmPass, String terms) {
        if (fullName == null || fullName.trim().isEmpty() || id == null || id.trim().isEmpty()
                || phone == null || phone.trim().isEmpty() || email == null || email.trim().isEmpty()
                || pass == null || pass.isEmpty() || confirmPass == null || confirmPass.isEmpty()) {
            return "Vui lòng điền đầy đủ tất cả các trường bắt buộc.";
        }
        if (!"on".equals(terms)) {
            return "Bạn phải đồng ý với các điều khoản dịch vụ để đăng ký.";
        }
        if (pass.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự.";
        }
        if (!pass.equals(confirmPass)) {
            return "Mật khẩu xác nhận không khớp.";
        }
        if (!phone.replaceAll("\\s+", "").matches("^0\\d{9}$")) {
            return "Số điện thoại không hợp lệ (Bắt đầu bằng 0 và có 10 chữ số).";
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return "Định dạng email không hợp lệ.";
        }
        if (!id.replaceAll("\\s+", "").matches("\\d{12}")) {
            return "Số CMND/CCCD không hợp lệ (phải có 12 chữ số).";
        }
        return null; // All validations passed
    }

    /**
     * Helper method to forward back to the registration page with an error
     * message.
     */
    private void forwardToRegisterWithError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        // Also forward back the user's input so they don't have to re-type everything
        request.setAttribute("fullName", request.getParameter("fullName"));
        request.setAttribute("identificationId", request.getParameter("identificationId"));
        request.setAttribute("phone", request.getParameter("phone"));
        request.setAttribute("email", request.getParameter("email"));
        request.getRequestDispatcher("/WEB-INF/jsp/includes/Register.jsp").forward(request, response);
    }

    /**
     * Hashes a string using the SHA-256 algorithm.
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
            throw new RuntimeException("Error hashing password", e);
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles user registration.";
    }
}
