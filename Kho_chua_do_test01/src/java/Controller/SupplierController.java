/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package Controller;

import DAL.SupplierDAO;
import Model.Supplier;
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
@WebServlet(name="SupplierController", urlPatterns={"/Supplier"})
public class SupplierController extends HttpServlet {
   private SupplierDAO supplierDAO;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    } 
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        supplierDAO = new SupplierDAO();
        int pageSize = 10;
        int page;
        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page < 1) page = 1;
        } catch (Exception e) {
            page = 1;
        }

        int total = supplierDAO.getTotalSuppliers();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        List<Supplier> suppliers = supplierDAO.getSuppliersPaged(page, pageSize);
        int startIndex = total == 0 ? 0 : (page - 1) * pageSize + 1;
        int endIndex = Math.min(page * pageSize, total);

        request.setAttribute("suppliers", suppliers);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalSuppliers", total);
        request.setAttribute("startSupplier", startIndex);
        request.setAttribute("endSupplier", endIndex);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/supplier.jsp");
        dispatcher.forward(request, response);
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
