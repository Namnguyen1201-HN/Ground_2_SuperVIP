/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package Controller;

import DAL.CustomerRequestDAO;
import Model.CustomerRequest;
import Model.User;
import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author admin
 */
@WebServlet(name="CustomerRequestController", urlPatterns={"/CustomerRequests"})
public class CustomerRequestController extends HttpServlet {
    private CustomerRequestDAO customerRequestDAO;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");
        if ( user == null ){
            request.getRequestDispatcher("WEB-INF/jsp/Login.jsp").forward(request, response);
            return;
        }
        customerRequestDAO = new CustomerRequestDAO();

        int pageSize = 10;
        int page;
        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page < 1) page = 1;
        } catch (Exception e) {
            page = 1;
        }

        int userId;
        try {
            userId = Integer.parseInt(request.getParameter("userId"));
        } catch (Exception e) {
            userId = 0;
        }

        String keyword = request.getParameter("q");
        String status = request.getParameter("status");
        String from = request.getParameter("from");
        String to = request.getParameter("to");

        java.sql.Date fromDate = null;
        java.sql.Date toDate = null;
        try {
            if (from != null && !from.trim().isEmpty()) {
                fromDate = java.sql.Date.valueOf(from);
            }
        } catch (Exception ex) { /* ignore invalid date */ }
        try {
            if (to != null && !to.trim().isEmpty()) {
                toDate = java.sql.Date.valueOf(to);
            }
        } catch (Exception ex) { /* ignore invalid date */ }

        int total = customerRequestDAO.getTotalCustomerRequestsFiltered(userId, keyword, status, fromDate, toDate);
        int totalPages = (int) Math.ceil((double) total / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        List<CustomerRequest> requests = customerRequestDAO.getCustomerRequestsByUserIdPagedFiltered(userId, page, pageSize, keyword, status, fromDate, toDate);
        int startIndex = total == 0 ? 0 : (page - 1) * pageSize + 1;
        int endIndex = Math.min(page * pageSize, total);

        request.setAttribute("requests", requests);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRequests", total);
        request.setAttribute("startRequest", startIndex);
        request.setAttribute("endRequest", endIndex);
        request.setAttribute("userId", userId);
        request.setAttribute("q", keyword == null ? "" : keyword);
        request.setAttribute("status", status == null ? "all" : status);
        request.setAttribute("from", from == null ? "" : from);
        request.setAttribute("to", to == null ? "" : to);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/customer_requests.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
    }
}



