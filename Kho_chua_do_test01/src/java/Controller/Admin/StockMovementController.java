package Controller.Admin;

import DAL.BranchDAO;
import DAL.ProductDetailDAO;
import DAL.StockMovementDAO;
import DAL.WarehouseDAO;
import Model.ExportCartItem;
import Model.ProductDetail;
import Model.StockMovementDetail;
import Model.StockMovementsRequest;
import Model.User;
import Model.Warehouse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unified controller for stock movements
 * Handles both:
 * - Admin: View all import/export requests
 * - Branch Manager: Create export requests from warehouse to branch
 */
@WebServlet("/stock-movements")
public class StockMovementController extends HttpServlet {
    
    private StockMovementDAO stockMovementDAO;
    private WarehouseDAO warehouseDAO;
    private ProductDetailDAO productDetailDAO;
    private BranchDAO branchDAO;
    
    @Override
    public void init() throws ServletException {
        stockMovementDAO = new StockMovementDAO();
        warehouseDAO = new WarehouseDAO();
        productDetailDAO = new ProductDetailDAO();
        branchDAO = new BranchDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        int roleId = currentUser.getRoleId();
        String action = request.getParameter("action");
        String warehouseIdParam = request.getParameter("warehouseId");
        
        System.out.println("=== StockMovementController GET ===");
        System.out.println("User: " + currentUser.getFullName() + ", RoleID: " + roleId);
        System.out.println("Action: " + action);
        System.out.println("WarehouseId: " + warehouseIdParam);
        
        // Admin (roleId = 0): View all movements or details
        if (roleId == 0) {
            if ("view-details".equals(action)) {
                handleViewDetails(request, response);
            } else {
                handleAdminListView(request, response, currentUser);
            }
        }
        // Branch Manager (roleId = 1): 
        // View list of export requests for their branch only
        // Export request creation is now handled by ImportRequestController
        else if (roleId == 1) {
            if ("view-details".equals(action)) {
                handleViewDetails(request, response);
            } else {
                // Always show list view for branch manager
                handleBranchManagerListView(request, response, currentUser);
            }
        }
        // Unauthorized
        else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        System.out.println("=== StockMovementController POST ===");
        System.out.println("Action: " + action);
        
        // Only Branch Manager can create export requests
        if (currentUser.getRoleId() == 1) {
            if ("add-product".equals(action)) {
                handleAddProduct(request, response);
            } else if ("remove-product".equals(action)) {
                handleRemoveProduct(request, response);
            } else if ("update-quantity".equals(action)) {
                handleUpdateQuantity(request, response);
            } else if ("reset-cart".equals(action)) {
                handleResetCart(request, response);
            } else if ("submit-export-request".equals(action)) {
                handleSubmitExportRequest(request, response, currentUser);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
    
    /**
     * Handle admin list view - show all import/export requests
     */
    private void handleAdminListView(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        
        String movementType = request.getParameter("type"); // "Import", "Export", or null for all
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (Exception e) {
            // Use default
        }
        
        int pageSize = 20;
        StockMovementDAO.PagedMoves pagedMoves = stockMovementDAO.getAllMovements(movementType, page, pageSize);
        
        int totalPages = (int) Math.ceil((double) pagedMoves.total / pageSize);
        
        System.out.println("Admin viewing movements: Type=" + movementType + ", Page=" + page);
        System.out.println("Total movements: " + pagedMoves.total + ", Total pages: " + totalPages);
        
        request.setAttribute("movements", pagedMoves.items);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", pagedMoves.total);
        request.setAttribute("movementType", movementType);
        
        request.getRequestDispatcher("/WEB-INF/jsp/admin/stock_movements_list.jsp").forward(request, response);
    }
    
    /**
     * Handle branch manager list view - show export requests for their branch only
     */
    private void handleBranchManagerListView(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        
        Integer branchId = currentUser.getBranchId();
        if (branchId == null) {
            request.setAttribute("errorMessage", "Bạn chưa được gán chi nhánh!");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/error.jsp").forward(request, response);
            return;
        }
        
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (Exception e) {
            // Use default
        }
        
        int pageSize = 20;
        StockMovementDAO.PagedMoves pagedMoves = stockMovementDAO.getMovementsByBranch(branchId, page, pageSize);
        
        int totalPages = (int) Math.ceil((double) pagedMoves.total / pageSize);
        
        System.out.println("Branch Manager viewing movements: BranchID=" + branchId + ", Page=" + page);
        System.out.println("Total movements: " + pagedMoves.total + ", Total pages: " + totalPages);
        
        request.setAttribute("movements", pagedMoves.items);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", pagedMoves.total);
        request.setAttribute("movementType", "Export"); // Branch managers only see exports
        request.setAttribute("isBranchManager", true);
        
        request.getRequestDispatcher("/WEB-INF/jsp/admin/stock_movements_list.jsp").forward(request, response);
    }
    
    /**
     * Handle viewing movement details
     */
    private void handleViewDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int movementId = Integer.parseInt(request.getParameter("id"));
            
            // Get movement header
            StockMovementsRequest movement = stockMovementDAO.getMovementById(movementId);
            if (movement == null) {
                request.setAttribute("errorMessage", "Không tìm thấy phiếu!");
                request.getRequestDispatcher("/WEB-INF/jsp/admin/error.jsp").forward(request, response);
                return;
            }
            
            // Get movement details
            List<StockMovementDetail> details = stockMovementDAO.getMovementDetails(movementId);
            
            request.setAttribute("movement", movement);
            request.setAttribute("details", details);
            
            request.getRequestDispatcher("/WEB-INF/jsp/admin/stock_movement_details.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/stock-movements");
        }
    }
    
    /**
     * Handle branch manager export request view
     */
    private void handleBranchManagerExportRequest(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        
        // Get branch manager's branch ID
        Integer branchId = currentUser.getBranchId();
        if (branchId == null) {
            request.setAttribute("errorMessage", "Bạn chưa được gán chi nhánh!");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/error.jsp").forward(request, response);
            return;
        }
        
        // Get parameters
        String warehouseIdStr = request.getParameter("warehouseId");
        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";
        
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (Exception e) {}
        
        // Load warehouses for dropdown
        List<Warehouse> listWarehouses = warehouseDAO.getAllWarehouses();
        request.setAttribute("listWarehouses", listWarehouses);
        System.out.println("Loaded " + listWarehouses.size() + " warehouses");
        
        // If warehouse selected, load products from that warehouse
        if (warehouseIdStr != null && !warehouseIdStr.isEmpty()) {
            try {
                int warehouseId = Integer.parseInt(warehouseIdStr);
                request.setAttribute("selectedWarehouseID", warehouseId);
                
                // Get products available in this warehouse with inventory
                List<ProductDetail> products = productDetailDAO.getProductsByWarehouse(warehouseId, keyword);
                
                int pageSize = 12;
                int totalProducts = products.size();
                int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
                int fromIndex = (page - 1) * pageSize;
                int toIndex = Math.min(fromIndex + pageSize, totalProducts);
                
                List<ProductDetail> pagedProducts = products.subList(fromIndex, toIndex);
                
                request.setAttribute("listProducts", pagedProducts);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("keyword", keyword);
                
                System.out.println("Loaded " + totalProducts + " products from warehouse " + warehouseId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        request.getRequestDispatcher("/WEB-INF/jsp/admin/export_request.jsp").forward(request, response);
    }
    
    /**
     * Add product to export cart
     */
    private void handleAddProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        
        try {
            int productDetailID = Integer.parseInt(request.getParameter("productDetailId"));
            int warehouseId = Integer.parseInt(request.getParameter("warehouseId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            
            // Get product details
            ProductDetail pd = productDetailDAO.getProductDetailById(productDetailID);
            if (pd == null) {
                session.setAttribute("errorMessage", "Sản phẩm không tồn tại!");
                response.sendRedirect(request.getContextPath() + "/stock-movements?warehouseId=" + warehouseId);
                return;
            }
            
            // Get or create cart
            @SuppressWarnings("unchecked")
            List<ExportCartItem> cart = (List<ExportCartItem>) session.getAttribute("exportCart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("exportCart", cart);
            }
            
            // Check if cart already has items from different warehouse
            Integer cartWarehouseId = (Integer) session.getAttribute("cartWarehouseId");
            if (cartWarehouseId != null && cartWarehouseId != warehouseId) {
                session.setAttribute("errorMessage", "Giỏ hàng đã có sản phẩm từ kho khác!");
                response.sendRedirect(request.getContextPath() + "/stock-movements?warehouseId=" + warehouseId);
                return;
            }
            
            session.setAttribute("cartWarehouseId", warehouseId);
            
            // Check if product already in cart
            boolean found = false;
            for (ExportCartItem item : cart) {
                if (item.getProductDetailID() == productDetailID) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                ExportCartItem newItem = new ExportCartItem();
                newItem.setProductDetailID(productDetailID);
                newItem.setProductName(pd.getProductName());
                newItem.setProductCode(pd.getProductCode());
                newItem.setDescription(pd.getDescription());
                newItem.setCostPrice(pd.getCostPrice());
                newItem.setRetailPrice(pd.getRetailPrice());
                newItem.setQuantity(quantity);
                newItem.setAvailableStock(pd.getQuantityInStock());
                newItem.setImageURL(pd.getImageURL());
                cart.add(newItem);
            }
            
            session.setAttribute("successMessage", "Đã thêm sản phẩm vào phiếu xuất!");
            response.sendRedirect(request.getContextPath() + "/stock-movements?warehouseId=" + warehouseId);
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/stock-movements");
        }
    }
    
    /**
     * Remove product from export cart
     */
    private void handleRemoveProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        
        try {
            int productDetailID = Integer.parseInt(request.getParameter("productDetailId"));
            int warehouseId = Integer.parseInt(request.getParameter("warehouseId"));
            
            @SuppressWarnings("unchecked")
            List<ExportCartItem> cart = (List<ExportCartItem>) session.getAttribute("exportCart");
            
            if (cart != null) {
                cart.removeIf(item -> item.getProductDetailID() == productDetailID);
                
                if (cart.isEmpty()) {
                    session.removeAttribute("exportCart");
                    session.removeAttribute("cartWarehouseId");
                }
            }
            
            response.sendRedirect(request.getContextPath() + "/stock-movements?warehouseId=" + warehouseId);
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/stock-movements");
        }
    }
    
    /**
     * Update product quantity in cart
     */
    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        
        try {
            int productDetailID = Integer.parseInt(request.getParameter("productDetailId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            int warehouseId = Integer.parseInt(request.getParameter("warehouseId"));
            
            if (quantity <= 0) {
                session.setAttribute("errorMessage", "Số lượng phải lớn hơn 0!");
                response.sendRedirect(request.getContextPath() + "/stock-movements?warehouseId=" + warehouseId);
                return;
            }
            
            @SuppressWarnings("unchecked")
            List<ExportCartItem> cart = (List<ExportCartItem>) session.getAttribute("exportCart");
            
            if (cart != null) {
                for (ExportCartItem item : cart) {
                    if (item.getProductDetailID() == productDetailID) {
                        item.setQuantity(quantity);
                        break;
                    }
                }
            }
            
            response.sendRedirect(request.getContextPath() + "/stock-movements?warehouseId=" + warehouseId);
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/stock-movements");
        }
    }
    
    /**
     * Reset cart
     */
    private void handleResetCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        
        int warehouseId = Integer.parseInt(request.getParameter("warehouseId"));
        
        session.removeAttribute("exportCart");
        session.removeAttribute("cartWarehouseId");
        
        session.setAttribute("successMessage", "Đã xóa toàn bộ giỏ hàng!");
        response.sendRedirect(request.getContextPath() + "/stock-movements?warehouseId=" + warehouseId);
    }
    
    /**
     * Submit export request
     */
    private void handleSubmitExportRequest(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws IOException {
        HttpSession session = request.getSession();
        
        try {
            Integer warehouseId = (Integer) session.getAttribute("cartWarehouseId");
            Integer branchId = currentUser.getBranchId();
            String note = request.getParameter("note");
            
            @SuppressWarnings("unchecked")
            List<ExportCartItem> cart = (List<ExportCartItem>) session.getAttribute("exportCart");
            
            if (warehouseId == null || branchId == null || cart == null || cart.isEmpty()) {
                session.setAttribute("errorMessage", "Thông tin không đầy đủ!");
                response.sendRedirect(request.getContextPath() + "/stock-movements");
                return;
            }
            
            System.out.println("Submitting export request:");
            System.out.println("  FromWarehouseID: " + warehouseId);
            System.out.println("  ToBranchID: " + branchId);
            System.out.println("  CreatedBy: " + currentUser.getUserId());
            System.out.println("  Items: " + cart.size());
            
            boolean success = stockMovementDAO.createExportRequest(
                warehouseId,
                branchId,
                currentUser.getUserId(),
                cart,
                note
            );
            
            if (success) {
                System.out.println("✅ Export request created successfully");
                session.removeAttribute("exportCart");
                session.removeAttribute("cartWarehouseId");
                session.setAttribute("successMessage", "Tạo yêu cầu xuất kho thành công!");
                response.sendRedirect(request.getContextPath() + "/stock-movements");
            } else {
                System.out.println("❌ Failed to create export request");
                session.setAttribute("errorMessage", "Có lỗi xảy ra khi tạo yêu cầu xuất kho!");
                response.sendRedirect(request.getContextPath() + "/stock-movements?warehouseId=" + warehouseId);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Có lỗi xảy ra!");
            response.sendRedirect(request.getContextPath() + "/stock-movements");
        }
    }
}
