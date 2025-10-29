package Controller.Admin;

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

@WebServlet(name = "ChangePassWordController", urlPatterns = {"/ChangePassWord"})
public class ChangePassWordController extends HttpServlet {

    // ✅ Hàm băm SHA-256
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
            throw new RuntimeException("Lỗi khi mã hóa mật khẩu", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/admin/change_password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            setMessage(request, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!", "warning");
            request.getRequestDispatcher("Logout").forward(request, response);
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        UserDAO dao = new UserDAO();

        // --- Lấy dữ liệu từ form ---
        String currentPassword = trim(request.getParameter("currentPassword"));
        String newPassword = trim(request.getParameter("newPassword"));
        String confirmPassword = trim(request.getParameter("confirmPassword"));

        // --- Validate cơ bản ---
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            setMessage(request, "Vui lòng nhập đầy đủ thông tin!", "warning");
            forward(request, response);
            return;
        }

        User dbUser = dao.getUserById(currentUser.getUserId());
        String hashedInput = hashSHA256(currentPassword);

        // --- Kiểm tra mật khẩu hiện tại ---
        if (dbUser == null || dbUser.getPasswordHash() == null || !dbUser.getPasswordHash().equals(hashedInput)) {
            setMessage(request, "Mật khẩu hiện tại không chính xác!", "danger");
            forward(request, response);
            return;
        }

        // --- Kiểm tra mật khẩu mới ---
        if (!newPassword.equals(confirmPassword)) {
            setMessage(request, "Mật khẩu xác nhận không khớp!", "danger");
            forward(request, response);
            return;
        }

        if (newPassword.equals(currentPassword)) {
            setMessage(request, "Mật khẩu mới phải khác mật khẩu hiện tại!", "warning");
            forward(request, response);
            return;
        }

        if (newPassword.length() < 6) {
            setMessage(request, "Mật khẩu mới phải có ít nhất 6 ký tự!", "warning");
            forward(request, response);
            return;
        }

        // --- Cập nhật mật khẩu ---
        boolean updated = dao.updatePassword(currentUser.getUserId(), newPassword);

        if (updated) {
            currentUser.setPasswordHash(hashSHA256(newPassword));
            session.setAttribute("currentUser", currentUser);
            setMessage(request, "Đổi mật khẩu thành công!", "success");
        } else {
            setMessage(request, "Đổi mật khẩu thất bại, vui lòng thử lại!", "danger");
        }

        forward(request, response);
    }

    // === Helper Methods ===
    private void setMessage(HttpServletRequest req, String msg, String type) {
        req.setAttribute("message", msg);
        req.setAttribute("msgType", type);
    }

    private void forward(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/admin/change_password.jsp").forward(req, res);
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    @Override
    public String getServletInfo() {
        return "Controller đổi mật khẩu người dùng (Admin & User)";
    }
}
