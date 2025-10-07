package Controller;

import DAL.ProductDAO;
import DAL.SupplierDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name="CreateInboundController", urlPatterns={"/CreateInbound"})
public class CreateInboundController extends HttpServlet {
    private SupplierDAO supplierDAO;
    private ProductDAO productDAO;

    @Override
    public void init() {
        supplierDAO = new SupplierDAO();
        productDAO = new ProductDAO();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("Login");
            return;
        }
        request.setAttribute("suppliers", supplierDAO.getAllSuppliers());
        request.setAttribute("products", productDAO.getAllProducts());
        request.getRequestDispatcher("/WEB-INF/jsp/create_inbound.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("Login");
            return;
        }

        // Collect basic form values
        String supplierIdStr = request.getParameter("supplierId");
        String customerName = request.getParameter("customerName");
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        String[] unitPrices = request.getParameterValues("unitPrice");

        if (supplierIdStr == null || productIds == null) {
            response.sendRedirect("CreateInbound?error=missing");
            return;
        }

        int createdBy = ((Model.User) session.getAttribute("currentUser")).getUserId();

        DAL.ReceiptDAO receiptDAO = new DAL.ReceiptDAO();
        DAL.OrderDetailDAO rdDAO = new DAL.OrderDetailDAO();

        double subtotal = 0;
        for (int i = 0; i < productIds.length; i++) {
            int q = Integer.parseInt(quantities[i]);
            double up = Double.parseDouble(unitPrices[i]);
            subtotal += q * up;
        }

        Model.Receipt r = new Model.Receipt();
        r.setCustomerName(customerName);
        r.setCreatedBy(createdBy);
        r.setStatus("Active");
        r.setSubTotal(subtotal);
        r.setDiscount(0);
        r.setTax(0);
        r.setGrandTotal(subtotal);

        int receiptId = receiptDAO.insert(r);
        if (receiptId > 0) {
            for (int i = 0; i < productIds.length; i++) {
                rdDAO.insertReceiptDetail(
                    receiptId,
                    Integer.parseInt(productIds[i]),
                    Integer.parseInt(quantities[i]),
                    Double.parseDouble(unitPrices[i])
                );
            }
            request.setAttribute("success", true);
        } else {
            request.setAttribute("success", false);
        }

        // reload dropdown data and forward back to form
        request.setAttribute("suppliers", supplierDAO.getAllSuppliers());
        request.setAttribute("products", productDAO.getAllProducts());
        request.getRequestDispatcher("/WEB-INF/jsp/create_inbound.jsp").forward(request, response);
    }
}


