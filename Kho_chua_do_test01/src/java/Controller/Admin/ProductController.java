package Controller.Admin;

import DAL.ProductDAO;
import DAL.ProductDetailDAO;
import DAL.CategoryDAO;
import DAL.BrandDAO;
import DAL.SupplierDAO;
import DAL.BranchDAO;

import Model.Product;
import Model.ProductDetail;
import Model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ProductController", urlPatterns = {"/product"})
public class ProductController extends HttpServlet {

    private static final int DEFAULT_STOCK_THRESHOLD = 30;
    private static final int DEFAULT_BRANCH_ID_FOR_QTY = 1; // nếu không chọn chi nhánh, dùng 1

    private ProductDAO  productDAO;
    private ProductDetailDAO productDetailDAO;
    private BrandDAO    brandDAO;
    private SupplierDAO supplierDAO;
    private CategoryDAO categoryDAO;
    private BranchDAO   branchDAO; // dùng cho dropdown chi nhánh khi chỉnh tồn

    @Override
    public void init() {
        productDAO  = new ProductDAO();
        productDetailDAO = new ProductDetailDAO();
        brandDAO    = new BrandDAO();
        supplierDAO = new SupplierDAO();
        categoryDAO = new CategoryDAO();
        branchDAO   = new BranchDAO(); // nếu chưa cần dropdown chi nhánh có thể bỏ
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "add"    -> showAddForm(request, response);
                case "edit"   -> showEditForm(request, response);   // -> ProductForm.jsp
                case "delete" -> deleteProduct(request, response);
                case "detail" -> showDetail(request, response);     // -> test.jsp (xem chi tiết)
                case "list"   -> listProducts(request, response);
                default       -> listProducts(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Kiểm tra xem response đã được committed chưa
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Đã xảy ra lỗi khi xử lý yêu cầu");
            }
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
                default       -> doGet(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Kiểm tra xem response đã được committed chưa
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Đã xảy ra lỗi khi xử lý dữ liệu gửi lên");
            }
        }
    }

    /* ========================= LISTING ========================= */
    private void listProducts(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Kiểm tra role và branch của user từ session
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        User currentUser = null;
        Integer roleID = null;
        Integer branchID = null;
        
        if (session != null) {
            currentUser = (User) session.getAttribute("currentUser");
            roleID = (Integer) session.getAttribute("roleID");
            branchID = (Integer) session.getAttribute("branchID");
            
            System.out.println("[DEBUG ProductController] currentUser: " + currentUser);
            System.out.println("[DEBUG ProductController] roleID from session: " + roleID);
            System.out.println("[DEBUG ProductController] branchID from session: " + branchID);
        }
        
        // Đọc ProductName từ form. Nếu chưa đổi form, fallback về "keyword"
        String productName = trimToNull(request.getParameter("productName"));
        if (productName == null) {
            productName = trimToNull(request.getParameter("keyword"));
        }

        // Lọc theo nhiều categoryName (theo tên) + tồn kho
        String[] rawCatNames = request.getParameterValues("categoryName");
        List<String> categoryNames = parseStrList(rawCatNames);
        if (categoryNames == null || categoryNames.isEmpty()) {
            String categoryNameCsv = trimToNull(request.getParameter("categoryName"));
            List<String> fromCsv = parseCsvStrList(categoryNameCsv);
            if (fromCsv != null && !fromCsv.isEmpty()) categoryNames = fromCsv;
        }

        // stock: all | in | out | belowMin | aboveMax
        String stock = request.getParameter("stock");
        if (stock == null || stock.isBlank()) stock = "all";

        int threshold = parseIntOrDefault(request.getParameter("stockThreshold"), DEFAULT_STOCK_THRESHOLD);

        List<Product> products;
        
        // Kiểm tra role: 
        // - Role 0 = Admin: xem tất cả sản phẩm, có quyền CRUD
        // - Role 1 = Manager: chỉ xem sản phẩm của chi nhánh, không có quyền CRUD
        if (roleID != null && roleID == 1 && branchID != null) {
            // Manager (Role 1) - chỉ lấy sản phẩm theo chi nhánh
            System.out.println("[DEBUG] Manager accessing products for branchID: " + branchID);
            products = getProductsByBranch(branchID, categoryNames, productName, stock, threshold);
        } else {
            // Admin (Role 0) hoặc khác - lấy tất cả sản phẩm
            System.out.println("[DEBUG] Admin accessing all products");
            products = productDAO.listProducts(
                    categoryNames,
                    productName,
                    ProductDAO.StockFilter.from(stock),
                    threshold
            );
        }

        // Load ProductDetails để có thêm thông tin chi tiết
        List<ProductDetail> allProductDetails = productDetailDAO.getAllProductDetails();
        
        // Tạo Map để lookup ProductDetail theo ProductID
        java.util.Map<Integer, ProductDetail> productDetailMap = new java.util.HashMap<>();
        for (ProductDetail detail : allProductDetails) {
            if (!productDetailMap.containsKey(detail.getProductID())) {
                productDetailMap.put(detail.getProductID(), detail);
            }
        }
        
        // Set ProductDetail vào request để JSP có thể truy cập
        request.setAttribute("productDetailMap", productDetailMap);

        // Lọc theo trạng thái (thêm mới)
        String status = request.getParameter("status");
        if (status == null || status.isBlank()) status = "all";
        
        // Filter products by status (isActive)
        System.out.println("[DEBUG] Status filter: " + status);
        System.out.println("[DEBUG] Products before status filter: " + products.size());
        
        if (!"all".equals(status)) {
            List<Product> filteredProducts = new ArrayList<>();
            for (Product product : products) {
                boolean isActive = product.getIsActive() != null && product.getIsActive();
                System.out.println("[DEBUG] Product " + product.getProductId() + " - isActive: " + isActive + ", filter: " + status);
                
                if ("active".equals(status) && isActive) {
                    filteredProducts.add(product);
                } else if ("inactive".equals(status) && !isActive) {
                    filteredProducts.add(product);
                }
            }
            products = filteredProducts;
            System.out.println("[DEBUG] Products after status filter: " + products.size());
        }
        
        // Tạo formatted date map cho JSP (sau khi đã filter)
        java.util.Map<Integer, String> formattedDateMap = new java.util.HashMap<>();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Product product : products) {
            if (product.getCreatedAt() != null) {
                formattedDateMap.put(product.getProductId(), product.getCreatedAt().format(formatter));
            }
        }
        request.setAttribute("formattedDateMap", formattedDateMap);
        
        // Đẩy dữ liệu ra view (giữ lại các giá trị người dùng nhập/chọn)
        request.setAttribute("products", products);
        request.setAttribute("productName", productName);                // nếu view dùng ${productName}
        request.setAttribute("keyword", productName);                    // tương thích view cũ dùng ${keyword}
        request.setAttribute("selectedCategoryNames", categoryNames);
        request.setAttribute("stock", stock);
        request.setAttribute("stockThreshold", threshold);
        request.setAttribute("status", status);

        // Sidebar danh mục
        request.setAttribute("categories", categoryDAO.getAll());

        request.getRequestDispatcher("/WEB-INF/jsp/admin/product.jsp").forward(request, response);
    }

    /* ========================= FORMS/DETAIL ========================= */

    /** Thêm mới → ProductForm.jsp (dropdown theo tên) */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Kiểm tra quyền Admin - chỉ Admin (Role 0) mới được thêm sản phẩm
        HttpSession session = request.getSession(false);
        Integer roleID = (session != null) ? (Integer) session.getAttribute("roleID") : null;
        if (roleID == null || roleID != 0) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ Admin mới có quyền thêm sản phẩm");
            return;
        }

        request.setAttribute("action", "insert");
        pushLookups(request); // nạp brands/categories/suppliers/(branches)
        request.getRequestDispatcher("/WEB-INF/jsp/admin/ProductForm.jsp").forward(request, response);
    }

    /** Sửa → ProductForm.jsp (từ test.jsp bấm Chỉnh sửa sẽ tới form này) */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Kiểm tra quyền Admin - chỉ Admin (Role 0) mới được chỉnh sửa
        HttpSession session = request.getSession(false);
        Integer roleID = (session != null) ? (Integer) session.getAttribute("roleID") : null;
        if (roleID == null || roleID != 0) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ Admin mới có quyền chỉnh sửa sản phẩm");
            return;
        }

        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }

        // Lấy kèm tổng tồn để hiện trong form nếu cần
        Product product;
        try {
            product = productDAO.getProductByIdWithQty(id);
        } catch (NoSuchMethodError | RuntimeException ex) {
            // fallback nếu DAO của bạn chưa có getProductByIdWithQty
            product = productDAO.getProductById(id);
        }

        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy sản phẩm");
            return;
        }

        request.setAttribute("product", product);
        request.setAttribute("action", "update");
        pushLookups(request); // nạp các dropdown
        request.getRequestDispatcher("/WEB-INF/jsp/admin/ProductForm.jsp").forward(request, response);
    }

    /** Xem chi tiết → test.jsp */
    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }

        Product product;
        try {
            product = productDAO.getProductByIdWithQty(id);
        } catch (NoSuchMethodError | RuntimeException ex) {
            product = productDAO.getProductById(id);
        }

        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy sản phẩm");
            return;
        }

        request.setAttribute("product", product);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/test.jsp").forward(request, response);
    }

    /* ========================= CRUD ========================= */
    private void insertProduct(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Kiểm tra quyền Admin - chỉ Admin (Role 0) mới được thêm sản phẩm
        HttpSession session = request.getSession(false);
        Integer roleID = (session != null) ? (Integer) session.getAttribute("roleID") : null;
        if (roleID == null || roleID != 0) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ Admin mới có quyền thêm sản phẩm");
            return;
        }

        Product p = extractProductFromRequest(request);
        productDAO.insertProduct(p);

        // (tùy chọn) nếu muốn set tồn luôn sau khi insert, bạn có thể:
        // int newId = productDAO.insertProductReturningId(p);
        // int qty   = parseIntOrDefault(request.getParameter("quantity"), 0);
        // int branchId = parseIntOrDefault(request.getParameter("branchId"), DEFAULT_BRANCH_ID_FOR_QTY);
        // productDAO.setQuantityForProductAtBranch(newId, branchId, qty);

        response.sendRedirect("product");
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Kiểm tra quyền Admin - chỉ Admin (Role 0) mới được cập nhật sản phẩm
        HttpSession session = request.getSession(false);
        Integer roleID = (session != null) ? (Integer) session.getAttribute("roleID") : null;
        if (roleID == null || roleID != 0) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ Admin mới có quyền cập nhật sản phẩm");
            return;
        }

        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }

        Product p = extractProductFromRequest(request);
        p.setProductId(id);
        productDAO.updateProduct(p);

        // (tùy chọn) cập nhật tồn kho nếu form có gửi
        String rawQty = trimToNull(request.getParameter("quantity"));
        if (rawQty != null && !rawQty.isBlank()) {
            try {
                int newQty = Integer.parseInt(rawQty.trim());
                int branchId = parseIntOrDefault(request.getParameter("branchId"), DEFAULT_BRANCH_ID_FOR_QTY);
                productDAO.setQuantityForProductAtBranch(id, branchId, newQty);
            } catch (NumberFormatException ignore) { /* bỏ qua nếu giá trị không hợp lệ */ }
        }

        response.sendRedirect("product");
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Integer id = parseIntOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai định dạng id");
            return;
        }
        productDAO.deleteProduct(id);
        response.sendRedirect("product?action=list");
    }

    /* ========================= HELPERS ========================= */

    /** Nạp dữ liệu cho dropdown trong ProductForm.jsp */
    private void pushLookups(HttpServletRequest req) {
        req.setAttribute("brands",     brandDAO.getAll());     // [{brandId, brandName}, ...]
        req.setAttribute("categories", categoryDAO.getAll());  // [{categoryId, categoryName}, ...]
        req.setAttribute("suppliers",  supplierDAO.getAllSuppliers());  // [{supplierId, supplierName}, ...]
        req.setAttribute("branches",   branchDAO.getAllBranches());    // dùng cho chọn chi nhánh khi chỉnh tồn
    }

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private int parseIntOrDefault(String s, int def) {
        try { return (s == null || s.isBlank()) ? def : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return def; }
    }

    private BigDecimal parseBigDecimalOrNull(String s) {
        try { return (s == null || s.isBlank()) ? null : new BigDecimal(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private List<String> parseStrList(String[] arr) {
        if (arr == null || arr.length == 0) return null;
        List<String> out = new ArrayList<>();
        for (String s : arr) {
            String v = trimToNull(s);
            if (v != null) out.add(v);
        }
        return out.isEmpty() ? null : out;
    }

    private List<String> parseCsvStrList(String csv) {
        if (csv == null || csv.isBlank()) return null;
        String[] parts = csv.split(",");
        return parseStrList(parts);
    }

    /** Map form -> Product: dùng TÊN (brandName/categoryName/supplierName) */
    private Product extractProductFromRequest(HttpServletRequest request) {
        Product p = new Product();
        p.setProductName(trimToNull(request.getParameter("name")));

        // Dùng TÊN thay vì ID (DAO sẽ resolve -> ID)
        p.setBrandName(trimToNull(request.getParameter("brandName")));
        p.setCategoryName(trimToNull(request.getParameter("categoryName")));
        p.setSupplierName(trimToNull(request.getParameter("supplierName")));

        p.setCostPrice(parseBigDecimalOrNull(request.getParameter("costPrice")));
        p.setRetailPrice(parseBigDecimalOrNull(request.getParameter("retailPrice")));
        p.setVat(parseBigDecimalOrNull(request.getParameter("vat")));
        p.setImageUrl(trimToNull(request.getParameter("imageUrl")));

        String rawIsActive = request.getParameter("isActive");
        // Checkbox: null = không check = false, "on"/"true" = check = true
        boolean isActive = false;
        if (rawIsActive != null) {
            isActive = ("on".equalsIgnoreCase(rawIsActive) || "true".equalsIgnoreCase(rawIsActive));
        }
        p.setIsActive(isActive);

        return p;
    }

    /**
     * Lấy sản phẩm theo chi nhánh cho Manager
     * Dựa vào bảng Inventory, InventoryProduct và ProductDetail
     */
    private List<Product> getProductsByBranch(Integer branchId, List<String> categoryNames, 
                                            String productName, String stock, int threshold) {
        try {
            // Sử dụng ProductDAO với branchId filter
            return productDAO.listProductsByBranch(branchId, categoryNames, productName, 
                                                 ProductDAO.StockFilter.from(stock), threshold);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tạo redirect URL với các filter parameters hiện tại
     */
    private String buildRedirectUrlWithCurrentFilters(HttpServletRequest request) {
        StringBuilder url = new StringBuilder("product?action=list");
        
        // Preserve status filter
        String status = request.getParameter("status");
        if (status != null && !status.isBlank()) {
            url.append("&status=").append(status);
        }
        
        // Preserve product name search
        String productName = request.getParameter("productName");
        if (productName == null) {
            productName = request.getParameter("keyword");
        }
        if (productName != null && !productName.isBlank()) {
            url.append("&productName=").append(java.net.URLEncoder.encode(productName, java.nio.charset.StandardCharsets.UTF_8));
        }
        
        // Preserve category filters
        String[] categoryNames = request.getParameterValues("categoryName");
        if (categoryNames != null) {
            for (String cat : categoryNames) {
                if (cat != null && !cat.isBlank()) {
                    url.append("&categoryName=").append(java.net.URLEncoder.encode(cat, java.nio.charset.StandardCharsets.UTF_8));
                }
            }
        }
        
        // Preserve stock filter
        String stock = request.getParameter("stock");
        if (stock != null && !stock.isBlank()) {
            url.append("&stock=").append(stock);
        }
        
        // Preserve stock threshold
        String threshold = request.getParameter("stockThreshold");
        if (threshold != null && !threshold.isBlank()) {
            url.append("&stockThreshold=").append(threshold);
        }
        
        return url.toString();
    }
}
