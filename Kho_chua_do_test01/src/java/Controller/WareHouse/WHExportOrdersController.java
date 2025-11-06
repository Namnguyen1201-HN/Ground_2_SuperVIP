package Controller.WareHouse;

import DAL.ProductDetailDAO;
import DAL.StockMovementsRequestDAO;
import Model.ProductDetail;
import Model.StockMovementsRequest;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "WHExportOrdersController", urlPatterns = {"/warehouse-export-orders"})
public class WHExportOrdersController extends HttpServlet {

    private final StockMovementsRequestDAO requestDAO = new StockMovementsRequestDAO();
    private final ProductDetailDAO productDetailDAO = new ProductDetailDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("currentUser");

            if (currentUser == null) {
                response.sendRedirect("login");
                return;
            }

            // Get warehouse ID from user (if available)
            // For non-warehouse managers, we'll show all export orders or handle differently
            Integer warehouseId = currentUser.getWarehouseId();
            
            // If user doesn't have warehouseId, try to get from parameter or show all
            if (warehouseId == null) {
                String warehouseIdParam = request.getParameter("warehouseId");
                if (warehouseIdParam != null && !warehouseIdParam.isEmpty()) {
                    try {
                        warehouseId = Integer.parseInt(warehouseIdParam);
                    } catch (NumberFormatException e) {
                        // Invalid warehouse ID parameter
                    }
                }
                
                // If still no warehouseId, show message but allow viewing
                if (warehouseId == null) {
                    // For non-warehouse managers, we can show all export orders or filter by their branch
                    // For now, we'll show a message but still allow access
                    request.setAttribute("infoMessage", "Bạn chưa được gán kho. Vui lòng chọn kho để xem đơn xuất.");
                }
            }

            // Get filter parameters
            String fromDate = request.getParameter("fromDate");
            String toDate = request.getParameter("toDate");
            String productId = request.getParameter("productId");
            String status = request.getParameter("status");
            if (status == null || status.isEmpty()) {
                status = "Tất cả";
            }

            // Get pagination parameters
            int page = 1;
            int pageSize = 10;
            try {
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    page = Integer.parseInt(pageParam);
                }
                String pageSizeParam = request.getParameter("pageSize");
                if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
                    pageSize = Integer.parseInt(pageSizeParam);
                }
            } catch (NumberFormatException e) {
                // Use defaults
            }

            // Get export orders with filters
            List<StockMovementsRequest> exportOrders;
            int totalCount;
            
            if (warehouseId != null) {
                // If warehouseId is available, filter by warehouse
                System.out.println("=== [WHExportOrdersController] Getting export orders ===");
                System.out.println("WarehouseID: " + warehouseId);
                System.out.println("FromDate: " + fromDate);
                System.out.println("ToDate: " + toDate);
                System.out.println("ProductID: " + productId);
                System.out.println("Status: " + status);
                System.out.println("Page: " + page + ", PageSize: " + pageSize);
                
                exportOrders = requestDAO.getExportRequestsWithFilter(
                        warehouseId, fromDate, toDate, productId, status, page, pageSize);
                totalCount = requestDAO.getExportRequestsCount(warehouseId, fromDate, toDate, productId, status);
                
                System.out.println("Found " + exportOrders.size() + " export orders");
                System.out.println("Total count: " + totalCount);
            } else {
                // If no warehouseId, show empty list or all orders (depending on business logic)
                // For now, show empty list with message
                System.out.println("=== [WHExportOrdersController] No warehouseId, showing empty list ===");
                exportOrders = new java.util.ArrayList<>();
                totalCount = 0;
            }
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // Format dates for display
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (StockMovementsRequest order : exportOrders) {
                if (order.getCreatedAt() != null) {
                    order.setFormattedDate(dateFormat.format(order.getCreatedAt()));
                }
            }

            // Get all products for filter dropdown
            List<ProductDetail> allProducts = productDetailDAO.getAllProductDetails();

            // Set attributes
            request.setAttribute("exportOrders", exportOrders);
            request.setAttribute("allProducts", allProducts);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("fromDate", fromDate);
            request.setAttribute("toDate", toDate);
            request.setAttribute("productId", productId);
            request.setAttribute("status", status);
            request.setAttribute("startIndex", (page - 1) * pageSize + 1);
            request.setAttribute("endIndex", Math.min(page * pageSize, totalCount));

            request.getRequestDispatcher("/WEB-INF/jsp/warehouse/wh-export-orders.jsp")
                    .forward(request, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải danh sách đơn xuất hàng: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/warehouse/wh-export-orders.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle filter form submission
        doGet(request, response);
    }
}

