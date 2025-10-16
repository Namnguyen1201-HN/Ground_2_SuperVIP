package DAL;

import Model.Product;
import Model.ProductStatisticDTO;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductDAO extends DataBaseContext {

    /* ===================== READ ===================== */
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = """
            SELECT  p.ProductID, p.ProductName,
                    p.BrandID,   b.BrandName,
                    p.CategoryID, c.CategoryName,
                    p.SupplierID, s.SupplierName,
                    p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive
            FROM Products p
            LEFT JOIN Brands b     ON b.BrandID = p.BrandID
            LEFT JOIN Categories c ON c.CategoryID = p.CategoryID
            LEFT JOIN Suppliers s  ON s.SupplierID = p.SupplierID
            ORDER BY p.ProductID
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    

    public Product getProductById(int id) {
        String sql = """
            SELECT  p.ProductID, p.ProductName,
                    p.BrandID,   b.BrandName,
                    p.CategoryID, c.CategoryName,
                    p.SupplierID, s.SupplierName,
                    p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive
            FROM Products p
            LEFT JOIN Brands b     ON b.BrandID = p.BrandID
            LEFT JOIN Categories c ON c.CategoryID = p.CategoryID
            LEFT JOIN Suppliers s  ON s.SupplierID = p.SupplierID
            WHERE p.ProductID = ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProduct(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> searchProductsByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = """
            SELECT  p.ProductID, p.ProductName,
                    p.BrandID,   b.BrandName,
                    p.CategoryID, c.CategoryName,
                    p.SupplierID, s.SupplierName,
                    p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive
            FROM Products p
            LEFT JOIN Brands b     ON b.BrandID = p.BrandID
            LEFT JOIN Categories c ON c.CategoryID = p.CategoryID
            LEFT JOIN Suppliers s  ON s.SupplierID = p.SupplierID
            WHERE p.ProductName LIKE ?
            ORDER BY p.ProductName
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + (keyword == null ? "" : keyword.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ===================== CREATE ===================== */
    /**
     * Thêm mới bằng TÊN (DAO tự resolve -> ID)
     */
    public boolean insertProduct(Product p) {
        String sql = """
            INSERT INTO Products
                (ProductName, BrandID, CategoryID, SupplierID,
                 CostPrice, RetailPrice, ImageURL, VAT, CreatedAt, IsActive)
            VALUES (?,?,?,?,?,?,?,?,GETDATE(),?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            setNullableInt(ps, 2, resolveBrandId(p.getBrandName()));
            setNullableInt(ps, 3, resolveCategoryId(p.getCategoryName()));
            setNullableInt(ps, 4, resolveSupplierId(p.getSupplierName()));
            setNullableBigDecimal(ps, 5, p.getCostPrice());
            setNullableBigDecimal(ps, 6, p.getRetailPrice());
            ps.setString(7, p.getImageUrl());
            setNullableBigDecimal(ps, 8, p.getVat());
            setNullableBoolean(ps, 9, p.getIsActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ===================== UPDATE ===================== */
    /**
     * Cập nhật bằng TÊN (DAO tự resolve -> ID)
     */
    public boolean updateProduct(Product p) {
        String sql = """
            UPDATE Products
            SET ProductName = ?,
                BrandID     = ?,
                CategoryID  = ?,
                SupplierID  = ?,
                CostPrice   = ?,
                RetailPrice = ?,
                ImageURL    = ?,
                VAT         = ?,
                IsActive    = ?
            WHERE ProductID = ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            setNullableInt(ps, 2, resolveBrandId(p.getBrandName()));
            setNullableInt(ps, 3, resolveCategoryId(p.getCategoryName()));
            setNullableInt(ps, 4, resolveSupplierId(p.getSupplierName()));
            setNullableBigDecimal(ps, 5, p.getCostPrice());
            setNullableBigDecimal(ps, 6, p.getRetailPrice());
            ps.setString(7, p.getImageUrl());
            setNullableBigDecimal(ps, 8, p.getVat());
            setNullableBoolean(ps, 9, p.getIsActive());
            ps.setInt(10, p.getProductId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ===================== DELETE ===================== */
    public boolean deleteProduct(int id) {
        String sql = """
            DELETE FROM Products
            WHERE ProductID = ?
              AND NOT EXISTS (SELECT 1 FROM ProductDetails WHERE ProductID = ?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ===================== STOCK FILTER (THEO TÊN) ===================== */
    /**
     * Lọc theo nhiều categoryName + ngưỡng tồn kho
     */
    public List<Product> findProductsWithThresholdByCategoryNames(List<String> categoryNames, String keyword, String stock, int threshold) {
        if (categoryNames != null && categoryNames.size() == 1) {
            return findProductsWithThresholdByCategoryName(categoryNames.get(0), keyword, stock, threshold);
        }

        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT  p.ProductID, p.ProductName,
                    p.BrandID,   b.BrandName,
                    p.CategoryID, c.CategoryName,
                    p.SupplierID, s.SupplierName,
                    p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive,
                    SUM(ISNULL(ip.Quantity,0)) AS TotalQty
            FROM Products p
            LEFT JOIN ProductDetails   pd ON pd.ProductID = p.ProductID
            LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
            LEFT JOIN Brands b     ON b.BrandID = p.BrandID
            LEFT JOIN Categories c ON c.CategoryID = p.CategoryID
            LEFT JOIN Suppliers s  ON s.SupplierID = p.SupplierID
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (categoryNames != null && !categoryNames.isEmpty()) {
            sql.append(" AND c.CategoryName IN (")
                    .append(String.join(",", Collections.nCopies(categoryNames.size(), "?")))
                    .append(") ");
            params.addAll(categoryNames);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.ProductName LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }

        sql.append("""
            GROUP BY p.ProductID, p.ProductName, p.BrandID, b.BrandName,
                     p.CategoryID, c.CategoryName, p.SupplierID, s.SupplierName,
                     p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive
        """);

        switch (stock == null ? "" : stock) {
            case "in" ->
                sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > 0 ");
            case "out" ->
                sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) = 0 ");
            case "belowMin" -> {
                sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) < ? ");
                params.add(threshold);
            }
            case "aboveMax" -> {
                sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > ? ");
                params.add(threshold);
            }
            default -> {
            }
        }

        sql.append(" ORDER BY p.ProductID ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object o : params) {
                if (o instanceof String s) {
                    ps.setString(idx++, s);
                } else if (o instanceof Integer i) {
                    ps.setInt(idx++, i);
                } else {
                    ps.setObject(idx++, o);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<Product> findProductsWithThresholdByCategoryName(String categoryName, String keyword, String stock, int threshold) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT  p.ProductID, p.ProductName,
                    p.BrandID,   b.BrandName,
                    p.CategoryID, c.CategoryName,
                    p.SupplierID, s.SupplierName,
                    p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive,
                    SUM(ISNULL(ip.Quantity,0)) AS TotalQty
            FROM Products p
            LEFT JOIN ProductDetails   pd ON pd.ProductID = p.ProductID
            LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
            LEFT JOIN Brands b     ON b.BrandID = p.BrandID
            LEFT JOIN Categories c ON c.CategoryID = p.CategoryID
            LEFT JOIN Suppliers s  ON s.SupplierID = p.SupplierID
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();
        if (categoryName != null && !categoryName.isBlank()) {
            sql.append(" AND c.CategoryName = ? ");
            params.add(categoryName.trim());
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.ProductName LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }

        sql.append("""
            GROUP BY p.ProductID, p.ProductName, p.BrandID, b.BrandName,
                     p.CategoryID, c.CategoryName, p.SupplierID, s.SupplierName,
                     p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive
        """);

        switch (stock == null ? "" : stock) {
            case "in" ->
                sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > 0 ");
            case "out" ->
                sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) = 0 ");
            case "belowMin" -> {
                sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) < ? ");
                params.add(threshold);
            }
            case "aboveMax" -> {
                sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > ? ");
                params.add(threshold);
            }
            default -> {
            }
        }
        sql.append(" ORDER BY p.ProductID ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ===================== MAPPERS & HELPERS ===================== */
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("ProductID"));
        p.setProductName(rs.getString("ProductName"));

        // ID (để tương thích ngược — KHÔNG dùng ở Controller/View)
        int brandId = rs.getInt("BrandID");
        if (!rs.wasNull()) {
            p.setBrandId(brandId);
        }
        int catId = rs.getInt("CategoryID");
        if (!rs.wasNull()) {
            p.setCategoryId(catId);
        }
        int supId = rs.getInt("SupplierID");
        if (!rs.wasNull()) {
            p.setSupplierId(supId);
        }

        // TÊN — dùng ở Controller/View
        p.setBrandName(rs.getString("BrandName"));
        p.setCategoryName(rs.getString("CategoryName"));
        p.setSupplierName(rs.getString("SupplierName"));

        p.setCostPrice(rs.getBigDecimal("CostPrice"));
        p.setRetailPrice(rs.getBigDecimal("RetailPrice"));
        p.setImageUrl(rs.getString("ImageURL"));
        p.setVat(rs.getBigDecimal("VAT"));

        Timestamp created = rs.getTimestamp("CreatedAt");
        p.setCreatedAt(created == null ? null : created.toLocalDateTime());

        boolean isActive = rs.getBoolean("IsActive");
        if (!rs.wasNull()) {
            p.setIsActive(isActive);
        }

        return p;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer val) throws SQLException {
        if (val == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, val);
        }
    }

    private void setNullableBigDecimal(PreparedStatement ps, int index, BigDecimal val) throws SQLException {
        if (val == null) {
            ps.setNull(index, Types.DECIMAL);
        } else {
            ps.setBigDecimal(index, val);
        }
    }

    private void setNullableBoolean(PreparedStatement ps, int index, Boolean val) throws SQLException {
        if (val == null) {
            ps.setNull(index, Types.BIT);
        } else {
            ps.setBoolean(index, val);
        }
    }

    /* ========== Resolve Name -> ID (Brands/Categories/Suppliers) ========== */
    private Integer resolveBrandId(String brandName) throws SQLException {
        if (brandName == null || brandName.isBlank()) {
            return null;
        }
        return resolveIdByName("Brands", "BrandID", "BrandName", brandName.trim());
    }

    private Integer resolveCategoryId(String categoryName) throws SQLException {
        if (categoryName == null || categoryName.isBlank()) {
            return null;
        }
        return resolveIdByName("Categories", "CategoryID", "CategoryName", categoryName.trim());
    }

    private Integer resolveSupplierId(String supplierName) throws SQLException {
        if (supplierName == null || supplierName.isBlank()) {
            return null;
        }
        return resolveIdByName("Suppliers", "SupplierID", "SupplierName", supplierName.trim());
    }

    private Integer resolveIdByName(String table, String idCol, String nameCol, String name) throws SQLException {
        String sql = "SELECT " + idCol + " FROM " + table + " WHERE " + nameCol + " = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        // Không tìm thấy -> trả null (tuỳ bạn muốn throw lỗi hay cho phép null)
        return null;
    }

    /* ===================== (TÙY CHỌN) DTO STOCK ===================== */
    public static class ProductWithStock {

        private final Product product;
        private final Integer totalQty;

        public ProductWithStock(Product product, Integer totalQty) {
            this.product = product;
            this.totalQty = totalQty;
        }

        public Product getProduct() {
            return product;
        }

        public Integer getTotalQty() {
            return totalQty;
        }
    }

    public List<ProductStatisticDTO> getTopProducts(String sortBy, String period, int limit) {
        List<ProductStatisticDTO> list = new ArrayList<>();

        String dateCondition = "";
        if ("this_month".equals(period)) {
            dateCondition = "WHERE MONTH(o.CreatedAt) = MONTH(GETDATE()) AND YEAR(o.CreatedAt) = YEAR(GETDATE())";
        } else if ("last_month".equals(period)) {
            dateCondition = "WHERE MONTH(o.CreatedAt) = MONTH(DATEADD(MONTH, -1, GETDATE())) "
                    + "AND YEAR(o.CreatedAt) = YEAR(DATEADD(MONTH, -1, GETDATE()))";
        }

        String orderBy = "revenue".equals(sortBy)
                ? "SUM(od.Quantity * p.RetailPrice) DESC"
                : "SUM(od.Quantity) DESC";

        String sql = ""
                + "SELECT TOP (?) \n"
                + "    p.ProductName, \n"
                + "    SUM(od.Quantity) AS TotalQuantity, \n"
                + "    SUM(od.Quantity * p.RetailPrice) AS Revenue \n"
                + "FROM Orders o \n"
                + "JOIN OrderDetails od ON o.OrderID = od.OrderID \n"
                + "JOIN ProductDetails pd ON od.ProductDetailID = pd.ProductDetailID \n"
                + "JOIN Products p ON pd.ProductID = p.ProductID \n"
                + (dateCondition.isEmpty() ? "" : dateCondition + "\n") // đảm bảo có dòng riêng
                + "GROUP BY p.ProductName \n"
                + "ORDER BY " + orderBy + ";";

        // In SQL ra để debug
        System.out.println("=== DEBUG SQL ===\n" + sql + "\n=================");

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProductStatisticDTO dto = new ProductStatisticDTO();
                dto.setProductName(rs.getString("ProductName"));
                dto.setTotalQuantity(rs.getInt("TotalQuantity"));
                dto.setRevenue(rs.getBigDecimal("Revenue"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    // TOTAL = Inventory + Warehouse (không lọc phạm vi)
// Tổng tồn theo branch (dùng InventoryProducts)
String SQL_BASE_BRANCH = """
    SELECT
        p.ProductID, p.ProductName,
        p.BrandID,   b.BrandName,
        p.CategoryID, c.CategoryName,
        p.SupplierID, s.SupplierName,
        p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive,
        COALESCE(SUM(ip.Quantity),0) AS TotalQty
    FROM Products p
    LEFT JOIN ProductDetails pd    ON pd.ProductID = p.ProductID
    LEFT JOIN Inventory i          ON i.BranchID = ?
    LEFT JOIN InventoryProducts ip ON ip.InventoryID = i.InventoryID AND ip.ProductDetailID = pd.ProductDetailID
    LEFT JOIN Brands b             ON b.BrandID = p.BrandID
    LEFT JOIN Categories c         ON c.CategoryID = p.CategoryID
    LEFT JOIN Suppliers s          ON s.SupplierID = p.SupplierID
""";
// nhớ bind branchId ở vị trí 1

/** Lấy (hoặc tạo) InventoryID cho Branch */
private Integer getOrCreateInventoryIdForBranch(int branchId) throws SQLException {
    // 1) tìm inventory
    try (PreparedStatement ps = connection.prepareStatement(
            "SELECT InventoryID FROM Inventory WHERE BranchID = ?")) {
        ps.setInt(1, branchId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
    }
    // 2) tạo inventory
    try (PreparedStatement ps = connection.prepareStatement(
            "INSERT INTO Inventory(BranchID) VALUES (?)",
            Statement.RETURN_GENERATED_KEYS)) {
        ps.setInt(1, branchId);
        ps.executeUpdate();
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) return rs.getInt(1);
        }
    }
    return null;
}

/** Lấy (hoặc tạo) ProductDetailID tối thiểu để quản lý tồn */
private Integer getOrCreateOneDetailForProduct(int productId) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(
            "SELECT TOP 1 ProductDetailID FROM ProductDetails WHERE ProductID = ? ORDER BY ProductDetailID")) {
        ps.setInt(1, productId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
    }
    // tạo một detail mặc định
    String ins = """
        INSERT INTO ProductDetails(ProductID, Description, ProductCode, WarrantyPeriod, ProductNameUnsigned)
        VALUES (?, NULL, NULL, NULL, NULL)
    """;
    try (PreparedStatement ps = connection.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
        ps.setInt(1, productId);
        ps.executeUpdate();
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) return rs.getInt(1);
        }
    }
    return null;
}

/** Upsert quantity theo BranchID cho 1 Product (đặt cứng vào 1 ProductDetail) */
public boolean setQuantityForProductAtBranch(int productId, int branchId, int newQty) {
    try {
        Integer inventoryId = getOrCreateInventoryIdForBranch(branchId);
        Integer productDetailId = getOrCreateOneDetailForProduct(productId);
        if (inventoryId == null || productDetailId == null) return false;

        // upsert InventoryProducts
        // nếu có bản ghi -> UPDATE, nếu chưa có -> INSERT
        int updated;
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE InventoryProducts SET Quantity = ? WHERE InventoryID = ? AND ProductDetailID = ?")) {
            ps.setInt(1, newQty);
            ps.setInt(2, inventoryId);
            ps.setInt(3, productDetailId);
            updated = ps.executeUpdate();
        }
        if (updated == 0) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO InventoryProducts(InventoryID, ProductDetailID, Quantity) VALUES (?,?,?)")) {
                ps.setInt(1, inventoryId);
                ps.setInt(2, productDetailId);
                ps.setInt(3, newQty);
                ps.executeUpdate();
            }
        }
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

/* =========================================================
   CÁC HÀM MỚI: LẤY SẢN PHẨM KÈM TOTAL QTY (INVENTORY)
   ========================================================= */

/** Lấy tất cả sản phẩm kèm tổng tồn kho (InventoryProducts) */
public List<Product> getAllProductsWithQty() {
    List<Product> list = new ArrayList<>();
    String sql = """
        SELECT 
            p.ProductID, p.ProductName,
            p.BrandID, b.BrandName,
            p.CategoryID, c.CategoryName,
            p.SupplierID, s.SupplierName,
            p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive,
            COALESCE(SUM(ip.Quantity), 0) AS TotalQty
        FROM Products p
        LEFT JOIN ProductDetails pd ON pd.ProductID = p.ProductID
        LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
        LEFT JOIN Brands b ON b.BrandID = p.BrandID
        LEFT JOIN Categories c ON c.CategoryID = p.CategoryID
        LEFT JOIN Suppliers s ON s.SupplierID = p.SupplierID
        GROUP BY 
            p.ProductID, p.ProductName,
            p.BrandID, b.BrandName,
            p.CategoryID, c.CategoryName,
            p.SupplierID, s.SupplierName,
            p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive
        ORDER BY p.ProductID
    """;
    try (PreparedStatement ps = connection.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(mapRowToProductWithQty(rs));
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}

/** Lấy 1 sản phẩm theo ID kèm tổng tồn kho */
public Product getProductByIdWithQty(int id) {
    String sql = """
        SELECT 
            p.ProductID, p.ProductName,
            p.BrandID, b.BrandName,
            p.CategoryID, c.CategoryName,
            p.SupplierID, s.SupplierName,
            p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive,
            COALESCE(SUM(ip.Quantity), 0) AS TotalQty
        FROM Products p
        LEFT JOIN ProductDetails pd ON pd.ProductID = p.ProductID
        LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
        LEFT JOIN Brands b ON b.BrandID = p.BrandID
        LEFT JOIN Categories c ON c.CategoryID = p.CategoryID
        LEFT JOIN Suppliers s ON s.SupplierID = p.SupplierID
        WHERE p.ProductID = ?
        GROUP BY 
            p.ProductID, p.ProductName,
            p.BrandID, b.BrandName,
            p.CategoryID, c.CategoryName,
            p.SupplierID, s.SupplierName,
            p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive
    """;
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return mapRowToProductWithQty(rs);
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return null;
}

/** Tìm theo tên kèm tổng tồn kho */
public List<Product> searchProductsByNameWithQty(String keyword) {
    List<Product> list = new ArrayList<>();
    String sql = """
        SELECT 
            p.ProductID, p.ProductName,
            p.BrandID, b.BrandName,
            p.CategoryID, c.CategoryName,
            p.SupplierID, s.SupplierName,
            p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive,
            COALESCE(SUM(ip.Quantity), 0) AS TotalQty
        FROM Products p
        LEFT JOIN ProductDetails pd ON pd.ProductID = p.ProductID
        LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
        LEFT JOIN Brands b ON b.BrandID = p.BrandID
        LEFT JOIN Categories c ON c.CategoryID = p.CategoryID
        LEFT JOIN Suppliers s ON s.SupplierID = p.SupplierID
        WHERE p.ProductName LIKE ?
        GROUP BY 
            p.ProductID, p.ProductName,
            p.BrandID, b.BrandName,
            p.CategoryID, c.CategoryName,
            p.SupplierID, s.SupplierName,
            p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive
        ORDER BY p.ProductName
    """;
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, "%" + (keyword == null ? "" : keyword.trim()) + "%");
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRowToProductWithQty(rs));
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}

/** Lấy tổng tồn kho nhanh cho 1 sản phẩm (nếu chỉ cần con số) */
public Integer getTotalQtyForProduct(int productId) {
    String sql = """
        SELECT COALESCE(SUM(ip.Quantity), 0) AS TotalQty
        FROM ProductDetails pd
        LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
        WHERE pd.ProductID = ?
    """;
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, productId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int v = rs.getInt("TotalQty");
                return rs.wasNull() ? null : v;
            }
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return null;
}

/** Map có hỗ trợ đọc cột TotalQty nếu có mặt trong ResultSet */
private Product mapRowToProductWithQty(ResultSet rs) throws SQLException {
    Product p = mapRowToProduct(rs); // dùng mapper cũ để set các trường chuẩn
    // Nếu câu SQL SELECT có cột TotalQty thì set, nếu không có cột này -> bỏ qua
    try {
        int q = rs.getInt("TotalQty");
        if (!rs.wasNull()) p.setTotalQty(q);
        else p.setTotalQty(null);
    } catch (SQLException ignore) {
        // cột không tồn tại trong ResultSet → không set
    }
    return p;
}



}
