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
    private ProductDAO productDAO;

    @Override
    public void init() {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // đảm bảo UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "add" -> showAddForm(request, response);
                case "edit" -> showEditForm(request, response);
                case "delete" -> deleteProduct(request, response);
                case "list" -> listProducts(request, response);
                default -> listProducts(request, response);
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
                default -> doGet(request, response);
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

        // HỖ TRỢ NHIỀU CATEGORY:
        // - Nếu UI gửi nhiều checkbox/select cùng name=categoryId -> dùng getParameterValues.
        // - Nếu UI gửi 1 chuỗi CSV (?categoryId=1,2,3) -> parse thêm (tùy bạn dùng hay không).
        String[] rawCatIds = request.getParameterValues("categoryId");
        List<Integer> categoryIds = parseIntList(rawCatIds);

        // (tùy chọn) hỗ trợ thêm dạng CSV trong 1 param duy nhất
        if ((categoryIds == null || categoryIds.isEmpty())) {
            String categoryIdCsv = trimToNull(request.getParameter("categoryId"));
            List<Integer> fromCsv = parseCsvIntList(categoryIdCsv);
            if (fromCsv != null && !fromCsv.isEmpty()) {
                categoryIds = fromCsv;
            }
        }

        // stock: all | in | out | belowMin | aboveMax
        String stock = request.getParameter("stock");
        if (stock == null || stock.isBlank()) stock = "all";

        int threshold = parseIntOrDefault(request.getParameter("stockThreshold"), DEFAULT_STOCK_THRESHOLD);

        // DAO MỚI: nhận List<Integer> categoryIds
        List<Product> products = productDAO.findProductsWithThreshold(categoryIds, keyword, stock, threshold);

        // Đẩy dữ liệu ra view
        request.setAttribute("products", products);
        request.setAttribute("keyword", keyword);
        request.setAttribute("selectedCategoryIds", categoryIds); // để JSP giữ trạng thái chọn
        request.setAttribute("stock", stock);
        request.setAttribute("stockThreshold", threshold);

        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAll();
        request.setAttribute("categories", categories);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/product.jsp").forward(request, response);
    }

    /* ========================= FORMS ========================= */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        request.setAttribute("action", "insert");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/test.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }
        Product product = productDAO.getProductById(id);
        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy sản phẩm");
            return;
        }
        request.setAttribute("product", product);
        request.setAttribute("action", "update");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/ProductForm.jsp").forward(request, response);
    }

    /* ========================= CRUD ========================= */
    private void insertProduct(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Product p = extractProductFromRequest(request);
        boolean ok = productDAO.insertProduct(p);
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
        boolean ok = productDAO.updateProduct(p);
        response.sendRedirect("product?action=list");
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }
        boolean ok = productDAO.deleteProduct(id);
        response.sendRedirect("product?action=list");
    }

    /* ========================= HELPERS ========================= */
    private Integer parseIntOrNull(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parseIntOrDefault(String s, int def) {
        try {
            return (s == null || s.isBlank()) ? def : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private BigDecimal parseBigDecimalOrNull(String s) {
        try {
            return (s == null || s.isBlank()) ? null : new BigDecimal(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // NEW: parse mảng String -> List<Integer>
    private List<Integer> parseIntList(String[] arr) {
        if (arr == null || arr.length == 0) return null;
        List<Integer> ids = new ArrayList<>();
        for (String s : arr) {
            try {
                if (s != null && !s.isBlank()) {
                    ids.add(Integer.valueOf(s.trim()));
                }
            } catch (NumberFormatException ignore) { }
        }
        return ids.isEmpty() ? null : ids;
    }

    // (tùy chọn) parse "1,2,3" -> List<Integer>
    private List<Integer> parseCsvIntList(String csv) {
        if (csv == null || csv.isBlank()) return null;
        String[] parts = csv.split(",");
        return parseIntList(parts);
    }

    /**
     * Map form -> Product tương thích DAO:
     * Cho phép DECIMAL/INT/BOOLEAN là NULL (DAO sẽ set NULL an toàn về DB).
     */
    private Product extractProductFromRequest(HttpServletRequest request) {
        Product p = new Product();
        p.setProductName(trimToNull(request.getParameter("name")));

        // INT (nullable)
        p.setBrandId(parseIntOrNull(request.getParameter("brandId")));
        p.setCategoryId(parseIntOrNull(request.getParameter("categoryId"))); // đây là category của SP (1 giá trị), KHÁC với filter nhiều category
        p.setSupplierId(parseIntOrNull(request.getParameter("supplierId")));

        // DECIMAL (nullable)
        p.setCostPrice(parseBigDecimalOrNull(request.getParameter("costPrice")));
        p.setRetailPrice(parseBigDecimalOrNull(request.getParameter("retailPrice")));
        p.setVat(parseBigDecimalOrNull(request.getParameter("vat")));

        p.setImageUrl(trimToNull(request.getParameter("imageUrl")));

        // BOOLEAN (nullable)
        String rawIsActive = request.getParameter("isActive");
        Boolean isActive = null;
        if (rawIsActive != null) {
            isActive = ("on".equalsIgnoreCase(rawIsActive) || "true".equalsIgnoreCase(rawIsActive));
        }
        p.setIsActive(isActive);

        return p;
    }
}
