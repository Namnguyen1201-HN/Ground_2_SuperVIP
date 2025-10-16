package DAL;

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
        private String name;
        private String sku; // ProductCode
        private String imageUrl;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
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
        String sql = "SELECT pd.ProductDetailID, p.ProductName, pd.ProductCode AS SKU, p.ImageURL " +
                     "FROM ProductDetails pd JOIN Products p ON p.ProductID = pd.ProductID " +
                     "WHERE pd.ProductDetailID IN " + in;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;
            for (Integer id : ids) ps.setInt(idx++, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetailInfo info = new DetailInfo();
                    info.setId(rs.getInt("ProductDetailID"));
                    info.setName(rs.getString("ProductName"));
                    info.setSku(rs.getString("SKU"));
                    info.setImageUrl(rs.getString("ImageURL"));
                    map.put(info.getId(), info);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public DetailInfo getInfoByCode(String productCode) {
        String sql = "SELECT pd.ProductDetailID, p.ProductName, pd.ProductCode AS SKU, p.ImageURL " +
                     "FROM ProductDetails pd JOIN Products p ON p.ProductID = pd.ProductID WHERE pd.ProductCode = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, productCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DetailInfo info = new DetailInfo();
                    info.setId(rs.getInt("ProductDetailID"));
                    info.setName(rs.getString("ProductName"));
                    info.setSku(rs.getString("SKU"));
                    info.setImageUrl(rs.getString("ImageURL"));
                    return info;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}


