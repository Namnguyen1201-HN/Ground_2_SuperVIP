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
        String keyword = request.getParameter("keyword");
        List<Product> products;

        if (keyword != null && !keyword.trim().isEmpty()) {

            products = productDAO.searchProductsByName(keyword.trim());
        } else {
            products = productDAO.getAllProducts();
        }

        request.setAttribute("products", products);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/WEB-INF/jsp/Product.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        request.setAttribute("action", "insert");  // để form biết đang insert
        request.getRequestDispatcher("/WEB-INF/jsp/ProductForm.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        Product product = productDAO.getProductById(id);  // lấy sản phẩm từ DB
        request.setAttribute("product", product);         // gắn vào request
        request.setAttribute("action", "update");         // để form biết đang update
        request.getRequestDispatcher("/WEB-INF/jsp/ProductForm.jsp").forward(request, response);
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
