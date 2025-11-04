package DAL;

import Model.ProductDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailDAO extends DataBaseContext {

    public static class DetailMini {
        private int id;
        private String name; // product name or variant
        private String sku; // using ProductCode in DB
        private Integer stock; // nullable

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }

    public List<DetailMini> search(String keyword) {
        List<DetailMini> list = new ArrayList<>();
        String kw = keyword == null ? "" : keyword.trim();
        String sql = """
            SELECT TOP 15 pd.ProductDetailID,
                   p.ProductName,
                   pd.ProductCode AS SKU,
                   SUM(ISNULL(ip.Quantity,0)) AS Stock
            FROM ProductDetails pd
            JOIN Products p ON p.ProductID = pd.ProductID
            LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
            WHERE (? = '' OR p.ProductName LIKE ? OR pd.ProductCode LIKE ? OR CAST(pd.ProductDetailID AS NVARCHAR(20)) LIKE ?)
            GROUP BY pd.ProductDetailID, p.ProductName, pd.ProductCode
            ORDER BY p.ProductName, pd.ProductDetailID
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, "%" + kw + "%");
            ps.setString(3, "%" + kw + "%");
            ps.setString(4, "%" + kw + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetailMini d = new DetailMini();
                    d.setId(rs.getInt("ProductDetailID"));
                    d.setName(rs.getString("ProductName"));
                    d.setSku(rs.getString("SKU"));
                    Object st = rs.getObject("Stock");
                    d.setStock(st == null ? null : rs.getInt("Stock"));
                    list.add(d);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public String getLabelById(int productDetailId) {
        String sql = """
            SELECT TOP 1 p.ProductName, pd.SKU
            FROM ProductDetails pd
            JOIN Products p ON p.ProductID = pd.ProductID
            WHERE pd.ProductDetailID = ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productDetailId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("ProductName");
                    String sku = rs.getString("SKU");
                    return (name == null ? "" : name) + (sku == null ? "" : (" - " + sku));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public java.util.Map<Integer,String> getLabelsByIds(java.util.Set<Integer> ids) {
        java.util.Map<Integer,String> map = new java.util.HashMap<>();
        if (ids == null || ids.isEmpty()) return map;
        StringBuilder in = new StringBuilder();
        in.append("(");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) in.append(',');
            in.append('?');
        }
        in.append(")");
        String sql = "SELECT pd.ProductDetailID, p.ProductName, pd.ProductCode AS SKU FROM ProductDetails pd JOIN Products p ON p.ProductID=pd.ProductID WHERE pd.ProductDetailID IN " + in;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;
            for (Integer id : ids) ps.setInt(idx++, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("ProductDetailID");
                    String name = rs.getString("ProductName");
                    String sku = rs.getString("SKU");
                    map.put(id, (name == null ? "" : name) + (sku == null ? "" : (" - " + sku)));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public static class DetailInfo {
        private int id;
        private String productName;
        private String name;
        private String sku; // ProductCode
        private String imageUrl;
        private String categoryName;
        private java.math.BigDecimal retailPrice;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public java.math.BigDecimal getRetailPrice() { return retailPrice; }
        public void setRetailPrice(java.math.BigDecimal retailPrice) { this.retailPrice = retailPrice; }
    }

    public java.util.Map<Integer, DetailInfo> getInfoByIds(java.util.Set<Integer> ids) {
        java.util.Map<Integer, DetailInfo> map = new java.util.HashMap<>();
        if (ids == null || ids.isEmpty()) return map;
        StringBuilder in = new StringBuilder();
        in.append("(");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) in.append(',');
            in.append('?');
        }
        in.append(")");
        String sql = "SELECT pd.ProductDetailID, p.ProductName, pd.ProductCode AS SKU, p.ImageURL, p.RetailPrice, c.CategoryName " +
                     "FROM ProductDetails pd " +
                     "JOIN Products p ON p.ProductID = pd.ProductID " +
                     "LEFT JOIN Categories c ON c.CategoryID = p.CategoryID " +
                     "WHERE pd.ProductDetailID IN " + in;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;
            for (Integer id : ids) ps.setInt(idx++, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetailInfo info = new DetailInfo();
                    info.setId(rs.getInt("ProductDetailID"));
                    info.setProductName(rs.getString("ProductName"));
                    info.setName(rs.getString("ProductName")); // backward compatibility
                    info.setSku(rs.getString("SKU"));
                    info.setImageUrl(rs.getString("ImageURL"));
                    info.setRetailPrice(rs.getBigDecimal("RetailPrice"));
                    info.setCategoryName(rs.getString("CategoryName"));
                    map.put(info.getId(), info);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public DetailInfo getInfoByCode(String productCode) {
        String sql = "SELECT pd.ProductDetailID, p.ProductName, pd.ProductCode AS SKU, p.ImageURL, p.RetailPrice, c.CategoryName " +
                     "FROM ProductDetails pd " +
                     "JOIN Products p ON p.ProductID = pd.ProductID " +
                     "LEFT JOIN Categories c ON c.CategoryID = p.CategoryID " +
                     "WHERE pd.ProductCode = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, productCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DetailInfo info = new DetailInfo();
                    info.setId(rs.getInt("ProductDetailID"));
                    info.setProductName(rs.getString("ProductName"));
                    info.setName(rs.getString("ProductName")); // backward compatibility
                    info.setSku(rs.getString("SKU"));
                    info.setImageUrl(rs.getString("ImageURL"));
                    info.setRetailPrice(rs.getBigDecimal("RetailPrice"));
                    info.setCategoryName(rs.getString("CategoryName"));
                    return info;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    /**
     * Get product details by supplier ID
     */
    public List<ProductDetail> getProductDetailsBySupplier(int supplierID) {
        System.out.println("=== [ProductDetailDAO] getProductDetailsBySupplier called ===");
        System.out.println("SupplierID: " + supplierID);
        
        List<ProductDetail> list = new ArrayList<>();
        String sql = "SELECT pd.ProductDetailID, pd.ProductID, pd.Description, pd.ProductCode, " +
                     "pd.WarrantyPeriod, pd.ProductNameUnsigned, pd.CreatedAt, p.CostPrice, p.RetailPrice " +
                     "FROM ProductDetails pd " +
                     "JOIN Products p ON p.ProductID = pd.ProductID " +
                     "WHERE p.SupplierID = ? AND p.IsActive = 1 " +
                     "ORDER BY p.ProductName";
        
        System.out.println("SQL Query: " + sql);
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, supplierID);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    ProductDetail pd = new ProductDetail();
                    pd.setProductDetailID(rs.getInt("ProductDetailID"));
                    pd.setProductID(rs.getInt("ProductID"));
                    pd.setDescription(rs.getString("Description"));
                    pd.setProductCode(rs.getString("ProductCode"));
                    pd.setWarrantyPeriod(rs.getString("WarrantyPeriod"));
                    pd.setProductNameUnsigned(rs.getString("ProductNameUnsigned"));
                    pd.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    pd.setCostPrice(rs.getDouble("CostPrice"));
                    pd.setRetailPrice(rs.getDouble("RetailPrice"));
                    list.add(pd);
                    count++;
                }
                System.out.println("✅ [ProductDetailDAO] Loaded " + count + " products for supplier " + supplierID);
            }
        } catch (SQLException e) {
            System.err.println("❌ [ProductDetailDAO] Error loading products: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Search product details by supplier and keyword
     */
    public List<ProductDetail> searchBySupplierAndKeyword(int supplierID, String keyword) {
        List<ProductDetail> list = new ArrayList<>();
        String sql = "SELECT pd.ProductDetailID, pd.ProductID, pd.Description, pd.ProductCode, " +
                     "pd.WarrantyPeriod, pd.ProductNameUnsigned, pd.CreatedAt, p.CostPrice, p.RetailPrice " +
                     "FROM ProductDetails pd " +
                     "JOIN Products p ON p.ProductID = pd.ProductID " +
                     "WHERE p.SupplierID = ? AND p.IsActive = 1 " +
                     "AND (p.ProductName LIKE ? OR pd.ProductCode LIKE ? OR pd.ProductNameUnsigned LIKE ?) " +
                     "ORDER BY p.ProductName";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, supplierID);
            String keywordPattern = "%" + keyword + "%";
            ps.setString(2, keywordPattern);
            ps.setString(3, keywordPattern);
            ps.setString(4, keywordPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductDetail pd = new ProductDetail();
                    pd.setProductDetailID(rs.getInt("ProductDetailID"));
                    pd.setProductID(rs.getInt("ProductID"));
                    pd.setDescription(rs.getString("Description"));
                    pd.setProductCode(rs.getString("ProductCode"));
                    pd.setWarrantyPeriod(rs.getString("WarrantyPeriod"));
                    pd.setProductNameUnsigned(rs.getString("ProductNameUnsigned"));
                    pd.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    pd.setCostPrice(rs.getDouble("CostPrice"));
                    pd.setRetailPrice(rs.getDouble("RetailPrice"));
                    list.add(pd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Get product detail by ID
     */
    public ProductDetail getProductDetailById(int productDetailID) {
        String sql = "SELECT pd.ProductDetailID, pd.ProductID, pd.Description, pd.ProductCode, " +
                     "pd.WarrantyPeriod, pd.ProductNameUnsigned, pd.CreatedAt, p.CostPrice, p.RetailPrice, " +
                     "p.ProductName, p.ImageURL, s.SupplierName " +
                     "FROM ProductDetails pd " +
                     "JOIN Products p ON p.ProductID = pd.ProductID " +
                     "LEFT JOIN Suppliers s ON s.SupplierID = p.SupplierID " +
                     "WHERE pd.ProductDetailID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productDetailID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProductDetail pd = new ProductDetail();
                    pd.setProductDetailID(rs.getInt("ProductDetailID"));
                    pd.setProductID(rs.getInt("ProductID"));
                    pd.setDescription(rs.getString("Description"));
                    pd.setProductCode(rs.getString("ProductCode"));
                    pd.setWarrantyPeriod(rs.getString("WarrantyPeriod"));
                    pd.setProductNameUnsigned(rs.getString("ProductNameUnsigned"));
                    pd.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    pd.setCostPrice(rs.getDouble("CostPrice"));
                    pd.setRetailPrice(rs.getDouble("RetailPrice"));
                    pd.setProductName(rs.getString("ProductName"));
                    pd.setImageURL(rs.getString("ImageURL"));
                    pd.setSupplierName(rs.getString("SupplierName"));
                    return pd;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get product details available in a specific branch
     * Used by branch managers to request export from warehouse
     */
    public List<ProductDetail> getProductDetailsByBranch(int branchId) {
        System.out.println("=== [ProductDetailDAO] getProductDetailsByBranch called ===");
        System.out.println("BranchID: " + branchId);
        
        List<ProductDetail> list = new ArrayList<>();
        String sql = "SELECT DISTINCT pd.ProductDetailID, pd.ProductID, pd.Description, pd.ProductCode, " +
                     "pd.WarrantyPeriod, pd.ProductNameUnsigned, pd.CreatedAt, p.CostPrice, p.RetailPrice " +
                     "FROM ProductDetails pd " +
                     "JOIN Products p ON p.ProductID = pd.ProductID " +
                     "JOIN Inventory i ON pd.ProductDetailID = i.ProductDetailID " +
                     "WHERE i.BranchID = ? AND p.IsActive = 1 AND i.Quantity > 0 " +
                     "ORDER BY p.ProductName";
        
        System.out.println("SQL Query: " + sql);
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    ProductDetail pd = new ProductDetail();
                    pd.setProductDetailID(rs.getInt("ProductDetailID"));
                    pd.setProductID(rs.getInt("ProductID"));
                    pd.setDescription(rs.getString("Description"));
                    pd.setProductCode(rs.getString("ProductCode"));
                    pd.setWarrantyPeriod(rs.getString("WarrantyPeriod"));
                    pd.setProductNameUnsigned(rs.getString("ProductNameUnsigned"));
                    pd.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    pd.setCostPrice(rs.getDouble("CostPrice"));
                    pd.setRetailPrice(rs.getDouble("RetailPrice"));
                    list.add(pd);
                    count++;
                }
                System.out.println("✅ [ProductDetailDAO] Loaded " + count + " products for branch " + branchId);
            }
        } catch (SQLException e) {
            System.err.println("❌ [ProductDetailDAO] Error loading products for branch: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Search product details by branch and keyword
     */
    public List<ProductDetail> searchByBranchAndKeyword(int branchId, String keyword) {
        List<ProductDetail> list = new ArrayList<>();
        String sql = "SELECT DISTINCT pd.ProductDetailID, pd.ProductID, pd.Description, pd.ProductCode, " +
                     "pd.WarrantyPeriod, pd.ProductNameUnsigned, pd.CreatedAt, p.CostPrice, p.RetailPrice " +
                     "FROM ProductDetails pd " +
                     "JOIN Products p ON p.ProductID = pd.ProductID " +
                     "JOIN Inventory i ON pd.ProductDetailID = i.ProductDetailID " +
                     "WHERE i.BranchID = ? AND p.IsActive = 1 AND i.Quantity > 0 " +
                     "AND (p.ProductName LIKE ? OR pd.ProductCode LIKE ? OR pd.ProductNameUnsigned LIKE ?) " +
                     "ORDER BY p.ProductName";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            String keywordPattern = "%" + keyword + "%";
            ps.setString(2, keywordPattern);
            ps.setString(3, keywordPattern);
            ps.setString(4, keywordPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductDetail pd = new ProductDetail();
                    pd.setProductDetailID(rs.getInt("ProductDetailID"));
                    pd.setProductID(rs.getInt("ProductID"));
                    pd.setDescription(rs.getString("Description"));
                    pd.setProductCode(rs.getString("ProductCode"));
                    pd.setWarrantyPeriod(rs.getString("WarrantyPeriod"));
                    pd.setProductNameUnsigned(rs.getString("ProductNameUnsigned"));
                    pd.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    pd.setCostPrice(rs.getDouble("CostPrice"));
                    pd.setRetailPrice(rs.getDouble("RetailPrice"));
                    list.add(pd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Get products available in a warehouse with inventory quantity
     * Used for branch manager to create export requests
     */
    public List<ProductDetail> getProductsByWarehouse(int warehouseId, String keyword) {
        List<ProductDetail> list = new ArrayList<>();
        System.out.println("=== [ProductDetailDAO] getProductsByWarehouse ===");
        System.out.println("WarehouseID: " + warehouseId + ", Keyword: " + keyword);
        
        StringBuilder sql = new StringBuilder(
            "SELECT pd.ProductDetailID, pd.ProductID, pd.ProductCode, " +
            "pd.Description, pd.WarrantyPeriod, pd.ProductNameUnsigned, pd.CreatedAt, " +
            "p.ProductName, p.CostPrice, p.RetailPrice, p.ImageURL, s.SupplierName, " +
            "ISNULL(ip.Quantity, 0) as QuantityInStock " +
            "FROM ProductDetails pd " +
            "JOIN Products p ON p.ProductID = pd.ProductID " +
            "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
            "LEFT JOIN InventoryProducts ip ON pd.ProductDetailID = ip.ProductDetailID " +
            "LEFT JOIN Inventory inv ON ip.InventoryID = inv.InventoryID AND inv.WarehouseID = ? " +
            "WHERE p.IsActive = 1 "
        );
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.ProductName LIKE ? OR pd.ProductCode LIKE ? OR pd.ProductNameUnsigned LIKE ?) ");
        }
        
        sql.append("ORDER BY p.ProductName, pd.ProductCode");
        
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, warehouseId);
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                String keywordPattern = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, keywordPattern);
                ps.setString(paramIndex++, keywordPattern);
                ps.setString(paramIndex, keywordPattern);
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProductDetail pd = new ProductDetail();
                pd.setProductDetailID(rs.getInt("ProductDetailID"));
                pd.setProductID(rs.getInt("ProductID"));
                pd.setProductCode(rs.getString("ProductCode"));
                pd.setDescription(rs.getString("Description"));
                pd.setWarrantyPeriod(rs.getString("WarrantyPeriod"));
                pd.setProductNameUnsigned(rs.getString("ProductNameUnsigned"));
                pd.setCreatedAt(rs.getTimestamp("CreatedAt"));
                pd.setProductName(rs.getString("ProductName"));
                pd.setCostPrice(rs.getDouble("CostPrice"));
                pd.setRetailPrice(rs.getDouble("RetailPrice"));
                pd.setImageURL(rs.getString("ImageURL"));
                pd.setSupplierName(rs.getString("SupplierName"));
                pd.setQuantityInStock(rs.getInt("QuantityInStock"));
                list.add(pd);
            }
            
            System.out.println("Found " + list.size() + " products in warehouse " + warehouseId);
        } catch (SQLException e) {
            System.err.println("❌ [ProductDetailDAO] Error loading products from warehouse: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Get ALL product details (no supplier filter)
     * Used for branch manager export request
     */
    public List<ProductDetail> getAllProductDetails() {
        System.out.println("=== [ProductDetailDAO] getAllProductDetails called ===");
        
        List<ProductDetail> list = new ArrayList<>();
        String sql = "SELECT pd.ProductDetailID, pd.ProductID, pd.Description, pd.ProductCode, " +
                     "pd.WarrantyPeriod, pd.ProductNameUnsigned, pd.CreatedAt, p.CostPrice, p.RetailPrice, " +
                     "p.ProductName, p.ImageURL, s.SupplierName " +
                     "FROM ProductDetails pd " +
                     "JOIN Products p ON p.ProductID = pd.ProductID " +
                     "LEFT JOIN Suppliers s ON s.SupplierID = p.SupplierID " +
                     "WHERE p.IsActive = 1 " +
                     "ORDER BY p.ProductName, pd.ProductCode";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    ProductDetail pd = new ProductDetail();
                    pd.setProductDetailID(rs.getInt("ProductDetailID"));
                    pd.setProductID(rs.getInt("ProductID"));
                    pd.setDescription(rs.getString("Description"));
                    pd.setProductCode(rs.getString("ProductCode"));
                    pd.setWarrantyPeriod(rs.getString("WarrantyPeriod"));
                    pd.setProductNameUnsigned(rs.getString("ProductNameUnsigned"));
                    pd.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    pd.setCostPrice(rs.getDouble("CostPrice"));
                    pd.setRetailPrice(rs.getDouble("RetailPrice"));
                    pd.setProductName(rs.getString("ProductName"));
                    pd.setImageURL(rs.getString("ImageURL"));
                    pd.setSupplierName(rs.getString("SupplierName"));
                    list.add(pd);
                    count++;
                }
                System.out.println("✅ [ProductDetailDAO] Loaded " + count + " products (all)");
            }
        } catch (SQLException e) {
            System.err.println("❌ [ProductDetailDAO] Error loading all products: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Search ALL product details by keyword (no supplier filter)
     * Used for branch manager export request with search
     */
    public List<ProductDetail> searchProductDetails(String keyword) {
        System.out.println("=== [ProductDetailDAO] searchProductDetails called with keyword: " + keyword + " ===");
        
        List<ProductDetail> list = new ArrayList<>();
        String sql = "SELECT pd.ProductDetailID, pd.ProductID, pd.Description, pd.ProductCode, " +
                     "pd.WarrantyPeriod, pd.ProductNameUnsigned, pd.CreatedAt, p.CostPrice, p.RetailPrice, " +
                     "p.ProductName, p.ImageURL, s.SupplierName " +
                     "FROM ProductDetails pd " +
                     "JOIN Products p ON p.ProductID = pd.ProductID " +
                     "LEFT JOIN Suppliers s ON s.SupplierID = p.SupplierID " +
                     "WHERE p.IsActive = 1 " +
                     "AND (p.ProductName LIKE ? OR pd.ProductCode LIKE ? OR pd.ProductNameUnsigned LIKE ?) " +
                     "ORDER BY p.ProductName, pd.ProductCode";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String keywordPattern = "%" + keyword + "%";
            ps.setString(1, keywordPattern);
            ps.setString(2, keywordPattern);
            ps.setString(3, keywordPattern);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    ProductDetail pd = new ProductDetail();
                    pd.setProductDetailID(rs.getInt("ProductDetailID"));
                    pd.setProductID(rs.getInt("ProductID"));
                    pd.setDescription(rs.getString("Description"));
                    pd.setProductCode(rs.getString("ProductCode"));
                    pd.setWarrantyPeriod(rs.getString("WarrantyPeriod"));
                    pd.setProductNameUnsigned(rs.getString("ProductNameUnsigned"));
                    pd.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    pd.setCostPrice(rs.getDouble("CostPrice"));
                    pd.setRetailPrice(rs.getDouble("RetailPrice"));
                    pd.setProductName(rs.getString("ProductName"));
                    pd.setImageURL(rs.getString("ImageURL"));
                    pd.setSupplierName(rs.getString("SupplierName"));
                    list.add(pd);
                    count++;
                }
                System.out.println("✅ [ProductDetailDAO] Found " + count + " products matching keyword: " + keyword);
            }
        } catch (SQLException e) {
            System.err.println("❌ [ProductDetailDAO] Error searching products: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}

