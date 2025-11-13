package Controller.Admin;

import DAL.ProductDAO;
import DAL.CategoryDAO;
import DAL.BrandDAO;
import DAL.SupplierDAO;
import DAL.BranchDAO;

import Model.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ProductController", urlPatterns = {"/product"})
public class ProductController extends HttpServlet {

    private static final int DEFAULT_STOCK_THRESHOLD = 30;
    private static final int DEFAULT_BRANCH_ID_FOR_QTY = 1;

    private ProductDAO  productDAO;
    private BrandDAO    brandDAO;
    private SupplierDAO supplierDAO;
    private CategoryDAO categoryDAO;
    private BranchDAO   branchDAO;

    @Override
    public void init() {
        productDAO  = new ProductDAO();
        brandDAO    = new BrandDAO();
        supplierDAO = new SupplierDAO();
        categoryDAO = new CategoryDAO();
        branchDAO   = new BranchDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.isBlank()) action = "list";

        try {
            switch (action) {
                case "add"       -> showAddForm(request, response);
                case "edit"      -> showEditForm(request, response);
                case "delete"    -> deleteProduct(request, response);
                case "detail"    -> showDetail(request, response);
                case "inventory" -> listInventoryByBranchPaged(request, response);
                case "list"      -> listProducts(request, response);
                default          -> listProducts(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi khi xử lý yêu cầu");
        }
    }

    /* ===================== INVENTORY VIEW (ProductDTO) ===================== */
    private void listInventoryByBranchPaged(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session = req.getSession(true);
        Integer branchId = DEFAULT_BRANCH_ID_FOR_QTY;
        Object branchIdObj = session.getAttribute("branchId");
        if (branchIdObj != null) {
            try { branchId = Integer.parseInt(branchIdObj.toString()); } catch (NumberFormatException ignore) {}
        }

        int pageSize = parseIntOrDefault(req.getParameter("pageSize"), 10);
        int page     = parseIntOrDefault(req.getParameter("page"), 1);
        page = Math.max(page, 1);
        int offset = (page - 1) * pageSize;

        var products = productDAO.getInventoryProductListByPageByBranchId(branchId, offset, pageSize);
        int totalProducts = productDAO.countProductsByBranchId(branchId);
        int totalPages = productDAO.calcTotalPages(totalProducts, pageSize);

        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("startProduct", totalProducts == 0 ? 0 : offset + 1);
        req.setAttribute("endProduct", Math.min(offset + pageSize, totalProducts));
        req.setAttribute("pageSize", pageSize);
        req.setAttribute("products", products);

        req.getRequestDispatcher("/WEB-INF/jsp/manager/products.jsp").forward(req, resp);
    }

    /* ========================= LISTING (Product) ========================= */
    private void listProducts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String productName = trimToNull(request.getParameter("productName"));
        if (productName == null) productName = trimToNull(request.getParameter("keyword"));

        String[] rawCatNames = request.getParameterValues("categoryName");
        List<String> categoryNames = parseStrList(rawCatNames);
        if (categoryNames == null || categoryNames.isEmpty()) {
            List<String> fromCsv = parseCsvStrList(trimToNull(request.getParameter("categoryName")));
            if (fromCsv != null && !fromCsv.isEmpty()) categoryNames = fromCsv;
        }

        String stock = request.getParameter("stock");
        if (stock == null || stock.isBlank()) stock = "all";
        int threshold = parseIntOrDefault(request.getParameter("stockThreshold"), DEFAULT_STOCK_THRESHOLD);

        // === Phân trang ===
        int pageSize = parseIntOrDefault(request.getParameter("pageSize"), 10);
        if (pageSize != 10 && pageSize != 20 && pageSize != 30) pageSize = 10; // bảo vệ
        int page = parseIntOrDefault(request.getParameter("page"), 1);
        page = Math.max(page, 1);

        // === Tổng & danh sách theo trang ===
        int totalProducts = productDAO.countProducts(
                categoryNames,
                productName,
                ProductDAO.StockFilter.from(stock),
                threshold
        );
        int totalPages = productDAO.calcTotalPages(totalProducts, pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        var products = productDAO.listProductsByPage(
                categoryNames,
                productName,
                ProductDAO.StockFilter.from(stock),
                threshold,
                page,
                pageSize
        );

        int offset = (page - 1) * pageSize;

        // Set attribute cho view
        request.setAttribute("products", products);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("startProduct", totalProducts == 0 ? 0 : offset + 1);
        request.setAttribute("endProduct", Math.min(offset + pageSize, totalProducts));
        request.setAttribute("pageSize", pageSize);

        // Giữ lại filter/search
        request.setAttribute("productName", productName);
        request.setAttribute("keyword", productName);
        request.setAttribute("selectedCategoryNames", categoryNames);
        request.setAttribute("stock", stock);
        request.setAttribute("stockThreshold", threshold);

        // Sidebar
        request.setAttribute("categories", categoryDAO.getAll());

        request.getRequestDispatcher("/WEB-INF/jsp/admin/product.jsp").forward(request, response);
    }

    /* ========================= FORMS/DETAIL ========================= */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute("action", "insert");
        pushLookups(request);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/ProductForm.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }
        Product product = safeGetByIdWithQty(id);
        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy sản phẩm");
            return;
        }
        request.setAttribute("product", product);
        request.setAttribute("action", "update");
        pushLookups(request);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/ProductForm.jsp").forward(request, response);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }
        Product product = safeGetByIdWithQty(id);
        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy sản phẩm");
            return;
        }
        request.setAttribute("product", product);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/test.jsp").forward(request, response);
    }

    private Product safeGetByIdWithQty(Integer id) {
        try {
            return productDAO.getProductByIdWithQty(id);
        } catch (NoSuchMethodError | RuntimeException ex) {
            return productDAO.getProductById(id);
        }
    }

    /* ========================= CRUD ========================= */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "insert" -> insertProduct(request, response);
                case "update" -> updateProduct(request, response);
                default       -> doGet(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi khi xử lý dữ liệu gửi lên");
        }
    }

    private void insertProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Product p = extractProductFromRequest(request);
        productDAO.insertProduct(p);
        response.sendRedirect("product?action=list");
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }
        Product p = extractProductFromRequest(request);
        p.setProductId(id);
        productDAO.updateProduct(p);

        String rawQty = trimToNull(request.getParameter("quantity"));
        if (rawQty != null) {
            try {
                int newQty = Integer.parseInt(rawQty.trim());
                HttpSession session = request.getSession(false);
                Integer branchId = DEFAULT_BRANCH_ID_FOR_QTY;
                if (session != null && session.getAttribute("branchId") != null) {
                    try { branchId = Integer.parseInt(session.getAttribute("branchId").toString()); }
                    catch (NumberFormatException ignore) {}
                }
                productDAO.setQuantityForProductAtBranch(id, branchId, newQty);
            } catch (NumberFormatException ignore) { }
        }

        response.sendRedirect("product?action=list");
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }
        productDAO.deleteProduct(id);
        response.sendRedirect("product?action=list");
    }

    /* ========================= HELPERS ========================= */
    private void pushLookups(HttpServletRequest req) {
        req.setAttribute("brands",     brandDAO.getAll());
        req.setAttribute("categories", categoryDAO.getAll());
        req.setAttribute("suppliers",  supplierDAO.getAllSuppliers());
        req.setAttribute("branches",   branchDAO.getAllBranches());
    }

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }
    private int parseIntOrDefault(String s, int def) {
        try { return (s == null || s.isBlank()) ? def : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return def; }
    }
    private BigDecimal parseBigDecimalOrNull(String s) {
        try { return (s == null || s.isBlank()) ? null : new BigDecimal(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }
    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
    private List<String> parseStrList(String[] arr) {
        if (arr == null || arr.length == 0) return null;
        List<String> out = new ArrayList<>();
        for (String s : arr) {
            String v = trimToNull(s);
            if (v != null) out.add(v);
        }
        return out.isEmpty() ? null : out;
    }
    private List<String> parseCsvStrList(String csv) {
        if (csv == null || csv.isBlank()) return null;
        String[] parts = csv.split(",");
        return parseStrList(parts);
    }
    private Product extractProductFromRequest(HttpServletRequest request) {
        Product p = new Product();
        p.setProductName(trimToNull(request.getParameter("name")));
        p.setBrandName(trimToNull(request.getParameter("brandName")));
        p.setCategoryName(trimToNull(request.getParameter("categoryName")));
        p.setSupplierName(trimToNull(request.getParameter("supplierName")));
        p.setCostPrice(parseBigDecimalOrNull(request.getParameter("costPrice")));
        p.setRetailPrice(parseBigDecimalOrNull(request.getParameter("retailPrice")));
        p.setVat(parseBigDecimalOrNull(request.getParameter("vat")));
        p.setImageUrl(trimToNull(request.getParameter("imageUrl")));
        String rawIsActive = request.getParameter("isActive");
        Boolean isActive = null;
        if (rawIsActive != null) {
            isActive = ("on".equalsIgnoreCase(rawIsActive) || "true".equalsIgnoreCase(rawIsActive));
        }
        p.setIsActive(isActive);
        return p;
    }
}
