package Controller;

import DAL.ReceiptDAO;
import DAL.ReceiptDetailDAO;
import DAL.ProductDAO;
import DAL.SupplierDAO;
import Model.Product;
import Model.Supplier;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name="InventoryTrackingController", urlPatterns={"/InventoryTracking"})
public class InventoryTrackingController extends HttpServlet {
    private ReceiptDAO receiptDAO;
    private ReceiptDetailDAO receiptDetailDAO;
    private ProductDAO productDAO;
    private SupplierDAO supplierDAO;

    @Override
    public void init() {
        receiptDAO = new ReceiptDAO();
        receiptDetailDAO = new ReceiptDetailDAO();
        productDAO = new ProductDAO();
        supplierDAO = new SupplierDAO();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("Login");
            return;
        }

        String ajax = request.getParameter("ajax");
        if ("details".equalsIgnoreCase(ajax)) {
            String idStr = request.getParameter("receiptId");
            if (idStr != null) {
                try {
                    int rid = Integer.parseInt(idStr);
                    java.util.List<Model.ReceiptDetail> details = receiptDetailDAO.getByReceipt(rid);
                    java.util.List<Model.Product> products = productDAO.getAllProducts();
                    java.util.List<Model.Supplier> suppliers = supplierDAO.getAllSuppliers();
                    java.util.Map<Integer, String> productIdToName = new java.util.HashMap<>();
                    java.util.Map<Integer, Integer> productIdToSupplier = new java.util.HashMap<>();
                    for (Model.Product p : products) {
                        productIdToName.put(p.getProductId(), p.getProductName());
                        productIdToSupplier.put(p.getProductId(), p.getSupplierId() == null ? 0 : p.getSupplierId());
                    }
                    java.util.Map<Integer, String> supplierIdToName = new java.util.HashMap<>();
                    for (Model.Supplier s : suppliers) supplierIdToName.put(s.getSupplierId(), s.getSupplierName());

                    StringBuilder json = new StringBuilder();
                    json.append("[");
                    for (int i = 0; i < details.size(); i++) {
                        Model.ReceiptDetail d = details.get(i);
                        int pid = d.getProductId();
                        Integer supId = productIdToSupplier.get(pid);
                        String pName = productIdToName.getOrDefault(pid, String.valueOf(pid));
                        String sName = supId != null ? supplierIdToName.getOrDefault(supId, "-") : "-";
                        if (i > 0) json.append(",");
                        json.append("{")
                            .append("\"receiptId\":").append(d.getReceiptId()).append(",")
                            .append("\"productName\":\"").append(pName.replace("\"","\\\"")).append("\",")
                            .append("\"supplierName\":\"").append(sName.replace("\"","\\\"")).append("\",")
                            .append("\"quantity\":").append(d.getQuantity()).append(",")
                            .append("\"unitPrice\":").append((long)d.getUnitPrice()).append(",")
                            .append("\"total\":").append((long)d.getTotal())
                            .append("}");
                    }
                    json.append("]");
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(json.toString());
                    return;
                } catch (NumberFormatException ignored) {}
            }
            response.setStatus(400);
            return;
        }
        // If request has 'id', forward to detail page
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                int rid = Integer.parseInt(idParam);
                Model.Receipt receipt = receiptDAO.getById(rid);
                request.setAttribute("receipt", receipt);
                request.setAttribute("details", receiptDetailDAO.getByReceipt(rid));
                request.getRequestDispatcher("/WEB-INF/jsp/admin/receipt_detail.jsp").forward(request, response);
                return;
            } catch (NumberFormatException ignored) {}
        }
        java.util.List<Model.Receipt> receipts = receiptDAO.getAll();
        request.setAttribute("receipts", receipts);
        if (receipts != null && !receipts.isEmpty()) {
            // không load tất cả chi tiết; để rỗng đến khi người dùng click
            request.setAttribute("details", java.util.Collections.emptyList());
            // Preload product and supplier lookups
            java.util.List<Product> products = productDAO.getAllProducts();
            java.util.List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            java.util.Map<Integer, String> productIdToName = new java.util.HashMap<>();
            for (Product p : products) productIdToName.put(p.getProductId(), p.getProductName());
            java.util.Map<Integer, Integer> productIdToSupplier = new java.util.HashMap<>();
            for (Product p : products) productIdToSupplier.put(p.getProductId(), p.getSupplierId() == null ? 0 : p.getSupplierId());
            java.util.Map<Integer, String> supplierIdToName = new java.util.HashMap<>();
            for (Supplier s : suppliers) supplierIdToName.put(s.getSupplierId(), s.getSupplierName());
            request.setAttribute("productIdToName", productIdToName);
            request.setAttribute("productIdToSupplier", productIdToSupplier);
            request.setAttribute("supplierIdToName", supplierIdToName);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin/inventory_tracking.jsp").forward(request, response);
    }
}


