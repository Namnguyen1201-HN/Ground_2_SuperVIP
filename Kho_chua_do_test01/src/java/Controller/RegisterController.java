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
import java.util.Date;

/**
 *
 * @author TieuPham
 */
@WebServlet(name="Register_controller", urlPatterns={"/Register"})
public class RegisterController extends HttpServlet {
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
        // Hiển thị trang đăng ký
        request.getRequestDispatcher("/WEB-INF/jsp/Register.jsp").forward(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String countryCode = request.getParameter("countryCode");
        String country = request.getParameter("country");
        String region = request.getParameter("region");
        String captcha = request.getParameter("captcha");
        String acceptTerms = request.getParameter("acceptTerms");
        
        // Validate input
        if (fullName == null || phone == null || country == null || region == null || 
            captcha == null || acceptTerms == null ||
            fullName.trim().isEmpty() || phone.trim().isEmpty() || 
            country.trim().isEmpty() || region.trim().isEmpty() || 
            captcha.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin và đồng ý điều khoản!");
            request.getRequestDispatcher("jsp/Register.jsp").forward(request, response);
            return;
        }
        
        // Validate phone format
        String cleanPhone = phone.replaceAll("\\s+", "");
        if (!cleanPhone.matches("\\d{10,11}")) {
            request.setAttribute("errorMessage", "Số điện thoại không hợp lệ!");
            request.getRequestDispatcher("jsp/Register.jsp").forward(request, response);
            return;
        }
        
        // Validate captcha (giả lập)
        HttpSession session = request.getSession();
        String sessionCaptcha = (String) session.getAttribute("captcha");
        if (sessionCaptcha == null || !sessionCaptcha.equals(captcha)) {
            request.setAttribute("errorMessage", "Mã xác thực không chính xác!");
            request.getRequestDispatcher("jsp/Register.jsp").forward(request, response);
            return;
        }
        
        try {
            // Check if user already exists
            if (userDAO.isPhoneExists(cleanPhone)) {
                request.setAttribute("errorMessage", "Số điện thoại đã được sử dụng!");
                request.getRequestDispatcher("jsp/Register.jsp").forward(request, response);
                return;
            }
            
            // Create new user
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setPhone(countryCode + cleanPhone);
            newUser.setUsername(generateUsername(fullName));
            newUser.setEmail(""); // Will be set later
            newUser.setRoleName("User");
            newUser.setActive(true);
            newUser.setCreatedAt(new Date());
            
            // Save user to database
            boolean success = userDAO.createUser(newUser);
            
            if (success) {
                // Registration successful
                session.setAttribute("registeredUser", newUser);
                session.setAttribute("successMessage", "Đăng ký thành công! Vui lòng hoàn tất thông tin tài khoản.");
                
                // Redirect to complete registration or login
                response.sendRedirect("Login");
                
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra trong quá trình đăng ký!");
                request.getRequestDispatcher("jsp/Register.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi hệ thống xảy ra!");
            request.getRequestDispatcher("jsp/Register.jsp").forward(request, response);
        }
    }
    
    private String generateUsername(String fullName) {
        // Generate username from full name
        String username = fullName.toLowerCase()
                                 .replaceAll("\\s+", "")
                                 .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                                 .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                                 .replaceAll("[ìíịỉĩ]", "i")
                                 .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                                 .replaceAll("[ùúụủũưừứựửữ]", "u")
                                 .replaceAll("[ỳýỵỷỹ]", "y")
                                 .replaceAll("[đ]", "d")
                                 .replaceAll("[^a-zA-Z0-9]", "");
        
        return username + System.currentTimeMillis() % 1000;
    }

    @Override
    public String getServletInfo() {
        return "Register Controller for WM System";
    }
}