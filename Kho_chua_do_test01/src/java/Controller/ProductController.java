package Controller;

import DAL.ProductDAO;
import Model.Product;
import java.io.IOException;
import java.sql.SQLException;
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
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // set mặc định hiển thị danh sách
        }

        try {
            switch (action) {
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteProduct(request, response);
                    break;
                case "list":
                    listProducts(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi web rồi =)))))");

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "insert":
                    insertProduct(request, response);
                    break;
                case "update":
                    updateProduct(request, response);
                    break;
                default:
                    doGet(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "loi web roi =))))))))");
        }
    }

    private void listProducts(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String keyword = trimToNull(request.getParameter("keyword"));
        String categoryIdParam = request.getParameter("categoryId");
        Integer categoryId = parseIntOrNull(categoryIdParam);

        // stock: all | in | out | belowMin | aboveMax
        String stock = request.getParameter("stock");
        if (stock == null || stock.isBlank()) {
            stock = "all";
        }

        // ngưỡng cố định (nếu không truyền thì lấy DEFAULT)
        int threshold = parseIntOrDefault(request.getParameter("stockThreshold"), DEFAULT_STOCK_THRESHOLD);

        // gọi DAO hợp nhất có so sánh ngưỡng
        List<Product> products = productDAO.findProductsWithThreshold(categoryId, keyword, stock, threshold);

        // gán attribute cho JSP
        request.setAttribute("products", products);
        request.setAttribute("keyword", keyword);
        request.setAttribute("selectedCategoryId", categoryIdParam);
        request.setAttribute("stock", stock);
        request.setAttribute("stockThreshold", threshold);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/product.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        request.setAttribute("action", "insert");  // để form biết đang insert
        request.getRequestDispatcher("/WEB-INF/jsp/admin/ProductForm.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        Product product = productDAO.getProductById(id);  // lấy sản phẩm từ DB
        request.setAttribute("product", product);         // gắn vào request
        request.setAttribute("action", "update");         // để form biết đang update
        request.getRequestDispatcher("/WEB-INF/jsp/admin/ProductForm.jsp").forward(request, response);
    }

    private void insertProduct(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Product p = extractProductFromRequest(request);
        productDAO.insertProduct(p);
        response.sendRedirect("product?action=list");
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Product p = extractProductFromRequest(request);
        p.setProductId(Integer.parseInt(request.getParameter("id")));
        productDAO.updateProduct(p);
        response.sendRedirect("product?action=list");
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        int id = Integer.parseInt(request.getParameter("id"));
        productDAO.deleteProduct(id);
        response.sendRedirect("product?action=list");
    }

    private Integer parseIntOrNull(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parseIntOrDefault(String s, int def) {
        try {
            return (s == null || s.isBlank()) ? def : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private Product extractProductFromRequest(HttpServletRequest request) {
        Product p = new Product();
        p.setProductName(request.getParameter("name"));

        String categoryId = request.getParameter("categoryId");
        p.setCategoryId(categoryId != null && !categoryId.isEmpty() ? Integer.parseInt(categoryId) : null);

        String supplierId = request.getParameter("supplierId");
        p.setSupplierId(supplierId != null && !supplierId.isEmpty() ? Integer.parseInt(supplierId) : null);

        p.setPrice(Double.parseDouble(request.getParameter("price")));
        p.setQuantity(Integer.parseInt(request.getParameter("quantity")));

        String expiryDate = request.getParameter("expiryDate");
        if (expiryDate != null && !expiryDate.isEmpty()) {
            p.setExpiryDate(java.sql.Date.valueOf(expiryDate));
        }

        return p;
    }

}
