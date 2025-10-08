package Controller;

import DAL.UserDAO;
import Model.PasswordResetToken;
import Model.User;
import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import Service.MailService;

@WebServlet(name = "ForgotPasswordController", urlPatterns = {"/ForgotPassword"})
public class ForgotPasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        
        // If there's a token, show reset password form
        if (token != null && !token.isEmpty()) {
            UserDAO userDAO = new UserDAO();
            if (userDAO.isValidResetToken(token)) {
                request.setAttribute("token", token);
                request.getRequestDispatcher("/WEB-INF/jsp/includes/ResetPassword.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn. Vui lòng yêu cầu đặt lại mật khẩu mới.");
                request.getRequestDispatcher("/WEB-INF/jsp/includes/ResetPassword.jsp").forward(request, response);
            }
        } else {
            // Show forgot password form
            request.getRequestDispatcher("/WEB-INF/jsp/includes/ResetPassword.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("resetPassword".equals(action)) {
            // Handle password reset
            handleResetPassword(request, response);
        } else {
            // Handle forgot password request
            handleForgotPassword(request, response);
        }
    }
    
    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        
        // Validate email input
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập địa chỉ email.");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/ResetPassword.jsp").forward(request, response);
            return;
        }
        
        // Basic email format validation
        if (!isValidEmail(email)) {
            request.setAttribute("errorMessage", "Địa chỉ email không hợp lệ.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/jsp/includes/ResetPassword.jsp").forward(request, response);
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmail(email.trim());
        
        if (user != null) {
            try {
                // Generate a unique token
                String token = UUID.randomUUID().toString();
                
                // Save the token to database with expiry (24 hours from now)
                boolean tokenSaved = userDAO.createPasswordResetToken(user.getUserId(), token);
                
                if (tokenSaved) {
                    // Create reset URL
                    String resetUrl = request.getScheme() + "://" + request.getServerName() + ":" + 
                            request.getServerPort() + request.getContextPath() + 
                            "/ForgotPassword?token=" + token;
                    
                    // Send email with reset link using MailService
                    MailService mailService = new MailService(getServletContext());
                    String subject = "Đặt lại mật khẩu - SuperVIP Store";
                    String body = "<div style='font-family:Arial,sans-serif; color:#212529; line-height:1.6;'>"
                                  + "<div style='max-width:560px; margin:0 auto; padding:24px; border:1px solid #e5e7eb; border-radius:8px;'>"
                                  + "<h2 style='margin:0 0 8px; font-size:20px;'>Chào " + (user.getFullName() != null ? user.getFullName() : "bạn") + ",</h2>"
                                  + "<p style='margin:0 0 16px;'>Bạn vừa yêu cầu đặt lại mật khẩu. Nhấn nút bên dưới để đặt lại mật khẩu của bạn:</p>"
                                  + "<p style='margin:16px 0; text-align:center;'>"
                                  + "<a href='" + resetUrl + "' style='display:inline-block; background:#0d6efd; color:#ffffff; text-decoration:none; padding:12px 18px; border-radius:6px; font-weight:600;'>Đặt lại mật khẩu</a>"
                                  + "</p>"
                                  + "<p style='margin:0 0 8px; color:#6c757d; font-size:13px;'>Hoặc sao chép liên kết dưới đây vào trình duyệt:</p>"
                                  + "<p style='word-break:break-all; font-size:13px;'><a href='" + resetUrl + "' style='color:#0d6efd;'>" + resetUrl + "</a></p>"
                                  + "<hr style='border:none; border-top:1px solid #e5e7eb; margin:16px 0;'/>"
                                  + "<p style='margin:0 0 8px; font-size:13px; color:#6c757d;'>Nếu bạn không yêu cầu thao tác này, vui lòng bỏ qua email này.</p>"
                                  + "<p style='margin:0; font-size:13px;'>Trân trọng,<br/>SuperVIP Store</p>"
                                  + "</div>"
                                  + "</div>";
                    boolean emailSent = mailService.send(user.getEmail(), subject, body);
                    
                    if (emailSent) {
                        request.setAttribute("successMessage", "Một email đặt lại mật khẩu đã được gửi đến địa chỉ email của bạn. Vui lòng kiểm tra hộp thư và làm theo hướng dẫn.");
                    } else {
                        request.setAttribute("errorMessage", "Không thể gửi email đặt lại mật khẩu. Vui lòng thử lại sau hoặc liên hệ quản trị viên.");
                    }
                } else {
                    request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tạo token đặt lại mật khẩu. Vui lòng thử lại sau.");
                }
            } catch (Exception e) {
                System.err.println("Error in handleForgotPassword: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
            }
        } else {
            // For security reasons, don't reveal that the email doesn't exist
            request.setAttribute("successMessage", "Nếu địa chỉ email tồn tại trong hệ thống, một email đặt lại mật khẩu sẽ được gửi đến địa chỉ email của bạn.");
        }
        
        request.getRequestDispatcher("/WEB-INF/jsp/includes/ForgotPassword.jsp").forward(request, response);
    }
    
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate inputs
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Token không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/jsp/includes/ForgotPassword.jsp").forward(request, response);
            return;
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập mật khẩu mới.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/jsp/includes/ResetPassword.jsp").forward(request, response);
            return;
        }
        
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng xác nhận mật khẩu mới.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/jsp/includes/ResetPassword.jsp").forward(request, response);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp với mật khẩu mới.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/jsp/includes/ResetPassword.jsp").forward(request, response);
            return;
        }
        
        if (newPassword.length() < 6) {
            request.setAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/jsp/includes/ResetPassword.jsp").forward(request, response);
            return;
        }
        
        try {
            UserDAO userDAO = new UserDAO();
            boolean resetSuccess = userDAO.resetPassword(token, newPassword);
            
            if (resetSuccess) {
                request.setAttribute("successMessage", "Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập với mật khẩu mới.");
                request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Token không hợp lệ, đã hết hạn hoặc đã được sử dụng. Vui lòng yêu cầu đặt lại mật khẩu mới.");
                request.getRequestDispatcher("/WEB-INF/jsp/includes/ForgotPassword.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error in handleResetPassword: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống khi đặt lại mật khẩu. Vui lòng thử lại sau.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/jsp/includes/ResetPassword.jsp").forward(request, response);
        }
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.trim().matches(emailRegex);
    }
}
