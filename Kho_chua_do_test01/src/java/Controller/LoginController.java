/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package Controller;

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
 * 
 *
 * @author TieuPham
 */
@WebServlet(name="LoginController", urlPatterns={"/Login"})
public class LoginController extends HttpServlet {
    private UserDAO userDAO;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    } 

    @Override
    public void init() {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Hiển thị trang đăng nhập
        request.getRequestDispatcher("/WEB-INF/jsp/Login.jsp").forward(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");
        
        // Validate input
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập đầy đủ thông tin đăng nhập!");
            request.getRequestDispatcher("jsp/Login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Authenticate user (giả lập xác thực)
            User user = userDAO.authenticateUser(username, password);
            
            if (user != null) {
                // Đăng nhập thành công
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);
                session.setAttribute("username", user.getUsername());
                
                // Nếu chọn ghi nhớ đăng nhập
                if ("on".equals(remember)) {
                    session.setMaxInactiveInterval(30 * 24 * 60 * 60); // 30 days
                } else {
                    session.setMaxInactiveInterval(30 * 60); // 30 minutes
                }
                
                // Redirect to dashboard
                response.sendRedirect("TongQuan");
                
            } else {
                // Đăng nhập thất bại
                request.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác!");
                request.getRequestDispatcher("jsp/Login.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Có lỗi xảy ra trong quá trình đăng nhập!");
            request.getRequestDispatcher("jsp/Login.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Login Controller for WM System";
    }
}