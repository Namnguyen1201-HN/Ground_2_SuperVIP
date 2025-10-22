/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Admin;

import DAL.UserDAO;
import Model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author TieuPham
 */
@WebServlet(name = "ChangePassWordController", urlPatterns = {"/ChangePassWord"})
public class ChangePassWordController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/change_password.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        //Cần phải thay đổi dòng dưới để có thể get đúng session tài khoản đăng nhập
        User currentUser = (User) session.getAttribute("account"); // giả sử bạn lưu user khi đăng nhập

        if (currentUser == null) {
            response.sendRedirect("Login");
            return;
        }

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

        // 2️⃣ Kiểm tra mật khẩu hiện tại
        UserDAO dao = new UserDAO();
        User dbUser = dao.getUserById(currentUser.getUserId());
        if (dbUser == null || !dbUser.getPasswordHash().equals(currentPassword)) {
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
            currentUser.setPasswordHash(newPassword);
            session.setAttribute("account", currentUser); // cập nhật lại session
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
        return "Short description";
    }

}
