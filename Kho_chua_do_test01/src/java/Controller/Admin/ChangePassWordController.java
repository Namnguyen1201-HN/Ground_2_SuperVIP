package Controller.Admin;

import DAL.UserDAO;
import Model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ChangePassWordController", urlPatterns = {"/ChangePassWord"})
public class ChangePassWordController extends HttpServlet {

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/admin/change_password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("currentUser") == null) {
            request.setAttribute("message", "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!");
            request.setAttribute("msgType", "warning");
            request.getRequestDispatcher("Logout").forward(request, response);
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");

        // 🔹 Lấy dữ liệu từ form
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // 1️⃣ Kiểm tra rỗng
        if (currentPassword == null || newPassword == null || confirmPassword == null
                || currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("message", "Vui lòng nhập đầy đủ thông tin.");
            request.setAttribute("msgType", "warning");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/change_password.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();
        User dbUser = dao.getUserById(currentUser.getUserId());

        String hashedInput = hashSHA256(currentPassword);

        // 2️⃣ Kiểm tra mật khẩu hiện tại (nếu có hash, bạn cần sửa đoạn này)
        if (dbUser == null || dbUser.getPasswordHash() == null || !dbUser.getPasswordHash().equals(hashedInput)) {
            request.setAttribute("message", "Mật khẩu hiện tại không chính xác!");
            request.setAttribute("msgType", "danger");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/change_password.jsp").forward(request, response);
            return;
        }

        // 3️⃣ Kiểm tra mật khẩu mới
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("message", "Mật khẩu xác nhận không khớp!");
            request.setAttribute("msgType", "danger");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/change_password.jsp").forward(request, response);
            return;
        }

        if (newPassword.equals(currentPassword)) {
            request.setAttribute("message", "Mật khẩu mới phải khác mật khẩu hiện tại!");
            request.setAttribute("msgType", "warning");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/change_password.jsp").forward(request, response);
            return;
        }

        // 4️⃣ Cập nhật DB
        boolean updated = dao.updatePassword(currentUser.getUserId(), newPassword);

        if (updated) {
            // Cập nhật lại đối tượng trong session
            currentUser.setPasswordHash(hashSHA256(newPassword)); // ✅ Lưu đúng dạng hash
            session.setAttribute("currentUser", currentUser);

            request.setAttribute("message", "Đổi mật khẩu thành công!");
            request.setAttribute("msgType", "success");
        } else {
            request.setAttribute("message", "Đổi mật khẩu thất bại, vui lòng thử lại!");
            request.setAttribute("msgType", "danger");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/admin/change_password.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Controller đổi mật khẩu người dùng (Admin & User)";
    }
}
