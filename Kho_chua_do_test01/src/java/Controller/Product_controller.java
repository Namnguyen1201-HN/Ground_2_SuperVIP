package Controller;

import DAL.ProductDAO;
import Model.Product;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Product_controller", urlPatterns = {"/product"})
public class Product_controller extends HttpServlet {
    private ProductDAO productDAO;

    @Override
    public void init() {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list"; // mặc định load danh sách

        switch (action) {
            case "add":
                request.getRequestDispatcher("/WEB-INF/jsp/product-form.jsp").forward(request, response);
                break;
            case "edit":
                int idEdit = Integer.parseInt(request.getParameter("id"));
                Product existingProduct = productDAO.getProductById(idEdit);
                request.setAttribute("product", existingProduct);
                request.getRequestDispatcher("/WEB-INF/jsp/product-form.jsp").forward(request, response);
                break;
            case "delete":
                int idDelete = Integer.parseInt(request.getParameter("id"));
                productDAO.deleteProduct(idDelete);
                response.sendRedirect("product"); // quay lại danh sách
                break;
            case "search":
                String keyword = request.getParameter("keyword");
                List<Product> searchList = productDAO.searchProductsByName(keyword);
                request.setAttribute("products", searchList);
                request.getRequestDispatcher("/WEB-INF/jsp/product.jsp").forward(request, response);
                break;
            default: // list
                List<Product> products = productDAO.getAllProducts();
                request.setAttribute("products", products);
                request.getRequestDispatcher("/WEB-INF/jsp/product.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // để nhận tiếng Việt
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String categoryId = request.getParameter("categoryId");
        String supplierId = request.getParameter("supplierId");
        String price = request.getParameter("price");
        String quantity = request.getParameter("quantity");
        String expiryDate = request.getParameter("expiryDate");

        Product p = new Product();
        if (id != null && !id.isEmpty()) {
            p.setProductId(Integer.parseInt(id));
        }
        p.setProductName(name);
        p.setCategoryId(categoryId == null || categoryId.isEmpty() ? null : Integer.parseInt(categoryId));
        p.setSupplierId(supplierId == null || supplierId.isEmpty() ? null : Integer.parseInt(supplierId));
        p.setPrice(Double.parseDouble(price));
        p.setQuantity(Integer.parseInt(quantity));
        if (expiryDate != null && !expiryDate.isEmpty()) {
            p.setExpiryDate(java.sql.Date.valueOf(expiryDate));
        }

        if (id == null || id.isEmpty()) {
            productDAO.insertProduct(p);
        } else {
            productDAO.updateProduct(p);
        }

        response.sendRedirect("product");
    }

    @Override
    public String getServletInfo() {
        return "Product Controller Servlet";
    }
}
