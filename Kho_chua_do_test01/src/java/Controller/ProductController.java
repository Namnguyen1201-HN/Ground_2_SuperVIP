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
        try {
            // Lấy keyword từ request
            String keyword = request.getParameter("keyword");

            List<Product> products;

            if (keyword != null && !keyword.trim().isEmpty()) {
                // ✅ Nếu có keyword → tìm theo tên sản phẩm
                products = productDAO.searchProductsByName(keyword.trim());
            } else {
                // ✅ Nếu không có keyword → lấy tất cả
                products = productDAO.getAllProducts();
            }

            // Gắn dữ liệu vào request attribute
            request.setAttribute("products", products);
            request.setAttribute("keyword", keyword);

            // Chuyển tiếp sang JSP
            request.getRequestDispatcher("/WEB-INF/jsp/product.jsp").forward(request, response);

        } catch (Exception  e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi truy xuất dữ liệu sản phẩm.");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
