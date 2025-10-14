package DAL;

import Model.Product;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ProductDAO — tương thích schema mới:
 *  - Bảng Products: (ProductID, ProductName, BrandID, CategoryID, SupplierID,
 *    CostPrice, RetailPrice, ImageURL, VAT, CreatedAt, IsActive)
 *  - Tồn kho tổng nằm ở InventoryProducts/ProductDetails.
 */
public class ProductDAO extends DataBaseContext {

    /* ===================== READ ===================== */

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = """
            SELECT ProductID, ProductName, BrandID, CategoryID, SupplierID,
                   CostPrice, RetailPrice, ImageURL, VAT, CreatedAt, IsActive
            FROM Products
            ORDER BY ProductID
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
            SELECT ProductID, ProductName, BrandID, CategoryID, SupplierID,
                   CostPrice, RetailPrice, ImageURL, VAT, CreatedAt, IsActive
            FROM Products WHERE ProductID = ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> searchProductsByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = """
            SELECT ProductID, ProductName, BrandID, CategoryID, SupplierID,
                   CostPrice, RetailPrice, ImageURL, VAT, CreatedAt, IsActive
            FROM Products
            WHERE ProductName LIKE ?
            ORDER BY ProductName
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + (keyword == null ? "" : keyword.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> list = new ArrayList<>();
        String sql = """
            SELECT ProductID, ProductName, BrandID, CategoryID, SupplierID,
                   CostPrice, RetailPrice, ImageURL, VAT, CreatedAt, IsActive
            FROM Products
            WHERE CategoryID = ?
            ORDER BY ProductID
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ===================== CREATE ===================== */

    public boolean insertProduct(Product p) {
        String sql = """
            INSERT INTO Products
                (ProductName, BrandID, CategoryID, SupplierID,
                 CostPrice, RetailPrice, ImageURL, VAT, CreatedAt, IsActive)
            VALUES (?,?,?,?,?,?,?,?,GETDATE(),?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            setNullableInt(ps, 2, p.getBrandId());
            setNullableInt(ps, 3, p.getCategoryId());
            setNullableInt(ps, 4, p.getSupplierId());
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
            setNullableInt(ps, 2, p.getBrandId());
            setNullableInt(ps, 3, p.getCategoryId());
            setNullableInt(ps, 4, p.getSupplierId());
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

    /* ===================== STOCK FILTER ===================== */
    /** BẢN CŨ: 1 categoryId (giữ để tương thích) */
    public List<Product> findProductsWithThreshold(Integer categoryId, String keyword, String stock, int threshold) {
        List<Product> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT p.ProductID, p.ProductName, p.BrandID, p.CategoryID, p.SupplierID,
                   p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive,
                   SUM(ISNULL(ip.Quantity,0)) AS TotalQty
            FROM Products p
            LEFT JOIN ProductDetails pd ON pd.ProductID = p.ProductID
            LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
            WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();

        if (categoryId != null) {
            sql.append(" AND p.CategoryID = ?");
            params.add(categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.ProductName LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }

        sql.append("""
            GROUP BY p.ProductID, p.ProductName, p.BrandID, p.CategoryID, p.SupplierID,
                     p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive
        """);

        switch (stock == null ? "" : stock) {
            case "in"       -> sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > 0 ");
            case "out"      -> sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) = 0 ");
            case "belowMin" -> { sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) < ? "); params.add(threshold); }
            case "aboveMax" -> { sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > ? "); params.add(threshold); }
            default         -> { }
        }

        sql.append(" ORDER BY p.ProductID ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** BẢN MỚI: nhiều categoryIds (để controller gọi) */
    public List<Product> findProductsWithThreshold(List<Integer> categoryIds, String keyword, String stock, int threshold) {
    // 0) fallback sang bản cũ để chắc chắn đường 1-id vẫn hoạt động
    if (categoryIds == null || categoryIds.isEmpty()) {
        return findProductsWithThreshold((Integer) null, keyword, stock, threshold);
    }
    if (categoryIds.size() == 1) {
        return findProductsWithThreshold(categoryIds.get(0), keyword, stock, threshold);
    }

    List<Product> list = new ArrayList<>();

    // 1) SQL base
    StringBuilder sql = new StringBuilder("""
        SELECT p.ProductID, p.ProductName, p.BrandID, p.CategoryID, p.SupplierID,
               p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive,
               SUM(ISNULL(ip.Quantity,0)) AS TotalQty
        FROM Products p
        LEFT JOIN ProductDetails pd ON pd.ProductID = p.ProductID
        LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
        WHERE 1=1
    """);

    // 2) IN (?, ?, ?)
    sql.append(" AND p.CategoryID IN (")
       .append(String.join(",", java.util.Collections.nCopies(categoryIds.size(), "?")))
       .append(") ");

    boolean hasKeyword = keyword != null && !keyword.isBlank();
    if (hasKeyword) {
        sql.append(" AND p.ProductName LIKE ? ");
    }

    sql.append("""
        GROUP BY p.ProductID, p.ProductName, p.BrandID, p.CategoryID, p.SupplierID,
                 p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive
    """);

    boolean hasThreshold = false;
    String effStock = (stock == null ? "" : stock);
    switch (effStock) {
        case "in"  -> sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > 0 ");
        case "out" -> sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) = 0 ");
        case "belowMin" -> { sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) < ? "); hasThreshold = true; }
        case "aboveMax" -> { sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > ? "); hasThreshold = true; }
        default -> { /* no having */ }
    }

    sql.append(" ORDER BY p.ProductID ");

    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
        int idx = 1;

        // 3) bind IN params (từng categoryId)
        for (Integer catId : categoryIds) {
            ps.setInt(idx++, catId);
        }

        // 4) bind keyword (nếu có)
        if (hasKeyword) {
            ps.setString(idx++, "%" + keyword.trim() + "%");
        }

        // 5) bind threshold (nếu có HAVING dùng threshold)
        if (hasThreshold) {
            ps.setInt(idx++, threshold);
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToProduct(rs));
            }
        }
    } catch (SQLException e) {
        // Gợi ý log thêm để dễ soi
        e.printStackTrace();
        // Bạn có thể tạm throw để thấy stacktrace trên trình duyệt:
        // throw new RuntimeException(e);
    }
    return list;
}


    /** BẢN CŨ + STOCK: trả ProductWithStock cho UI */
    public List<ProductWithStock> findProductsWithThresholdWithStock(Integer categoryId, String keyword, String stock, int threshold) {
        List<ProductWithStock> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT p.ProductID, p.ProductName, p.BrandID, p.CategoryID, p.SupplierID,
                   p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive,
                   SUM(ISNULL(ip.Quantity,0)) AS TotalQty
            FROM Products p
            LEFT JOIN ProductDetails pd ON pd.ProductID = p.ProductID
            LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
            WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();

        if (categoryId != null) {
            sql.append(" AND p.CategoryID = ?");
            params.add(categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.ProductName LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }

        sql.append("""
            GROUP BY p.ProductID, p.ProductName, p.BrandID, p.CategoryID, p.SupplierID,
                     p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive
        """);

        switch (stock == null ? "" : stock) {
            case "in"       -> sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > 0 ");
            case "out"      -> sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) = 0 ");
            case "belowMin" -> { sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) < ? "); params.add(threshold); }
            case "aboveMax" -> { sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > ? "); params.add(threshold); }
            default         -> { }
        }

        sql.append(" ORDER BY p.ProductID ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product prod = mapRowToProduct(rs);
                    Integer totalQty = rs.getObject("TotalQty") == null ? null : rs.getInt("TotalQty");
                    if (rs.wasNull()) totalQty = null;
                    list.add(new ProductWithStock(prod, totalQty));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** BẢN MỚI + STOCK: nhiều categoryIds */
    public List<ProductWithStock> findProductsWithThresholdWithStock(List<Integer> categoryIds, String keyword, String stock, int threshold) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return findProductsWithThresholdWithStock((Integer) null, keyword, stock, threshold);
        }
        if (categoryIds.size() == 1) {
            return findProductsWithThresholdWithStock(categoryIds.get(0), keyword, stock, threshold);
        }

        List<ProductWithStock> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT p.ProductID, p.ProductName, p.BrandID, p.CategoryID, p.SupplierID,
                   p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive,
                   SUM(ISNULL(ip.Quantity,0)) AS TotalQty
            FROM Products p
            LEFT JOIN ProductDetails pd ON pd.ProductID = p.ProductID
            LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
            WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();

        sql.append(" AND p.CategoryID IN (")
           .append(String.join(",", Collections.nCopies(categoryIds.size(), "?")))
           .append(") ");
        params.addAll(categoryIds);

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.ProductName LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }

        sql.append("""
            GROUP BY p.ProductID, p.ProductName, p.BrandID, p.CategoryID, p.SupplierID,
                     p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive
        """);

        switch (stock == null ? "" : stock) {
            case "in"       -> sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > 0 ");
            case "out"      -> sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) = 0 ");
            case "belowMin" -> { sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) < ? "); params.add(threshold); }
            case "aboveMax" -> { sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > ? "); params.add(threshold); }
            default         -> { }
        }

        sql.append(" ORDER BY p.ProductID ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                if (p instanceof Integer i) ps.setInt(idx++, i);
                else ps.setObject(idx++, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product prod = mapRowToProduct(rs);
                    Integer totalQty = rs.getObject("TotalQty") == null ? null : rs.getInt("TotalQty");
                    if (rs.wasNull()) totalQty = null;
                    list.add(new ProductWithStock(prod, totalQty));
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

        int brandId = rs.getInt("BrandID");
        if (!rs.wasNull()) p.setBrandId(brandId);

        int catId = rs.getInt("CategoryID");
        if (!rs.wasNull()) p.setCategoryId(catId);

        int supId = rs.getInt("SupplierID");
        if (!rs.wasNull()) p.setSupplierId(supId);

        BigDecimal cost = rs.getBigDecimal("CostPrice");
        p.setCostPrice(cost);
        BigDecimal retail = rs.getBigDecimal("RetailPrice");
        p.setRetailPrice(retail);
        p.setImageUrl(rs.getString("ImageURL"));
        p.setVat(rs.getBigDecimal("VAT"));

        Timestamp created = rs.getTimestamp("CreatedAt");
        if (created != null) {
            p.setCreatedAt(created.toLocalDateTime());
        } else {
            p.setCreatedAt((LocalDateTime) null);
        }

        boolean isActive = rs.getBoolean("IsActive");
        if (!rs.wasNull()) p.setIsActive(isActive);

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

    /* ===================== DTO ===================== */
    public static class ProductWithStock {
        private final Product product;
        private final Integer totalQty; // có thể null

        public ProductWithStock(Product product, Integer totalQty) {
            this.product = product;
            this.totalQty = totalQty;
        }
        public Product getProduct() { return product; }
        public Integer getTotalQty() { return totalQty; }
    }
}
