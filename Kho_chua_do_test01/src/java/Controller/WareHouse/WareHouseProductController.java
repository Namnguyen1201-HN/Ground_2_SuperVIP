package Controller.WareHouse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

@WebServlet(name = "WareHouseProductController", urlPatterns = {"/WareHouseProduct"})
public class WareHouseProductController extends HttpServlet {

    // ===== Page size cố định ở đây =====
    private static final int PAGE_SIZE = 8; // đổi số này nếu muốn

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        // ===== lấy filter hiện có (GIỮ NGUYÊN CODE CŨ) =====
        String searchKeyword = request.getParameter("search");
        String[] categories = request.getParameterValues("category");
        String stockFilter = request.getParameter("stock");   // all | in | out
        String statusFilter = request.getParameter("status"); // all | active | inactive
        String priceFrom = request.getParameter("priceFrom");
        String priceTo = request.getParameter("priceTo");

        // Page param (mặc định 1)
        int currentPage = parseIntOrDefault(request.getParameter("page"), 1);
        if (currentPage < 1) currentPage = 1;

        // ===== DAO =====
        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        BrandDAO brandDAO = new BrandDAO();

        // ===== Lấy & lọc sản phẩm (GIỮ NGUYÊN CODE CŨ) =====
        List<Product> products;

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            products = productDAO.searchProductsByNameWithQty(searchKeyword.trim());
        } else {
            products = productDAO.getAllProductsWithQty();
        }

        // Lọc theo danh mục (nếu có)
        if (categories != null && categories.length > 0) {
            java.util.Set<String> selected = new java.util.HashSet<>();
            for (String c : categories) if (c != null && !c.trim().isEmpty()) selected.add(c.trim());
            if (!selected.isEmpty()) {
                products = products.stream()
                        .filter(p -> p.getCategoryId() != null && selected.contains(String.valueOf(p.getCategoryId())))
                        .collect(java.util.stream.Collectors.toList());
            }
        }

        // Lọc trạng thái (active/inactive)
        if (statusFilter != null && !"all".equalsIgnoreCase(statusFilter)) {
            boolean active = "active".equalsIgnoreCase(statusFilter);
            products = products.stream()
                    .filter(p -> p.getIsActive() != null && p.getIsActive() == active)
                    .collect(java.util.stream.Collectors.toList());
        }

        // Lọc tồn kho (in/out)
        if (stockFilter != null && !"all".equalsIgnoreCase(stockFilter)) {
            if ("in".equalsIgnoreCase(stockFilter)) {
                products = products.stream()
                        .filter(p -> p.getTotalQty() != null && p.getTotalQty() > 0)
                        .collect(java.util.stream.Collectors.toList());
            } else if ("out".equalsIgnoreCase(stockFilter)) {
                products = products.stream()
                        .filter(p -> p.getTotalQty() == null || p.getTotalQty() == 0)
                        .collect(java.util.stream.Collectors.toList());
            }
        }

        // Lọc theo khoảng giá
        if (priceFrom != null && !priceFrom.trim().isEmpty()) {
            try {
                double fromPrice = Double.parseDouble(priceFrom.trim());
                products = products.stream()
                        .filter(p -> p.getRetailPrice() != null && p.getRetailPrice().doubleValue() >= fromPrice)
                        .collect(java.util.stream.Collectors.toList());
            } catch (NumberFormatException ignore) { }
        }
        if (priceTo != null && !priceTo.trim().isEmpty()) {
            try {
                double toPrice = Double.parseDouble(priceTo.trim());
                products = products.stream()
                        .filter(p -> p.getRetailPrice() != null && p.getRetailPrice().doubleValue() <= toPrice)
                        .collect(java.util.stream.Collectors.toList());
            } catch (NumberFormatException ignore) { }
        }

        // =====================================================================================
        // ===== [NEW] Phân trang SAU LỌC (page size cố định) =====
        int totalItems = products.size();
        int totalPages = (totalItems == 0) ? 1 : (int) Math.ceil((double) totalItems / PAGE_SIZE);
        if (currentPage > totalPages) currentPage = totalPages;

        int fromIdx = Math.max(0, (currentPage - 1) * PAGE_SIZE);
        int toIdx = Math.min(fromIdx + PAGE_SIZE, totalItems);
        List<Product> pageItems = (fromIdx >= toIdx) ? java.util.Collections.emptyList() : products.subList(fromIdx, toIdx);

        int startItem = (totalItems == 0) ? 0 : (fromIdx + 1);
        int endItem   = (totalItems == 0) ? 0 : toIdx;

        // Xây base query giữ nguyên filter (không gồm page)
        String baseQuery = buildBaseQuery(searchKeyword, categories, stockFilter, statusFilter, priceFrom, priceTo);
        // =====================================================================================

        // ===== [OLD] Set nguyên list (đã thay bằng pageItems) =====
        // request.setAttribute("products", products);

        // ===== Set attributes cho JSP =====
        request.setAttribute("products", pageItems);
        request.setAttribute("categories", categoryDAO.getAll());
        request.setAttribute("brands", brandDAO.getAll());

        request.setAttribute("searchKeyword", searchKeyword);
        request.setAttribute("selectedCategories", categories);
        request.setAttribute("stockFilter", stockFilter);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("priceFrom", priceFrom);
        request.setAttribute("priceTo", priceTo);

        // Paging echo
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("startItem", startItem);
        request.setAttribute("endItem", endItem);
        request.setAttribute("baseQuery", baseQuery);

        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/Product.jsp").forward(request, response);
    }

    private static int parseIntOrDefault(String s, int d) {
        try { return Integer.parseInt(s); } catch (Exception e) { return d; }
    }

    private static String enc(String v) {
        return URLEncoder.encode(v == null ? "" : v, StandardCharsets.UTF_8);
    }

    /** Tạo query string giữ nguyên các filter, KHÔNG gồm page (để JSP append page). */
    private static String buildBaseQuery(String search, String[] categories,
                                         String stock, String status,
                                         String priceFrom, String priceTo) {
        StringBuilder sb = new StringBuilder();

        if (search != null && !search.trim().isEmpty()) {
            sb.append("&search=").append(enc(search.trim()));
        }
        if (categories != null && categories.length > 0) {
            for (String c : categories) {
                if (c != null && !c.trim().isEmpty()) {
                    sb.append("&category=").append(enc(c.trim()));
                }
            }
        }
        if (stock != null && !stock.trim().isEmpty() && !"all".equalsIgnoreCase(stock)) {
            sb.append("&stock=").append(enc(stock.trim()));
        }
        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            sb.append("&status=").append(enc(status.trim()));
        }
        if (priceFrom != null && !priceFrom.trim().isEmpty()) {
            sb.append("&priceFrom=").append(enc(priceFrom.trim()));
        }
        if (priceTo != null && !priceTo.trim().isEmpty()) {
            sb.append("&priceTo=").append(enc(priceTo.trim()));
        }

        return sb.toString();
    }

    @Override
    public String getServletInfo() {
        return "Danh sách hàng hóa (phân trang sau lọc, page size cố định).";
    }
}
