/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.WareHouse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import DAL.ProductDAO;
import DAL.CategoryDAO;
import DAL.BrandDAO;
import Model.Product;
import Model.Category;
import Model.Brand;

/**
 *
 * @author TieuPham
 */
@WebServlet(name = "WareHouseProductController", urlPatterns = {"/WareHouseProduct"})
public class WareHouseProductController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Lấy tham số từ request
        String searchKeyword = request.getParameter("search");
        String[] categories = request.getParameterValues("category");
        String stockFilter = request.getParameter("stock");
        String statusFilter = request.getParameter("status");
        String priceFrom = request.getParameter("priceFrom");
        String priceTo = request.getParameter("priceTo");

        // Khởi tạo DAO
        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        BrandDAO brandDAO = new BrandDAO();

        // Lấy danh sách sản phẩm theo điều kiện lọc
        List<Product> products;

        // Xử lý tìm kiếm và lọc
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // Tìm kiếm theo tên sản phẩm
            products = productDAO.searchProductsByNameWithQty(searchKeyword.trim());
        } else {
            // Lấy tất cả sản phẩm
            products = productDAO.getAllProductsWithQty();
        }

        // Áp dụng bộ lọc trạng thái
        if (statusFilter != null && !statusFilter.equals("all")) {
            boolean isActive = "active".equals(statusFilter);
            products = products.stream()
                    .filter(p -> p.getIsActive() != null && p.getIsActive() == isActive)
                    .collect(java.util.stream.Collectors.toList());
        }

        // Áp dụng bộ lọc tồn kho
        if (stockFilter != null && !stockFilter.equals("all")) {
            if ("in".equals(stockFilter)) {
                products = products.stream()
                        .filter(p -> p.getTotalQty() != null && p.getTotalQty() > 0)
                        .collect(java.util.stream.Collectors.toList());
            } else if ("out".equals(stockFilter)) {
                products = products.stream()
                        .filter(p -> p.getTotalQty() == null || p.getTotalQty() == 0)
                        .collect(java.util.stream.Collectors.toList());
            }
        }

        // Áp dụng bộ lọc khoảng giá
        if (priceFrom != null && !priceFrom.trim().isEmpty()) {
            try {
                double fromPrice = Double.parseDouble(priceFrom);
                products = products.stream()
                        .filter(p -> p.getRetailPrice() != null && p.getRetailPrice().doubleValue() >= fromPrice)
                        .collect(java.util.stream.Collectors.toList());
            } catch (NumberFormatException e) {
                // Bỏ qua lỗi parse
            }
        }

        if (priceTo != null && !priceTo.trim().isEmpty()) {
            try {
                double toPrice = Double.parseDouble(priceTo);
                products = products.stream()
                        .filter(p -> p.getRetailPrice() != null && p.getRetailPrice().doubleValue() <= toPrice)
                        .collect(java.util.stream.Collectors.toList());
            } catch (NumberFormatException e) {
                // Bỏ qua lỗi parse
            }
        }

        // Áp dụng bộ lọc danh mục
        if (categories != null && categories.length > 0) {
            java.util.Set<String> categorySet = java.util.Arrays.stream(categories)
                    .filter(c -> c != null && !c.trim().isEmpty())
                    .collect(java.util.stream.Collectors.toSet());

            if (!categorySet.isEmpty()) {
                products = products.stream()
                        .filter(p -> p.getCategoryName() != null && categorySet.contains(p.getCategoryName()))
                        .collect(java.util.stream.Collectors.toList());
            }
        }

        // Lấy danh sách categories và brands cho dropdown
        List<Category> categoriesList = categoryDAO.getAll();
        List<Brand> brandsList = brandDAO.getAll();

        // Set attributes cho JSP
        request.setAttribute("products", products);
        request.setAttribute("categories", categoriesList);
        request.setAttribute("brands", brandsList);
        request.setAttribute("searchKeyword", searchKeyword);
        request.setAttribute("selectedCategories", categories);
        request.setAttribute("stockFilter", stockFilter);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("priceFrom", priceFrom);
        request.setAttribute("priceTo", priceTo);

        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/Product.jsp").forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
