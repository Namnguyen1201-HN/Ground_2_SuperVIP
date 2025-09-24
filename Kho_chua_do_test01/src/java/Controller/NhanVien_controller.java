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
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author TieuPham
 */
@WebServlet(name = "NhanVien_controller", urlPatterns = {"/NhanVien"})
public class NhanVien_controller extends HttpServlet {

    private UserDAO dao = new UserDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String search = request.getParameter("search"); // keyword tìm kiếm
        String statusParam = request.getParameter("status");
        String roleParam = request.getParameter("role");
        
        List<User> users = dao.getAllUsers();

        List<User> result;

        if (search != null && !search.trim().isEmpty()) {
            // ===== PHẦN TÌM KIẾM =====
            String keyword = search.trim().toLowerCase();
            result = users.stream()
                    .filter(u -> String.valueOf(u.getUserId()).equals(keyword)
                    || u.getFullName().toLowerCase().contains(keyword))
                    .toList();

            request.setAttribute("searchKeyword", search); // để hiển thị lại ô input

        } else {
            // ===== PHẦN LỌC =====
            // gán về final/effectively final
            final String status = (statusParam == null) ? "all" : statusParam;
            final String role = (roleParam == null) ? "None" : roleParam;

            result = users.stream()
                    .filter(u -> {
                        if ("active".equalsIgnoreCase(status) && !u.isActive()) {
                            return false;
                        }
                        if ("inactive".equalsIgnoreCase(status) && u.isActive()) {
                            return false;
                        }
                        return true;
                    })
                    .filter(u -> {
                        if (!"None".equalsIgnoreCase(role) && !role.equalsIgnoreCase(u.getRoleName())) {
                            return false;
                        }
                        return true;
                    })
                    .toList();

            request.setAttribute("selectedStatus", status);
            request.setAttribute("selectedRole", role);
        }

        request.setAttribute("users", result);
        request.getRequestDispatcher("/WEB-INF/jsp/Nhan_vien.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
