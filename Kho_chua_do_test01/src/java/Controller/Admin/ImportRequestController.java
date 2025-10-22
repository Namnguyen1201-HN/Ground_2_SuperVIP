package Controller.Admin;

import DAL.ProductDetailDAO;
import DAL.SupplierDAO;
import DAL.WarehouseDAO;
import DAL.StockMovementDAO;
import DAL.BranchDAO;
import Model.ProductDetail;
import Model.Supplier;
import Model.Warehouse;
import Model.ImportCartItem;
import Model.ExportCartItem;
import Model.User;
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
 * Controller handling import request functionality
 * Allows admin to create import requests from suppliers to warehouses
 */
@WebServlet(name = "ImportRequestController", urlPatterns = {"/import-request"})
public class ImportRequestController extends HttpServlet {

    private static final int PRODUCTS_PER_PAGE = 12;
    
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();
    private final ProductDetailDAO productDetailDAO = new ProductDetailDAO();
    private final StockMovementDAO stockMovementDAO = new StockMovementDAO();
    private final BranchDAO branchDAO = new BranchDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        // Check authorization - admin (role 0) and branch manager (role 1) can access
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        
        int roleId = currentUser.getRoleId();
        
        // Only admin (0) and branch manager (1) can access
        if (roleId != 0 && roleId != 1) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        // Role-based handling
        if (roleId == 0) {
            // Admin: Import from supplier to warehouse
            handleAdminImportRequest(request, response, session, currentUser);
        } else if (roleId == 1) {
            // Branch Manager: Export from warehouse to branch
            handleBranchManagerExportRequest(request, response, session, currentUser);
        }
    }
    
    /**
     * Handle admin import request (from supplier to warehouse)
     */
    private void handleAdminImportRequest(HttpServletRequest request, HttpServletResponse response, 
            HttpSession session, User currentUser) throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Handle reset action
        if ("reset".equals(action)) {
            handleReset(request, response);
            return;
        }
        
        // Get supplier ID from parameter
        String supplierIdParam = request.getParameter("supplierId");
        Integer selectedSupplierID = null;
        
        if (supplierIdParam != null && !supplierIdParam.isEmpty()) {
            try {
                selectedSupplierID = Integer.parseInt(supplierIdParam);
                session.setAttribute("cartSupplierId", selectedSupplierID);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            // Get from session if available
            if (session.getAttribute("cartSupplierId") != null) {
                selectedSupplierID = (Integer) session.getAttribute("cartSupplierId");
            }
        }
        
        // Load suppliers list
        List<Supplier> listSuppliers = supplierDAO.getAllSuppliers();
        System.out.println("=== DEBUG: Load Suppliers ===");
        System.out.println("Total suppliers loaded: " + (listSuppliers != null ? listSuppliers.size() : "NULL"));
        if (listSuppliers != null && !listSuppliers.isEmpty()) {
            for (Supplier s : listSuppliers) {
                System.out.println("  - Supplier ID: " + s.getSupplierId() + ", Name: " + s.getSupplierName());
            }
        }
        request.setAttribute("listSuppliers", listSuppliers);
        
        // Load warehouses list
        List<Warehouse> listWarehouse = warehouseDAO.getAllWarehouses();
        System.out.println("=== DEBUG: Load Warehouses ===");
        System.out.println("Total warehouses loaded: " + (listWarehouse != null ? listWarehouse.size() : "NULL"));
        request.setAttribute("listWarehouse", listWarehouse);
        
        // Get cart items from session
        @SuppressWarnings("unchecked")
        List<ImportCartItem> cartItems = (List<ImportCartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }
        request.setAttribute("cartItems", cartItems);
        
        // Set selected supplier ID
        request.setAttribute("selectedSupplierID", selectedSupplierID);
        
        // Get selected warehouse from session
        Integer selectedToWarehouseID = (Integer) session.getAttribute("selectedToWarehouseID");
        request.setAttribute("selectedToWarehouseID", selectedToWarehouseID);
        
        // If supplier is selected, load products
        if (selectedSupplierID != null) {
            loadProducts(request, selectedSupplierID);
        }
        
        request.getRequestDispatcher("/WEB-INF/jsp/admin/import_request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        // Check authorization - Allow both Admin (0) and Branch Manager (1)
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        
        int roleId = currentUser.getRoleId();
        if (roleId != 0 && roleId != 1) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        String action = request.getParameter("action");
        
        // Role = 0 (Admin): Import from supplier
        // Role = 1 (Branch Manager): Export to branch
        // Both roles use the same actions but with different cart types
        
        switch (action) {
            case "add":
                handleAddProduct(request, response);
                break;
            case "remove":
                handleRemoveProduct(request, response);
                break;
            case "updateQuantity":
                handleUpdateQuantity(request, response);
                break;
            case "reset":
                handleReset(request, response);
                break;
            case "submitRequest":
                handleSubmitRequest(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/import-request");
                break;
        }
    }
    
    /**
     * Load products from selected supplier with pagination and search
     */
    private void loadProducts(HttpServletRequest request, int supplierID) {
        System.out.println("=== DEBUG: loadProducts called ===");
        System.out.println("Supplier ID: " + supplierID);
        
        String keyword = request.getParameter("keyword");
        String pageParam = request.getParameter("page");
        
        int currentPage = 1;
        if (pageParam != null) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        
        // Get products by supplier
        List<ProductDetail> allProducts;
        if (keyword != null && !keyword.trim().isEmpty()) {
            System.out.println("Searching products with keyword: " + keyword);
            allProducts = productDetailDAO.searchBySupplierAndKeyword(supplierID, keyword.trim());
            request.setAttribute("keyword", keyword);
        } else {
            System.out.println("Loading all products for supplier");
            allProducts = productDetailDAO.getProductDetailsBySupplier(supplierID);
        }
        
        System.out.println("Total products found: " + (allProducts != null ? allProducts.size() : "NULL"));
        if (allProducts != null && !allProducts.isEmpty()) {
            for (int i = 0; i < Math.min(5, allProducts.size()); i++) {
                ProductDetail p = allProducts.get(i);
                System.out.println("  - Product " + (i+1) + ": ID=" + p.getProductDetailID() + ", Code=" + p.getProductCode() + ", Name=" + p.getProductNameUnsigned());
            }
        }
        
        // Calculate pagination
        int totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / PRODUCTS_PER_PAGE);
        
        // Get products for current page
        int startIndex = (currentPage - 1) * PRODUCTS_PER_PAGE;
        int endIndex = Math.min(startIndex + PRODUCTS_PER_PAGE, totalProducts);
        
        List<ProductDetail> pageProducts = new ArrayList<>();
        if (startIndex < totalProducts) {
            pageProducts = allProducts.subList(startIndex, endIndex);
        }
        
        request.setAttribute("listProductDetails", pageProducts);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
    }
    
    /**
     * Add product to import cart
     */
    private void handleAddProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        int roleId = currentUser != null ? currentUser.getRoleId() : -1;

        // Branch Manager (role=1) -> export cart behavior
        if (roleId == 1) {
            try {
                // Get product ID from either parameter name
                String productParam = request.getParameter("productDetailId");
                if (productParam == null) productParam = request.getParameter("productDetailID");
                int productDetailID = Integer.parseInt(productParam);
                
                // Warehouse will be selected in form on submit, not here
                // Just add product to cart
                int quantity = 1;
                try { quantity = Integer.parseInt(request.getParameter("quantity")); } catch (Exception x) {}

                ProductDetail pd = productDetailDAO.getProductDetailById(productDetailID);
                if (pd == null) {
                    session.setAttribute("errorMessage", "Sản phẩm không tồn tại!");
                    response.sendRedirect(request.getContextPath() + "/import-request");
                    return;
                }

                @SuppressWarnings("unchecked")
                List<ExportCartItem> cart = (List<ExportCartItem>) session.getAttribute("exportCart");
                if (cart == null) {
                    cart = new ArrayList<>();
                    session.setAttribute("exportCart", cart);
                }

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
                response.sendRedirect(request.getContextPath() + "/import-request");

            } catch (NumberFormatException e) {
                e.printStackTrace();
                session.setAttribute("errorMessage", "Dữ liệu không hợp lệ!");
                response.sendRedirect(request.getContextPath() + "/import-request");
            }
            return;
        }

        // Default: Admin import behavior (existing)
        String productDetailIdParam = request.getParameter("productDetailID");
        String supplierIdParam = request.getParameter("supplierId");
        
        try {
            int productDetailID = Integer.parseInt(productDetailIdParam);
            int supplierID = Integer.parseInt(supplierIdParam);
            
            // Get cart from session
            @SuppressWarnings("unchecked")
            List<ImportCartItem> cartItems = (List<ImportCartItem>) session.getAttribute("cartItems");
            if (cartItems == null) {
                cartItems = new ArrayList<>();
            }
            
            // Check if product already in cart
            boolean found = false;
            for (ImportCartItem item : cartItems) {
                if (item.getProductDetailID() == productDetailID) {
                    item.setQuantity(item.getQuantity() + 1);
                    found = true;
                    break;
                }
            }
            
            // If not in cart, add new
            if (!found) {
                ProductDetail product = productDetailDAO.getProductDetailById(productDetailID);
                if (product != null) {
                    ImportCartItem newItem = new ImportCartItem(
                        productDetailID,
                        product.getProductCode(),
                        product.getProductNameUnsigned(),
                        1,
                        product.getCostPrice()
                    );
                    cartItems.add(newItem);
                }
            }
            
            session.setAttribute("cartItems", cartItems);
            session.setAttribute("cartSupplierId", supplierID);
            
            response.sendRedirect(request.getContextPath() + "/import-request?supplierId=" + supplierID);
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/import-request");
        }
    }
    
    /**
     * Remove product from import cart
     */
    private void handleRemoveProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        int roleId = currentUser != null ? currentUser.getRoleId() : -1;

        if (roleId == 1) {
            // export cart removal
            try {
                String productParam = request.getParameter("productDetailId");
                if (productParam == null) productParam = request.getParameter("productDetailID");
                int productDetailID = Integer.parseInt(productParam);

                @SuppressWarnings("unchecked")
                List<ExportCartItem> cart = (List<ExportCartItem>) session.getAttribute("exportCart");

                if (cart != null) {
                    cart.removeIf(item -> item.getProductDetailID() == productDetailID);
                    if (cart.isEmpty()) {
                        session.removeAttribute("exportCart");
                    }
                }

                response.sendRedirect(request.getContextPath() + "/import-request");
                return;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/import-request");
                return;
            }
        }

        // import cart removal
        String productDetailIdParam = request.getParameter("productDetailID");
        String supplierIdParam = request.getParameter("supplierId");
        
        try {
            int productDetailID = Integer.parseInt(productDetailIdParam);
            
            @SuppressWarnings("unchecked")
            List<ImportCartItem> cartItems = (List<ImportCartItem>) session.getAttribute("cartItems");
            
            if (cartItems != null) {
                cartItems.removeIf(item -> item.getProductDetailID() == productDetailID);
                session.setAttribute("cartItems", cartItems);
            }
            
            response.sendRedirect(request.getContextPath() + "/import-request?supplierId=" + supplierIdParam);
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/import-request");
        }
    }
    
    /**
     * Update quantity of product in cart
     */
    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        int roleId = currentUser != null ? currentUser.getRoleId() : -1;

        if (roleId == 1) {
            // export cart update
            try {
                String productParam = request.getParameter("productDetailId");
                if (productParam == null) productParam = request.getParameter("productDetailID");
                int productDetailID = Integer.parseInt(productParam);
                int quantity = Integer.parseInt(request.getParameter("quantity"));

                if (quantity <= 0) quantity = 1;

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

                response.sendRedirect(request.getContextPath() + "/import-request");
                return;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/import-request");
                return;
            }
        }

        // import cart update
        String productDetailIdParam = request.getParameter("productDetailID");
        String quantityParam = request.getParameter("quantity");
        String supplierIdParam = request.getParameter("supplierId");
        
        try {
            int productDetailID = Integer.parseInt(productDetailIdParam);
            int quantity = Integer.parseInt(quantityParam);
            
            if (quantity < 1) quantity = 1;
            
            @SuppressWarnings("unchecked")
            List<ImportCartItem> cartItems = (List<ImportCartItem>) session.getAttribute("cartItems");
            
            if (cartItems != null) {
                for (ImportCartItem item : cartItems) {
                    if (item.getProductDetailID() == productDetailID) {
                        item.setQuantity(quantity);
                        break;
                    }
                }
                session.setAttribute("cartItems", cartItems);
            }
            
            response.sendRedirect(request.getContextPath() + "/import-request?supplierId=" + supplierIdParam);
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/import-request");
        }
    }
    
    /**
     * Reset/clear the import cart
     */
    private void handleReset(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        int roleId = currentUser != null ? currentUser.getRoleId() : -1;

        if (roleId == 1) {
            // reset export cart
            session.removeAttribute("exportCart");
            session.setAttribute("successMessage", "Đã xóa toàn bộ giỏ hàng!");
            response.sendRedirect(request.getContextPath() + "/import-request");
            return;
        }

        // reset import cart
        session.removeAttribute("cartItems");
        session.removeAttribute("cartSupplierId");
        session.removeAttribute("selectedToWarehouseID");

        String supplierIdParam = request.getParameter("supplierId");

        if (supplierIdParam != null && !supplierIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/import-request?supplierId=" + supplierIdParam);
        } else {
            response.sendRedirect(request.getContextPath() + "/import-request");
        }
    }
    
    /**
     * Submit import request to database
     */
    private void handleSubmitRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        System.out.println("=== DEBUG: handleSubmitRequest called ===");
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        System.out.println("Current user: " + (currentUser != null ? currentUser.getUserId() : "NULL"));
        
        String toWarehouseIDParam = request.getParameter("toWarehouseID");
        String overallNote = request.getParameter("overallNote");
        
        System.out.println("ToWarehouseID param: " + toWarehouseIDParam);
        System.out.println("Overall note: " + overallNote);
        
        try {
            // If role is branch manager, submit export request
//            User currentUser = (User) session.getAttribute("currentUser");
            int roleId = currentUser != null ? currentUser.getRoleId() : -1;

            if (roleId == 1) {
                // Branch Manager submits export request
                // Get warehouse from form parameter (user selects before submit)
                String fromWarehouseIdParam = request.getParameter("fromWarehouseID");
                if (fromWarehouseIdParam == null || fromWarehouseIdParam.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Vui lòng chọn kho xuất hàng!");
                    response.sendRedirect(request.getContextPath() + "/import-request");
                    return;
                }
                
                int fromWarehouseId = Integer.parseInt(fromWarehouseIdParam);
                Integer branchId = currentUser.getBranchId();
                String note = request.getParameter("overallNote");

                @SuppressWarnings("unchecked")
                List<ExportCartItem> exportCart = (List<ExportCartItem>) session.getAttribute("exportCart");

                if (branchId == null || exportCart == null || exportCart.isEmpty()) {
                    session.setAttribute("errorMessage", "Thông tin không đầy đủ hoặc giỏ hàng trống!");
                    response.sendRedirect(request.getContextPath() + "/import-request");
                    return;
                }

                boolean success = stockMovementDAO.createExportRequest(
                    fromWarehouseId,
                    branchId,
                    currentUser.getUserId(),
                    exportCart,
                    note
                );

                if (success) {
                    session.removeAttribute("exportCart");
                    session.setAttribute("successMessage", "Tạo yêu cầu xuất kho thành công!");
                    response.sendRedirect(request.getContextPath() + "/import-request");
                } else {
                    session.setAttribute("errorMessage", "Có lỗi xảy ra khi tạo yêu cầu xuất kho!");
                    response.sendRedirect(request.getContextPath() + "/import-request");
                }
                return;
            }

            // Otherwise role = 0 -> import request (existing logic)
            int toWarehouseID = Integer.parseInt(toWarehouseIDParam);
            Integer supplierID = (Integer) session.getAttribute("cartSupplierId");
            
            @SuppressWarnings("unchecked")
            List<ImportCartItem> cartItems = (List<ImportCartItem>) session.getAttribute("cartItems");
            
            // Validation
            if (supplierID == null || cartItems == null || cartItems.isEmpty()) {
                session.setAttribute("errorMessage", "Giỏ hàng rỗng! Vui lòng thêm sản phẩm.");
                response.sendRedirect(request.getContextPath() + "/import-request");
                return;
            }
            
            if (toWarehouseID == 0) {
                session.setAttribute("errorMessage", "Vui lòng chọn kho đích!");
                response.sendRedirect(request.getContextPath() + "/import-request?supplierId=" + supplierID);
                return;
            }
            
            boolean success = stockMovementDAO.createImportRequest(
                supplierID,
                toWarehouseID,
                currentUser.getUserId(),
                cartItems,
                overallNote
            );
            
            if (success) {
                // Clear cart
                session.removeAttribute("cartItems");
                session.removeAttribute("cartSupplierId");
                session.removeAttribute("selectedToWarehouseID");
                
                session.setAttribute("successMessage", "Tạo yêu cầu nhập hàng thành công!");
                response.sendRedirect(request.getContextPath() + "/import-request");
            } else {
                session.setAttribute("errorMessage", "Có lỗi xảy ra khi tạo yêu cầu nhập hàng!");
                response.sendRedirect(request.getContextPath() + "/import-request?supplierId=" + supplierID);
            }
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/import-request");
        }
    }
    
    /**
     * Handle branch manager export request (from warehouse to branch)
     * Simple approach: display all products + warehouse dropdown, no pre-filtering
     */
    private void handleBranchManagerExportRequest(HttpServletRequest request, HttpServletResponse response, 
            HttpSession session, User currentUser) throws ServletException, IOException {
        
        // Get branch manager's branch ID
        Integer branchId = currentUser.getBranchId();
        if (branchId == null) {
            request.setAttribute("errorMessage", "Bạn chưa được gán chi nhánh!");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/error.jsp").forward(request, response);
            return;
        }
        
        // Get search keyword and page
        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";
        
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (Exception e) {}
        
        // Load ALL warehouses for dropdown selection
        List<Warehouse> listWarehouses = warehouseDAO.getAllWarehouses();
        request.setAttribute("listWarehouses", listWarehouses);
        
        // Load ALL products (no warehouse pre-filtering)
        List<ProductDetail> allProducts;
        if (keyword != null && !keyword.trim().isEmpty()) {
            allProducts = productDetailDAO.searchProductDetails(keyword.trim());
            request.setAttribute("keyword", keyword);
        } else {
            allProducts = productDetailDAO.getAllProductDetails();
        }
        
        // Pagination
        int pageSize = 12;
        int totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalProducts);
        
        List<ProductDetail> pagedProducts = allProducts.subList(fromIndex, toIndex);
        
        request.setAttribute("listProductDetails", pagedProducts);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        
        // Get export cart from session
        @SuppressWarnings("unchecked")
        List<ExportCartItem> exportCart = (List<ExportCartItem>) session.getAttribute("exportCart");
        if (exportCart == null) {
            exportCart = new ArrayList<>();
            session.setAttribute("exportCart", exportCart);
        }
        request.setAttribute("exportCart", exportCart);
        
        // Get selected warehouse from session (for cart display)
        Integer cartWarehouseId = (Integer) session.getAttribute("cartWarehouseId");
        request.setAttribute("cartWarehouseId", cartWarehouseId);
        
        // Forward to export request JSP
        request.getRequestDispatcher("/WEB-INF/jsp/admin/export_request.jsp").forward(request, response);
    }
}

