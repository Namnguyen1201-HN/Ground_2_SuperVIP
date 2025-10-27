package DAL;

import Model.Product;
import Model.ProductStatisticDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductDAO extends DataBaseContext {

    /* ===================== STOCK FILTER ENUM ===================== */
    public enum StockFilter {
        ALL, IN, OUT, BELOW_MIN, ABOVE_MAX;

        public static StockFilter from(String s) {
            if (s == null || s.isBlank()) return ALL;
            return switch (s.trim()) {
                case "in" -> IN;
                case "out" -> OUT;
                case "belowMin" -> BELOW_MIN;
                case "aboveMax" -> ABOVE_MAX;
                default -> ALL;
            };
        }
    }

    /* ===================== COMMON SQL PARTS ===================== */
    // CTE tính tổng tồn theo ProductID, tránh GROUP BY/HAVING ở ngoài
    private static final String CTE_QTY = """
        WITH qty AS (
            SELECT pd.ProductID, SUM(ISNULL(ip.Quantity,0)) AS TotalQty
            FROM ProductDetails pd
            LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
            GROUP BY pd.ProductID
        )
        """;

    // SELECT chung (đã JOIN brand/category/supplier + qty)
    private static final String SELECT_BASE_WITH_QTY = """
        SELECT
            p.ProductID, p.ProductName,
            p.BrandID,   b.BrandName,
            p.CategoryID, c.CategoryName,
            p.SupplierID, s.SupplierName,
            p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive,
            COALESCE(q.TotalQty, 0) AS TotalQty
        FROM Products p
        LEFT JOIN qty        q ON q.ProductID = p.ProductID
        LEFT JOIN Brands     b ON b.BrandID = p.BrandID
        LEFT JOIN Categories c ON c.CategoryID = p.CategoryID
        LEFT JOIN Suppliers  s ON s.SupplierID = p.SupplierID
        WHERE 1=1
        """;

    private static final String ORDER_BY_DEFAULT = " ORDER BY p.ProductID ";

    /* ===================== HELPERS ===================== */
    private static String escapeLike(String s) {
        if (s == null) return null;
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static void addAllParams(PreparedStatement ps, List<Object> params) throws SQLException {
        int idx = 1;
        for (Object o : params) ps.setObject(idx++, o);
    }

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("ProductID"));
        p.setProductName(rs.getString("ProductName"));

        int brandId = rs.getInt("BrandID");
        if (!rs.wasNull()) p.setBrandId(brandId);
        int catId = rs.getInt("CategoryID");
        if (!rs.wasNull()) p.setCategoryId(catId);
        int supId = rs.getInt("SupplierID");
        if (!rs.wasNull()) p.setSupplierId(supId);

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
        if (!rs.wasNull()) p.setIsActive(isActive);

        // TotalQty nếu có
        try {
            int q = rs.getInt("TotalQty");
            if (!rs.wasNull()) p.setTotalQty(q);
        } catch (SQLException ignore) { /* cột không tồn tại */ }

        return p;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer val) throws SQLException {
        if (val == null) ps.setNull(index, Types.INTEGER);
        else ps.setInt(index, val);
    }

    private void setNullableBigDecimal(PreparedStatement ps, int index, BigDecimal val) throws SQLException {
        if (val == null) ps.setNull(index, Types.DECIMAL);
        else ps.setBigDecimal(index, val);
    }

    private void setNullableBoolean(PreparedStatement ps, int index, Boolean val) throws SQLException {
        if (val == null) ps.setNull(index, Types.BIT);
        else ps.setBoolean(index, val);
    }

    /* ===================== CRUD ===================== */
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

    /* ===================== READ (COMPACT) ===================== */

    /** API chính: trả danh sách sản phẩm (kèm TotalQty) theo các bộ lọc tuỳ chọn */
    public List<Product> listProducts(List<String> categoryNames,
                                      String keyword,
                                      StockFilter stock,
                                      int threshold) {
        List<Product> out = new ArrayList<>();
        StringBuilder sql = new StringBuilder(CTE_QTY);
        sql.append(SELECT_BASE_WITH_QTY);

        List<Object> params = new ArrayList<>();

        // Danh mục theo tên
        if (categoryNames != null && !categoryNames.isEmpty()) {
            sql.append(" AND c.CategoryName IN (")
               .append(String.join(",", Collections.nCopies(categoryNames.size(), "?")))
               .append(") ");
            params.addAll(categoryNames);
        }

        // Lọc theo ProductName (escape %/_ + ESCAPE '\')
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.ProductName LIKE ? ESCAPE '\\' ");
            params.add("%" + escapeLike(keyword.trim()) + "%");
        }

        // Lọc theo tồn
        StockFilter sf = (stock == null) ? StockFilter.ALL : stock;
        switch (sf) {
            case IN -> sql.append(" AND COALESCE(q.TotalQty,0) > 0 ");
            case OUT -> sql.append(" AND COALESCE(q.TotalQty,0) = 0 ");
            case BELOW_MIN -> { sql.append(" AND COALESCE(q.TotalQty,0) < ? "); params.add(threshold); }
            case ABOVE_MAX -> { sql.append(" AND COALESCE(q.TotalQty,0) > ? "); params.add(threshold); }
            case ALL -> { /* no-op */ }
        }

        sql.append(ORDER_BY_DEFAULT);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            addAllParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /** Giữ API cũ cho tương thích */
    public List<Product> getAllProducts() {
        return listProducts(null, null, StockFilter.ALL, 0);
    }

    public List<Product> getAllProductsWithQty() {
        return listProducts(null, null, StockFilter.ALL, 0);
    }

    public List<Product> searchProductsByName(String keyword) {
        return listProducts(null, keyword, StockFilter.ALL, 0);
    }

    public List<Product> searchProductsByNameWithQty(String keyword) {
        return listProducts(null, keyword, StockFilter.ALL, 0);
    }

    public Product getProductById(int id) {
        // Dùng CTE và WHERE theo ProductID (kèm TotalQty)
        String sql = CTE_QTY + SELECT_BASE_WITH_QTY + " AND p.ProductID = ? ";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToProduct(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Product getProductByIdWithQty(int id) {
        return getProductById(id); // vì SELECT_BASE_WITH_QTY đã có TotalQty
    }

    /* ===================== NAME -> ID ===================== */
    private Integer resolveBrandId(String brandName) throws SQLException {
        if (brandName == null || brandName.isBlank()) return null;
        return resolveIdByName("Brands", "BrandID", "BrandName", brandName.trim());
    }

    private Integer resolveCategoryId(String categoryName) throws SQLException {
        if (categoryName == null || categoryName.isBlank()) return null;
        return resolveIdByName("Categories", "CategoryID", "CategoryName", categoryName.trim());
    }

    private Integer resolveSupplierId(String supplierName) throws SQLException {
        if (supplierName == null || supplierName.isBlank()) return null;
        return resolveIdByName("Suppliers", "SupplierID", "SupplierName", supplierName.trim());
    }

    private Integer resolveIdByName(String table, String idCol, String nameCol, String name) throws SQLException {
        String sql = "SELECT " + idCol + " FROM " + table + " WHERE " + nameCol + " = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
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

    public List<ProductStatisticDTO> getTopProducts(String sortBy, String period, int limit, Integer branchId) {
        List<ProductStatisticDTO> list = new ArrayList<>();

        String dateCondition = "";
        if ("this_month".equals(period)) {
            dateCondition = "WHERE MONTH(o.CreatedAt) = MONTH(GETDATE()) AND YEAR(o.CreatedAt) = YEAR(GETDATE())";
        } else if ("last_month".equals(period)) {
            dateCondition = "WHERE MONTH(o.CreatedAt) = MONTH(DATEADD(MONTH, -1, GETDATE())) "
                    + "AND YEAR(o.CreatedAt) = YEAR(DATEADD(MONTH, -1, GETDATE()))";
        }

        // Thêm filter chi nhánh
        if (branchId != null) {
            dateCondition += (dateCondition.isEmpty() ? "WHERE " : " AND ") + "o.BranchID = ?";
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
            int paramIndex = 1;
            ps.setInt(paramIndex++, limit);
            if (branchId != null) {
                ps.setInt(paramIndex++, branchId);
            }
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

    private Integer getOrCreateOneDetailForProduct(int productId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT TOP 1 ProductDetailID FROM ProductDetails WHERE ProductID = ? ORDER BY ProductDetailID")) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
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

    public boolean setQuantityForProductAtBranch(int productId, int branchId, int newQty) {
        try {
            Integer inventoryId = getOrCreateInventoryIdForBranch(branchId);
            Integer productDetailId = getOrCreateOneDetailForProduct(productId);
            if (inventoryId == null || productDetailId == null) return false;

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

    /* ===================== REPORT (giữ nguyên) ===================== */
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
                + (dateCondition.isEmpty() ? "" : dateCondition + "\n")
                + "GROUP BY p.ProductName \n"
                + "ORDER BY " + orderBy + ";";

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
}