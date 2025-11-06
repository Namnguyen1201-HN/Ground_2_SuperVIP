package DAL;

import Model.Promotion;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PromotionDAO extends DataBaseContext {

    /**
     * Get all promotions with branch count and product count
     * JOIN với 3 bảng: Promotions, PromotionBranches, PromotionProducts
     */
    public List<Promotion> getAllPromotions() {
        List<Promotion> list = new ArrayList<>();
        String sql = "SELECT " +
                    "p.PromotionID, " +
                    "p.PromoName, " +
                    "p.DiscountPercent, " +
                    "p.StartDate, " +
                    "p.EndDate, " +
                    "ISNULL(COUNT(DISTINCT pb.BranchID), 0) AS BranchCount, " +
                    "ISNULL(COUNT(DISTINCT pp.ProductDetailID), 0) AS ProductCount " +
                    "FROM Promotions p " +
                    "LEFT JOIN PromotionBranches pb ON p.PromotionID = pb.PromotionID " +
                    "LEFT JOIN PromotionProducts pp ON p.PromotionID = pp.PromotionID " +
                    "GROUP BY p.PromotionID, p.PromoName, p.DiscountPercent, p.StartDate, p.EndDate " +
                    "ORDER BY p.PromotionID DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Promotion promo = new Promotion();
                promo.setPromotionId(rs.getInt("PromotionID"));
                promo.setPromoName(rs.getString("PromoName"));
                promo.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                
                Date startDate = rs.getDate("StartDate");
                Date endDate = rs.getDate("EndDate");
                promo.setStartDate(startDate);
                promo.setEndDate(endDate);
                
                promo.setBranchCount(rs.getInt("BranchCount"));
                promo.setProductCount(rs.getInt("ProductCount"));
                
                // Tính toán trạng thái và số ngày còn lại
                calculateStatus(promo, startDate, endDate);
                
                list.add(promo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tính toán trạng thái và số ngày còn lại
     */
    private void calculateStatus(Promotion promo, Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            promo.setStatus("unknown");
            return;
        }
        
        Date now = new Date();
        
        if (now.before(startDate)) {
            // Chưa bắt đầu - Đã lên lịch
            promo.setStatus("scheduled");
            long diff = startDate.getTime() - now.getTime();
            promo.setDaysRemaining(diff / (1000 * 60 * 60 * 24));
        } else if (now.after(endDate)) {
            // Đã hết hạn
            promo.setStatus("expired");
            promo.setDaysRemaining(0);
        } else {
            // Đang hoạt động
            promo.setStatus("active");
            long diff = endDate.getTime() - now.getTime();
            promo.setDaysRemaining(diff / (1000 * 60 * 60 * 24));
        }
    }

    /**
     * Get promotion by ID
     */
    public Promotion getPromotionById(int promotionId) {
        String sql = "SELECT " +
                    "p.PromotionID, " +
                    "p.PromoName, " +
                    "p.DiscountPercent, " +
                    "p.StartDate, " +
                    "p.EndDate, " +
                    "ISNULL(COUNT(DISTINCT pb.BranchID), 0) AS BranchCount, " +
                    "ISNULL(COUNT(DISTINCT pp.ProductDetailID), 0) AS ProductCount " +
                    "FROM Promotions p " +
                    "LEFT JOIN PromotionBranches pb ON p.PromotionID = pb.PromotionID " +
                    "LEFT JOIN PromotionProducts pp ON p.PromotionID = pp.PromotionID " +
                    "WHERE p.PromotionID = ? " +
                    "GROUP BY p.PromotionID, p.PromoName, p.DiscountPercent, p.StartDate, p.EndDate";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Promotion promo = new Promotion();
                promo.setPromotionId(rs.getInt("PromotionID"));
                promo.setPromoName(rs.getString("PromoName"));
                promo.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                
                Date startDate = rs.getDate("StartDate");
                Date endDate = rs.getDate("EndDate");
                promo.setStartDate(startDate);
                promo.setEndDate(endDate);
                
                promo.setBranchCount(rs.getInt("BranchCount"));
                promo.setProductCount(rs.getInt("ProductCount"));
                
                calculateStatus(promo, startDate, endDate);
                
                return promo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Insert new promotion
     */
    public boolean insertPromotion(Promotion promo) {
        String sql = "INSERT INTO Promotions (PromoName, DiscountPercent, StartDate, EndDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, promo.getPromoName());
            ps.setBigDecimal(2, promo.getDiscountPercent());
            ps.setDate(3, new java.sql.Date(promo.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(promo.getEndDate().getTime()));
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    promo.setPromotionId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update promotion
     */
    public boolean updatePromotion(Promotion promo) {
        String sql = "UPDATE Promotions SET PromoName=?, DiscountPercent=?, StartDate=?, EndDate=? WHERE PromotionID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, promo.getPromoName());
            ps.setBigDecimal(2, promo.getDiscountPercent());
            ps.setDate(3, new java.sql.Date(promo.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(promo.getEndDate().getTime()));
            ps.setInt(5, promo.getPromotionId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete promotion and related records in PromotionBranches and PromotionProducts
     */
    public boolean deletePromotion(int promotionId) {
        try {
            // First delete related records
            deletePromotionBranches(promotionId);
            deletePromotionProducts(promotionId);
            
            // Then delete the promotion itself
            String sql = "DELETE FROM Promotions WHERE PromotionID = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, promotionId);
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete all branches for a promotion
     */
    public boolean deletePromotionBranches(int promotionId) {
        String sql = "DELETE FROM PromotionBranches WHERE PromotionID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete all products for a promotion
     */
    public boolean deletePromotionProducts(int promotionId) {
        String sql = "DELETE FROM PromotionProducts WHERE PromotionID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Insert promotion branches
     */
    public boolean insertPromotionBranches(int promotionId, List<Integer> branchIds) {
        if (branchIds == null || branchIds.isEmpty()) {
            return true; // Không có branch nào cũng OK
        }
        
        String sql = "INSERT INTO PromotionBranches (PromotionID, BranchID) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Integer branchId : branchIds) {
                ps.setInt(1, promotionId);
                ps.setInt(2, branchId);
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Insert promotion products
     */
    public boolean insertPromotionProducts(int promotionId, List<Integer> productDetailIds) {
        if (productDetailIds == null || productDetailIds.isEmpty()) {
            return true; // Không có product nào cũng OK
        }
        
        String sql = "INSERT INTO PromotionProducts (PromotionID, ProductDetailID) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Integer productDetailId : productDetailIds) {
                ps.setInt(1, promotionId);
                ps.setInt(2, productDetailId);
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get branch IDs for a promotion
     */
    public List<Integer> getBranchIdsByPromotion(int promotionId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT BranchID FROM PromotionBranches WHERE PromotionID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("BranchID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get product detail IDs for a promotion
     */
    public List<Integer> getProductDetailIdsByPromotion(int promotionId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT ProductDetailID FROM PromotionProducts WHERE PromotionID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("ProductDetailID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

