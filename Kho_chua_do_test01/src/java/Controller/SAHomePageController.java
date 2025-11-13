package Controller;

import DAL.ProductDAO;
import DAL.CategoryDAO;
import DAL.BrandDAO;
import DAL.SupplierDAO;
import DAL.BranchDAO;

import Model.Product;
import Model.UserDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "SAHomePageController", urlPatterns = {"/sale"})
public class SAHomePageController extends HttpServlet {

    private static final int DEFAULT_STOCK_THRESHOLD = 30;
    private static final int DEFAULT_BRANCH_ID_FOR_QTY = 1; // nếu không chọn chi nhánh, dùng 1

    private ProductDAO  productDAO;
    private BrandDAO    brandDAO;
    private SupplierDAO supplierDAO;
    private CategoryDAO categoryDAO;
    private BranchDAO   branchDAO; // dùng cho dropdown chi nhánh khi chỉnh tồn

    @Override
    public void init() {
        productDAO  = new ProductDAO();
        brandDAO    = new BrandDAO();
        supplierDAO = new SupplierDAO();
        categoryDAO = new CategoryDAO();
        branchDAO   = new BranchDAO(); // nếu chưa cần dropdown chi nhánh có thể bỏ
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "add"    -> showAddForm(request, response);
                case "edit"   -> showEditForm(request, response);   // -> ProductForm.jsp
                case "delete" -> deleteProduct(request, response);
                case "detail" -> showDetail(request, response);     // -> test.jsp (xem chi tiết)
                case "list"   -> listProducts(request, response);
                default       -> listProducts(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi khi xử lý yêu cầu");
        }
    }

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
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi khi xử lý dữ liệu gửi lên");
        }
    }

    /* ========================= LISTING ========================= */
    private void listProducts(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Đọc ProductName từ form. Nếu chưa đổi form, fallback về "keyword"
        String productName = trimToNull(request.getParameter("productName"));
        if (productName == null) {
            productName = trimToNull(request.getParameter("keyword"));
        }

        // Lọc theo nhiều categoryName (theo tên) + tồn kho
        String[] rawCatNames = request.getParameterValues("categoryName");
        List<String> categoryNames = parseStrList(rawCatNames);
        if (categoryNames == null || categoryNames.isEmpty()) {
            String categoryNameCsv = trimToNull(request.getParameter("categoryName"));
            List<String> fromCsv = parseCsvStrList(categoryNameCsv);
            if (fromCsv != null && !fromCsv.isEmpty()) categoryNames = fromCsv;
        }

        // stock: all | in | out | belowMin | aboveMax
        String stock = request.getParameter("stock");
        if (stock == null || stock.isBlank()) stock = "all";

        int threshold = parseIntOrDefault(request.getParameter("stockThreshold"), DEFAULT_STOCK_THRESHOLD);

        // Dùng DAO tối ưu: listProducts(..., StockFilter, threshold)
        List<Product> products = productDAO.listProducts(
                categoryNames,
                productName,
                ProductDAO.StockFilter.from(stock),
                threshold
        );

        // Đẩy dữ liệu ra view (giữ lại các giá trị người dùng nhập/chọn)
        request.setAttribute("products", products);
        request.setAttribute("productName", productName);                // nếu view dùng ${productName}
        request.setAttribute("keyword", productName);                    // tương thích view cũ dùng ${keyword}
        request.setAttribute("selectedCategoryNames", categoryNames);
        request.setAttribute("stock", stock);
        request.setAttribute("stockThreshold", threshold);

        // Sidebar danh mục
        request.setAttribute("categories", categoryDAO.getAll());

        request.getRequestDispatcher("/WEB-INF/jsp/sale/sale.jsp").forward(request, response);
    }

    /* ========================= FORMS/DETAIL ========================= */

    /** Thêm mới → ProductForm.jsp (dropdown theo tên) */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        request.setAttribute("action", "insert");
        pushLookups(request); // nạp brands/categories/suppliers/(branches)
        request.getRequestDispatcher("/WEB-INF/jsp/sale/sale.jsp").forward(request, response);
    }

    /** Sửa → ProductForm.jsp (từ test.jsp bấm Chỉnh sửa sẽ tới form này) */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }

        // Lấy kèm tổng tồn để hiện trong form nếu cần
        Product product;
        try {
            product = productDAO.getProductByIdWithQty(id);
        } catch (NoSuchMethodError | RuntimeException ex) {
            // fallback nếu DAO của bạn chưa có getProductByIdWithQty
            product = productDAO.getProductById(id);
        }

        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy sản phẩm");
            return;
        }

        request.setAttribute("product", product);
        request.setAttribute("action", "update");
        pushLookups(request); // nạp các dropdown
        request.getRequestDispatcher("/WEB-INF/jsp/sale/sale.jsp").forward(request, response);
    }

    /** Xem chi tiết → test.jsp */
    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }

        Product product;
        try {
            product = productDAO.getProductByIdWithQty(id);
        } catch (NoSuchMethodError | RuntimeException ex) {
            product = productDAO.getProductById(id);
        }

        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy sản phẩm");
            return;
        }

        request.setAttribute("product", product);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/test.jsp").forward(request, response);
    }

    /* ========================= CRUD ========================= */
    private void insertProduct(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Product p = extractProductFromRequest(request);
        productDAO.insertProduct(p);

        // (tùy chọn) nếu muốn set tồn luôn sau khi insert, bạn có thể:
        // int newId = productDAO.insertProductReturningId(p);
        // int qty   = parseIntOrDefault(request.getParameter("quantity"), 0);
        // int branchId = parseIntOrDefault(request.getParameter("branchId"), DEFAULT_BRANCH_ID_FOR_QTY);
        // productDAO.setQuantityForProductAtBranch(newId, branchId, qty);

        response.sendRedirect("sale?action=list");
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }

        Product p = extractProductFromRequest(request);
        p.setProductId(id);
        productDAO.updateProduct(p);

        // (tùy chọn) cập nhật tồn kho nếu form có gửi
        String rawQty = trimToNull(request.getParameter("quantity"));
        if (rawQty != null && !rawQty.isBlank()) {
            try {
                int newQty = Integer.parseInt(rawQty.trim());
                int branchId = parseIntOrDefault(request.getParameter("branchId"), DEFAULT_BRANCH_ID_FOR_QTY);
                productDAO.setQuantityForProductAtBranch(id, branchId, newQty);
            } catch (NumberFormatException ignore) { /* bỏ qua nếu giá trị không hợp lệ */ }
        }

        response.sendRedirect("sale?action=list");
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }
        productDAO.deleteProduct(id);
        response.sendRedirect("sale?action=list");
    }

    /* ========================= HELPERS ========================= */

    /** Nạp dữ liệu cho dropdown trong ProductForm.jsp */
    private void pushLookups(HttpServletRequest req) {
        req.setAttribute("brands",     brandDAO.getAll());     // [{brandId, brandName}, ...]
        req.setAttribute("categories", categoryDAO.getAll());  // [{categoryId, categoryName}, ...]
        req.setAttribute("suppliers",  supplierDAO.getAllSuppliers());  // [{supplierId, supplierName}, ...]
        req.setAttribute("branches",   branchDAO.getAllBranches());    // dùng cho chọn chi nhánh khi chỉnh tồn
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

    /** Map form -> Product: dùng TÊN (brandName/categoryName/supplierName) */
    private Product extractProductFromRequest(HttpServletRequest request) {
        Product p = new Product();
        p.setProductName(trimToNull(request.getParameter("name")));

        // Dùng TÊN thay vì ID (DAO sẽ resolve -> ID)
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
