package Controller;

import DAL.ProductDAO;
import DAL.CategoryDAO;
import Model.Category;
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

@WebServlet(name = "ProductController", urlPatterns = {"/product"})
public class ProductController extends HttpServlet {

    private static final int DEFAULT_STOCK_THRESHOLD = 30;
    private static final int DEFAULT_BRANCH_ID_FOR_QTY = 1; // nếu không chọn chi nhánh, dùng 1
    private ProductDAO productDAO;

    @Override
    public void init() {
        productDAO = new ProductDAO();
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
                case "detail" -> showDetail(request, response);     // -> test.jsp
                case "list"   -> listProducts(request, response);
                default       -> listProducts(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi khi xử lý yêu cầu");
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
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi khi xử lý dữ liệu gửi lên");
        }
    }

    /* ========================= LISTING ========================= */
    private void listProducts(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String keyword = trimToNull(request.getParameter("keyword"));

        // Lọc theo NHIỀU categoryName (tên) + tồn kho
        String[] rawCatNames = request.getParameterValues("categoryName");
        List<String> categoryNames = parseStrList(rawCatNames);
        if (categoryNames == null || categoryNames.isEmpty()) {
            String categoryNameCsv = trimToNull(request.getParameter("categoryName"));
            List<String> fromCsv = parseCsvStrList(categoryNameCsv);
            if (fromCsv != null && !fromCsv.isEmpty()) categoryNames = fromCsv;
        }

        String stock = request.getParameter("stock");
        if (stock == null || stock.isBlank()) stock = "all";

        int threshold = parseIntOrDefault(request.getParameter("stockThreshold"), DEFAULT_STOCK_THRESHOLD);

        // Gọi DAO lọc + SUM tồn kho (hàm này SELECT có TotalQty)
        // Lưu ý: đảm bảo DAO đang map TotalQty (dùng mapRowToProductWithQty).
        List<Product> products = productDAO.findProductsWithThresholdByCategoryNames(
                categoryNames, keyword, stock, threshold
        );

        // Đẩy dữ liệu ra view
        request.setAttribute("products", products);
        request.setAttribute("keyword", keyword);
        request.setAttribute("selectedCategoryNames", categoryNames);
        request.setAttribute("stock", stock);
        request.setAttribute("stockThreshold", threshold);

        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAll();
        request.setAttribute("categories", categories);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/product.jsp").forward(request, response);
    }

    /* ========================= FORMS/DETAIL ========================= */

    /** Thêm mới → ProductForm.jsp */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        request.setAttribute("action", "insert");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/ProductForm.jsp").forward(request, response);
    }

    /** Chỉnh sửa → ProductForm.jsp (đúng yêu cầu: từ test.jsp bấm Chỉnh sửa tới form) */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }
        // Lấy SP kèm TotalQty để đổ vào form (nếu form có hiển thị)
        Product product = productDAO.getProductByIdWithQty(id);
        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy sản phẩm");
            return;
        }
        request.setAttribute("product", product);
        request.setAttribute("action", "update");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/ProductForm.jsp").forward(request, response);
    }

    /** Xem chi tiết → test.jsp (từ danh sách bấm “Chi tiết”) */
    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }
        // Lấy SP kèm TotalQty để hiển thị đẹp ở test.jsp
        Product product = productDAO.getProductByIdWithQty(id);
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
        // giữ nguyên insert (không trả id). Nếu bạn muốn set tồn ngay khi thêm,
        // hãy đổi sang insertProductReturningId(...) ở DAO rồi gọi setQuantityForProductAtBranch(...).
        productDAO.insertProduct(p);
        response.sendRedirect("product?action=list");
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

        // ====== (TÙY CHỌN) Cập nhật tồn kho nếu form có gửi quantity ======
        String rawQty = trimToNull(request.getParameter("quantity"));
        if (rawQty != null && !rawQty.isBlank()) {
            try {
                int newQty = Integer.parseInt(rawQty.trim());
                int branchId = parseIntOrDefault(request.getParameter("branchId"), DEFAULT_BRANCH_ID_FOR_QTY);
                productDAO.setQuantityForProductAtBranch(id, branchId, newQty);
            } catch (NumberFormatException ignore) {
                // không làm gì nếu không hợp lệ
            }
        }
        // ================================================================

        response.sendRedirect("product?action=list");
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }
        productDAO.deleteProduct(id);
        response.sendRedirect("product?action=list");
    }

    /* ========================= HELPERS ========================= */
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

    /** Map form -> Product (DÙNG TÊN) */
    private Product extractProductFromRequest(HttpServletRequest request) {
        Product p = new Product();
        p.setProductName(trimToNull(request.getParameter("name")));

        // Dùng TÊN thay vì ID
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
