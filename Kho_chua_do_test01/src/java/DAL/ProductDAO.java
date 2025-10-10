
package DAL;

import Model.Product;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductDAO — tương thích schema mới:
 *  - Bảng Products: (ProductID, ProductName, BrandID, CategoryID, SupplierID,
 *    CostPrice, RetailPrice, ImageURL, VAT, CreatedAt, IsActive)
 *  - Không còn Quantity/ExpiryDate/UpdatedAt trong Products.
 *  - Tồn kho nằm ở InventoryProducts/WarehouseProducts (tổng theo hệ thống hoặc theo chi nhánh/kho nếu cần).
 */
public class ProductDAO extends DataBaseContext {

    /* ===================== READ ===================== */

    // Lấy toàn bộ danh sách sản phẩm (chỉ thông tin bảng Products)
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
            // TODO: dùng logger của bạn thay vì printStackTrace
            e.printStackTrace();
        }
        return list;
    }

    // Lấy sản phẩm theo ID
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

    // Tìm kiếm theo tên
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

    // Lấy sản phẩm theo CategoryID
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

    // Thêm sản phẩm (bảng Products)
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

    // Cập nhật sản phẩm theo ID
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

    // Xóa sản phẩm theo ID — CHỈ xóa khi không còn ProductDetails tham chiếu
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
            // Gợi ý: parse e.getErrorCode() để trả message thân thiện cho client
            e.printStackTrace();
        }
        return false;
    }

    /* ===================== STOCK FILTER ===================== */
    /**
     * Tổng tồn kho hệ thống dựa trên SUM(InventoryProducts.Quantity)
     *
     * @return Danh sách Product (không kèm tồn kho). Giữ method cũ để backward-compatible.
     */
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

        sql.append(" GROUP BY p.ProductID, p.ProductName, p.BrandID, p.CategoryID, p.SupplierID, ")
           .append(" p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive ");

        switch (stock == null ? "" : stock) {
            case "in"       -> sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > 0 ");
            case "out"      -> sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) = 0 ");
            case "belowMin" -> { sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) < ? "); params.add(threshold); }
            case "aboveMax" -> { sql.append(" HAVING SUM(ISNULL(ip.Quantity,0)) > ? "); params.add(threshold); }
            default         -> { /* không filter tồn kho */ }
        }

        sql.append(" ORDER BY p.ProductID ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
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

    /**
     * Phiên bản trả về cả tổng tồn kho (DTO ProductWithStock) để dùng cho UI.
     */
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

        sql.append(" GROUP BY p.ProductID, p.ProductName, p.BrandID, p.CategoryID, p.SupplierID, ")
           .append(" p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT, p.CreatedAt, p.IsActive ");

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

        // DECIMAL -> BigDecimal (cho phép NULL)
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
