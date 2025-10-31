package DAL;

import Model.Product;
import Model.ProductDTO;
import Model.ProductStatisticDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
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
                case "in"       -> IN;
                case "out"      -> OUT;
                case "belowMin" -> BELOW_MIN;
                case "aboveMax" -> ABOVE_MAX;
                default         -> ALL;
            };
        }
    }

    /* ===================== COMMON SQL PARTS ===================== */
    // Tổng tồn TOÀN HỆ THỐNG theo ProductID
    private static final String CTE_QTY = """
        WITH qty AS (
            SELECT pd.ProductID, SUM(ISNULL(ip.Quantity,0)) AS TotalQty
            FROM ProductDetails pd
            LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
            GROUP BY pd.ProductID
        )
        """;

    // Tổng tồn THEO CHI NHÁNH (LEFT JOIN để thấy sản phẩm không có dòng tồn -> 0)
    private static final String CTE_QTY_BY_BRANCH = """
        WITH qty AS (
            SELECT pd.ProductID, SUM(ISNULL(ip.Quantity,0)) AS TotalQty
            FROM ProductDetails pd
            LEFT JOIN InventoryProducts ip ON ip.ProductDetailID = pd.ProductDetailID
            LEFT JOIN Inventory inv        ON inv.InventoryID    = ip.InventoryID
                                          AND inv.BranchID       = ?
            GROUP BY pd.ProductID
        )
        """;

    // SELECT chung (đã JOIN brand/category/supplier + qty)
    private static final String SELECT_BASE_WITH_QTY = """
        SELECT
            p.ProductID, p.ProductName,
            p.BrandID,   b.BrandID AS BId,  b.BrandName,
            p.CategoryID, c.CategoryID AS CId, c.CategoryName,
            p.SupplierID, s.SupplierID AS SId, s.SupplierName,
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

        try {
            int q = rs.getInt("TotalQty");
            if (!rs.wasNull()) p.setTotalQty(q);
        } catch (SQLException ignore) { }

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
        } catch (SQLException e) { e.printStackTrace(); }
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
        } catch (SQLException e) { e.printStackTrace(); }
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
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /* ===================== READ (COMPACT) ===================== */
    // (Giữ để tương thích khi cần list không phân trang)
    public List<Product> listProducts(List<String> categoryNames, String keyword, StockFilter stock, int threshold) {
        List<Product> out = new ArrayList<>();
        StringBuilder sql = new StringBuilder(CTE_QTY);
        sql.append(SELECT_BASE_WITH_QTY);

        List<Object> params = new ArrayList<>();

        if (categoryNames != null && !categoryNames.isEmpty()) {
            sql.append(" AND c.CategoryName IN (")
               .append(String.join(",", Collections.nCopies(categoryNames.size(), "?")))
               .append(") ");
            params.addAll(categoryNames);
        }

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.ProductName LIKE ? ESCAPE '\\' ");
            params.add("%" + escapeLike(keyword.trim()) + "%");
        }

        StockFilter sf = (stock == null) ? StockFilter.ALL : stock;
        switch (sf) {
            case IN       -> sql.append(" AND COALESCE(q.TotalQty,0) > 0 ");
            case OUT      -> sql.append(" AND COALESCE(q.TotalQty,0) = 0 ");
            case BELOW_MIN -> { sql.append(" AND COALESCE(q.TotalQty,0) < ? "); params.add(threshold); }
            case ABOVE_MAX -> { sql.append(" AND COALESCE(q.TotalQty,0) > ? "); params.add(threshold); }
            case ALL -> {}
        }

        sql.append(ORDER_BY_DEFAULT);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            addAllParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    /* ===================== PAGINATION for /admin/product.jsp ===================== */
    public int countProducts(
            List<String> categoryNames,
            String keyword,
            StockFilter stock,
            int threshold
    ) {
        StringBuilder sql = new StringBuilder(CTE_QTY).append("""
            SELECT COUNT(*) AS Cnt
            FROM Products p
            LEFT JOIN qty        q ON q.ProductID = p.ProductID
            LEFT JOIN Brands     b ON b.BrandID    = p.BrandID
            LEFT JOIN Categories c ON c.CategoryID = p.CategoryID
            LEFT JOIN Suppliers  s ON s.SupplierID = p.SupplierID
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
            sql.append(" AND p.ProductName LIKE ? ESCAPE '\\' ");
            params.add("%" + escapeLike(keyword.trim()) + "%");
        }

        StockFilter sf = (stock == null) ? StockFilter.ALL : stock;
        switch (sf) {
            case IN       -> sql.append(" AND COALESCE(q.TotalQty,0) > 0 ");
            case OUT      -> sql.append(" AND COALESCE(q.TotalQty,0) = 0 ");
            case BELOW_MIN -> { sql.append(" AND COALESCE(q.TotalQty,0) < ? "); params.add(threshold); }
            case ABOVE_MAX -> { sql.append(" AND COALESCE(q.TotalQty,0) > ? "); params.add(threshold); }
            case ALL -> {}
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            addAllParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("Cnt");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<Product> listProductsByPage(
            List<String> categoryNames,
            String keyword,
            StockFilter stock,
            int threshold,
            int page,    // 1-based
            int pageSize
    ) {
        List<Product> out = new ArrayList<>();
        StringBuilder sql = new StringBuilder(CTE_QTY).append(SELECT_BASE_WITH_QTY);
        List<Object> params = new ArrayList<>();

        if (categoryNames != null && !categoryNames.isEmpty()) {
            sql.append(" AND c.CategoryName IN (")
               .append(String.join(",", Collections.nCopies(categoryNames.size(), "?")))
               .append(") ");
            params.addAll(categoryNames);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.ProductName LIKE ? ESCAPE '\\' ");
            params.add("%" + escapeLike(keyword.trim()) + "%");
        }

        StockFilter sf = (stock == null) ? StockFilter.ALL : stock;
        switch (sf) {
            case IN       -> sql.append(" AND COALESCE(q.TotalQty,0) > 0 ");
            case OUT      -> sql.append(" AND COALESCE(q.TotalQty,0) = 0 ");
            case BELOW_MIN -> { sql.append(" AND COALESCE(q.TotalQty,0) < ? "); params.add(threshold); }
            case ABOVE_MAX -> { sql.append(" AND COALESCE(q.TotalQty,0) > ? "); params.add(threshold); }
            case ALL -> {}
        }

        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, pageSize);
        int offset   = (safePage - 1) * safeSize;

        sql.append(ORDER_BY_DEFAULT).append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        params.add(offset);
        params.add(safeSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            addAllParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    /* ===================== INVENTORY (giữ nguyên, chỉnh CTE) ===================== */
    public int countProductsByBranchId(int branchId) {
        // Nếu list theo ProductDetail, hãy đếm theo ProductDetail; nếu list theo Product, đổi COUNT DISTINCT ProductID.
        String sql = """
            SELECT COUNT(*) AS Cnt
            FROM Inventory i
            LEFT JOIN InventoryProducts ip ON ip.InventoryID = i.InventoryID
            LEFT JOIN ProductDetails pd    ON pd.ProductDetailID = ip.ProductDetailID
            WHERE i.BranchID = ? AND ip.ProductDetailID IS NOT NULL
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("Cnt");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int countProductsByBranchForPaging(
            int branchId,
            List<String> categoryNames,
            String keyword,
            StockFilter stock,
            int threshold
    ) {
        StringBuilder sql = new StringBuilder(CTE_QTY_BY_BRANCH).append("""
            SELECT COUNT(*) AS Cnt
            FROM Products p
            LEFT JOIN qty        q ON q.ProductID = p.ProductID
            LEFT JOIN Brands     b ON b.BrandID = p.BrandID
            LEFT JOIN Categories c ON c.CategoryID = p.CategoryID
            LEFT JOIN Suppliers  s ON s.SupplierID = p.SupplierID
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();
        params.add(branchId); // tham số cho CTE

        if (categoryNames != null && !categoryNames.isEmpty()) {
            sql.append(" AND c.CategoryName IN (")
               .append(String.join(",", Collections.nCopies(categoryNames.size(), "?")))
               .append(") ");
            params.addAll(categoryNames);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.ProductName LIKE ? ESCAPE '\\' ");
            params.add("%" + escapeLike(keyword.trim()) + "%");
        }

        StockFilter sf = (stock == null) ? StockFilter.ALL : stock;
        switch (sf) {
            case IN       -> sql.append(" AND COALESCE(q.TotalQty,0) > 0 ");
            case OUT      -> sql.append(" AND COALESCE(q.TotalQty,0) = 0 ");
            case BELOW_MIN -> { sql.append(" AND COALESCE(q.TotalQty,0) < ? "); params.add(threshold); }
            case ABOVE_MAX -> { sql.append(" AND COALESCE(q.TotalQty,0) > ? "); params.add(threshold); }
            case ALL -> {}
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            addAllParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("Cnt");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<Product> listProductsPagedByBranch(
            int branchId,
            List<String> categoryNames,
            String keyword,
            StockFilter stock,
            int threshold,
            int page, // 1-based
            int pageSize
    ) {
        List<Product> out = new ArrayList<>();

        StringBuilder sql = new StringBuilder(CTE_QTY_BY_BRANCH).append(SELECT_BASE_WITH_QTY);
        List<Object> params = new ArrayList<>();
        params.add(branchId); // tham số cho CTE

        if (categoryNames != null && !categoryNames.isEmpty()) {
            sql.append(" AND c.CategoryName IN (")
               .append(String.join(",", Collections.nCopies(categoryNames.size(), "?")))
               .append(") ");
            params.addAll(categoryNames);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.ProductName LIKE ? ESCAPE '\\' ");
            params.add("%" + escapeLike(keyword.trim()) + "%");
        }

        StockFilter sf = (stock == null) ? StockFilter.ALL : stock;
        switch (sf) {
            case IN       -> sql.append(" AND COALESCE(q.TotalQty,0) > 0 ");
            case OUT      -> sql.append(" AND COALESCE(q.TotalQty,0) = 0 ");
            case BELOW_MIN -> { sql.append(" AND COALESCE(q.TotalQty,0) < ? "); params.add(threshold); }
            case ABOVE_MAX -> { sql.append(" AND COALESCE(q.TotalQty,0) > ? "); params.add(threshold); }
            case ALL -> {}
        }

        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, pageSize);
        int offset   = (safePage - 1) * safeSize;

        sql.append(ORDER_BY_DEFAULT).append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        params.add(offset);
        params.add(safeSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            addAllParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    /* ===================== REPORT (giữ nguyên) ===================== */
//    public List<ProductStatisticDTO> getTopProducts(String sortBy, String period, int limit) {
//        List<ProductStatisticDTO> list = new ArrayList<>();
//        String dateCondition = "";
//        if ("this_month".equals(period)) {
//            dateCondition = "WHERE MONTH(o.CreatedAt) = MONTH(GETDATE()) AND YEAR(o.CreatedAt) = YEAR(GETDATE())";
//        } else if ("last_month".equals(period)) {
//            dateCondition = "WHERE MONTH(o.CreatedAt) = MONTH(DATEADD(MONTH, -1, GETDATE())) "
//                          + "AND YEAR(o.CreatedAt) = YEAR(DATEADD(MONTH, -1, GETDATE()))";
//        }
//
//        String orderBy = "revenue".equals(sortBy)
//                ? "SUM(od.Quantity * p.RetailPrice) DESC"
//                : "SUM(od.Quantity) DESC";
//
//        String sql = ""
//                + "SELECT TOP (?) \n"
//                + "    p.ProductName, \n"
//                + "    SUM(od.Quantity) AS TotalQuantity, \n"
//                + "    SUM(od.Quantity * p.RetailPrice) AS Revenue \n"
//                + "FROM Orders o \n"
//                + "JOIN OrderDetails od ON o.OrderID = od.OrderID \n"
//                + "JOIN ProductDetails pd ON od.ProductDetailID = pd.ProductDetailID \n"
//                + "JOIN Products p ON pd.ProductID = p.ProductID \n"
//                + (dateCondition.isEmpty() ? "" : dateCondition + "\n")
//                + "GROUP BY p.ProductName \n"
//                + "ORDER BY " + orderBy + ";";
//
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setInt(1, limit);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                ProductStatisticDTO dto = new ProductStatisticDTO();
//                dto.setProductName(rs.getString("ProductName"));
//                dto.setTotalQuantity(rs.getInt("TotalQuantity"));
//                dto.setRevenue(rs.getBigDecimal("Revenue"));
//                list.add(dto);
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
    public List<ProductStatisticDTO> getTopProducts(String sortBy, String period, int limit, int branchId) {
    List<ProductStatisticDTO> list = new ArrayList<>();
    String dateCondition = "";

    if ("this_month".equals(period)) {
        dateCondition = "WHERE MONTH(o.CreatedAt) = MONTH(GETDATE()) AND YEAR(o.CreatedAt) = YEAR(GETDATE())";
    } else if ("last_month".equals(period)) {
        dateCondition = "WHERE MONTH(o.CreatedAt) = MONTH(DATEADD(MONTH, -1, GETDATE())) "
                      + "AND YEAR(o.CreatedAt) = YEAR(DATEADD(MONTH, -1, GETDATE()))";
    }

    // nếu có branchId thì thêm điều kiện lọc theo chi nhánh
    if (branchId > 0) {
        if (dateCondition.isEmpty()) {
            dateCondition = "WHERE o.BranchID = ?";
        } else {
            dateCondition += " AND o.BranchID = ?";
        }
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
        int index = 1;
        ps.setInt(index++, limit);
        if (branchId > 0) {
            ps.setInt(index++, branchId);
        }
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            ProductStatisticDTO dto = new ProductStatisticDTO();
            dto.setProductName(rs.getString("ProductName"));
            dto.setTotalQuantity(rs.getInt("TotalQuantity"));
            dto.setRevenue(rs.getBigDecimal("Revenue"));
            list.add(dto);
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}


    /* ===================== DTO helpers (nếu cần cho inventory view) ===================== */
    private static Integer getNullableInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }
    private static BigDecimal getNullableBigDecimal(ResultSet rs, String col) throws SQLException {
        return rs.getBigDecimal(col);
    }
    private static LocalDateTime getNullableLdt(ResultSet rs, String col) throws SQLException {
        Timestamp ts = rs.getTimestamp(col);
        return (ts == null) ? null : ts.toLocalDateTime();
    }
    private static String getNullableString(ResultSet rs, String col) throws SQLException {
        return rs.getString(col);
    }
    private static ProductDTO extractProductDTOFromResultSet(ResultSet rs) throws SQLException {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(getNullableInt(rs, "ProductID"));
        dto.setProductName(getNullableString(rs, "ProductName"));
        dto.setBrandName(getNullableString(rs, "BrandName"));
        dto.setCategoryName(getNullableString(rs, "CategoryName"));
        dto.setSupplierName(getNullableString(rs, "SupplierName"));
        dto.setCostPrice(getNullableBigDecimal(rs, "CostPrice"));
        dto.setRetailPrice(getNullableBigDecimal(rs, "RetailPrice"));
        dto.setImageUrl(getNullableString(rs, "ImageURL"));
        dto.setCreatedAt(getNullableLdt(rs, "CreatedAt"));

        String status = getNullableString(rs, "Status");
        if (status != null) {
            dto.setIsActive("Đang kinh doanh".equalsIgnoreCase(status));
            dto.setStatusText(status);
        }
        try {
            Integer totalQty = getNullableInt(rs, "TotalQty");
            if (totalQty != null) dto.setTotalQty(totalQty);
        } catch (SQLException ignore) { }

        dto.setProductDetailId(getNullableInt(rs, "ProductDetailID"));
        dto.setInventoryQuantity(getNullableInt(rs, "InventoryQuantity"));
        dto.setDescription(getNullableString(rs, "Description"));
        dto.setProductCode(getNullableString(rs, "SerialNumber"));
        dto.setWarrantyPeriod(getNullableString(rs, "WarrantyPeriod"));
        return dto;
    }

    public List<ProductDTO> getInventoryProductListByPageByBranchId(int branchId, int offset, int limit) {
        List<ProductDTO> list = new ArrayList<>();
        int safeOffset = Math.max(0, offset);
        int safeLimit  = Math.max(1, limit);

        String sql = """
            SELECT 
                i.InventoryID,
                p.ProductID,
                ip.ProductDetailID,
                ip.Quantity AS InventoryQuantity,
                p.ProductName,
                b.BrandName,
                c.CategoryName,
                s.SupplierName,
                p.CostPrice,
                p.RetailPrice,
                p.ImageURL,
                CASE WHEN p.IsActive = 1 THEN N'Đang kinh doanh' ELSE N'Không kinh doanh' END AS Status,
                pd.Description,
                pd.SerialNumber,
                pd.WarrantyPeriod,
                p.CreatedAt
            FROM Inventory i
            LEFT JOIN InventoryProducts ip ON i.InventoryID = ip.InventoryID
            LEFT JOIN ProductDetails pd    ON ip.ProductDetailID = pd.ProductDetailID
            LEFT JOIN Products p           ON pd.ProductID = p.ProductID
            LEFT JOIN Brands b             ON p.BrandID = b.BrandID
            LEFT JOIN Categories c         ON p.CategoryID = c.CategoryID
            LEFT JOIN Suppliers s          ON p.SupplierID = s.SupplierID
            WHERE i.BranchID = ?
            ORDER BY COALESCE(ip.ProductDetailID, 0), p.ProductID
            OFFSET ? ROWS
            FETCH NEXT ? ROWS ONLY
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, safeOffset);
            ps.setInt(3, safeLimit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extractProductDTOFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    /* ===================== Utils ===================== */
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

    public int calcTotalPages(int totalRows, int pageSize) {
        if (pageSize <= 0) return 0;
        return (totalRows + pageSize - 1) / pageSize;
    }
    /* ===================== INVENTORY HELPERS & UPSERT QTY ===================== */

// Tạo (nếu chưa có) Inventory cho 1 chi nhánh, trả về InventoryID
private Integer getOrCreateInventoryIdForBranch(int branchId) throws SQLException {
    // 1) Thử tìm
    try (PreparedStatement ps = connection.prepareStatement(
            "SELECT InventoryID FROM Inventory WHERE BranchID = ?")) {
        ps.setInt(1, branchId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
    }
    // 2) Chưa có -> tạo mới
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

// Đảm bảo sản phẩm có ít nhất 1 ProductDetail để gắn tồn, trả về ProductDetailID
private Integer getOrCreateOneDetailForProduct(int productId) throws SQLException {
    // 1) Lấy 1 detail sẵn có (ưu tiên cái nhỏ nhất)
    try (PreparedStatement ps = connection.prepareStatement(
            "SELECT TOP 1 ProductDetailID FROM ProductDetails WHERE ProductID = ? ORDER BY ProductDetailID")) {
        ps.setInt(1, productId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
    }
    // 2) Nếu chưa có -> tạo 1 dòng detail “rỗng”
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

/**
 * Đặt (replace) số lượng tồn cho 1 Product tại 1 chi nhánh.
 * - Nếu đã có dòng trong InventoryProducts -> UPDATE Quantity
 * - Nếu chưa có -> INSERT
 * Trả về true nếu thành công.
 */
public boolean setQuantityForProductAtBranch(int productId, int branchId, int newQty) {
    boolean oldAutoCommit = true;
    try {
        oldAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        Integer inventoryId = getOrCreateInventoryIdForBranch(branchId);
        Integer productDetailId = getOrCreateOneDetailForProduct(productId);
        if (inventoryId == null || productDetailId == null) {
            connection.rollback();
            return false;
        }

        // SQL Server: dùng MERGE cho upsert
        String merge = """
            MERGE InventoryProducts AS tgt
            USING (SELECT ? AS InventoryID, ? AS ProductDetailID) AS src
               ON tgt.InventoryID = src.InventoryID AND tgt.ProductDetailID = src.ProductDetailID
            WHEN MATCHED THEN
                UPDATE SET Quantity = ?
            WHEN NOT MATCHED THEN
                INSERT (InventoryID, ProductDetailID, Quantity) VALUES (src.InventoryID, src.ProductDetailID, ?);
        """;

        try (PreparedStatement ps = connection.prepareStatement(merge)) {
            ps.setInt(1, inventoryId);
            ps.setInt(2, productDetailId);
            ps.setInt(3, newQty); // update
            ps.setInt(4, newQty); // insert
            ps.executeUpdate();
        }

        connection.commit();
        return true;
    } catch (SQLException e) {
        try { connection.rollback(); } catch (SQLException ignore) {}
        e.printStackTrace();
        return false;
    } finally {
        try { connection.setAutoCommit(oldAutoCommit); } catch (SQLException ignore) {}
    }
}
/**
 * Lấy thông tin sản phẩm theo ID (đơn giản, không tính tồn kho)
 */
public Product getProductById(int id) {
    String sql = """
        SELECT 
            p.ProductID, p.ProductName,
            b.BrandName, c.CategoryName, s.SupplierName,
            p.CostPrice, p.RetailPrice, p.ImageURL, p.VAT,
            p.CreatedAt, p.IsActive
        FROM Products p
        LEFT JOIN Brands b     ON p.BrandID = b.BrandID
        LEFT JOIN Categories c ON p.CategoryID = c.CategoryID
        LEFT JOIN Suppliers s  ON p.SupplierID = s.SupplierID
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

/**
 * Lấy sản phẩm theo ID, kèm tổng tồn kho (TotalQty)
 * => dùng CTE_QTY để cộng dồn tồn trong InventoryProducts
 */
public Product getProductByIdWithQty(int id) {
    String sql = CTE_QTY + SELECT_BASE_WITH_QTY + " AND p.ProductID = ? ";
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


}



