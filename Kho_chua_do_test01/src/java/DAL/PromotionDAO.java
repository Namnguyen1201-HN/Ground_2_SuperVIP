package DAL;

import Model.Promotion;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO extends DataBaseContext {

    public List<Promotion> getAllPromotions() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = """
            SELECT p.*, 
                   COALESCE(pc.ProductCount, 0) as ProductCount,
                   COALESCE(bc.BranchCount, 0) as BranchCount
            FROM Promotions p
            LEFT JOIN (
                SELECT PromotionID, COUNT(*) as ProductCount 
                FROM PromotionProducts 
                GROUP BY PromotionID
            ) pc ON p.PromotionID = pc.PromotionID
            LEFT JOIN (
                SELECT PromotionID, COUNT(*) as BranchCount 
                FROM PromotionBranches 
                GROUP BY PromotionID
            ) bc ON p.PromotionID = bc.PromotionID
            ORDER BY p.PromotionID DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Promotion promotion = new Promotion();
                promotion.setPromotionId(rs.getInt("PromotionID"));
                promotion.setPromoName(rs.getString("PromoName"));
                promotion.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                promotion.setStartDate(rs.getDate("StartDate"));
                promotion.setEndDate(rs.getDate("EndDate"));
                promotion.setProductCount(rs.getInt("ProductCount"));
                promotion.setBranchCount(rs.getInt("BranchCount"));
                
                promotions.add(promotion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promotions;
    }

    public List<Promotion> getPromotionsByBranch(int branchId) {
        List<Promotion> promotions = new ArrayList<>();
        String sql = """
            SELECT p.*, 
                   COALESCE(pc.ProductCount, 0) as ProductCount,
                   COALESCE(bc.BranchCount, 0) as BranchCount
            FROM Promotions p
            INNER JOIN PromotionBranches pb ON p.PromotionID = pb.PromotionID
            LEFT JOIN (
                SELECT PromotionID, COUNT(*) as ProductCount 
                FROM PromotionProducts 
                GROUP BY PromotionID
            ) pc ON p.PromotionID = pc.PromotionID
            LEFT JOIN (
                SELECT PromotionID, COUNT(*) as BranchCount 
                FROM PromotionBranches 
                GROUP BY PromotionID
            ) bc ON p.PromotionID = bc.PromotionID
            WHERE pb.BranchID = ?
            ORDER BY p.PromotionID DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Promotion promotion = new Promotion();
                    promotion.setPromotionId(rs.getInt("PromotionID"));
                    promotion.setPromoName(rs.getString("PromoName"));
                    promotion.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                    promotion.setStartDate(rs.getDate("StartDate"));
                    promotion.setEndDate(rs.getDate("EndDate"));
                    promotion.setProductCount(rs.getInt("ProductCount"));
                    promotion.setBranchCount(rs.getInt("BranchCount"));
                    
                    promotions.add(promotion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promotions;
    }

    public boolean createPromotion(Promotion promotion, List<Integer> branchIds, List<Integer> productDetailIds) {
        String insertPromotion = """
            INSERT INTO Promotions (PromoName, DiscountPercent, StartDate, EndDate)
            VALUES (?, ?, ?, ?)
        """;
        
        try {
            connection.setAutoCommit(false);
            
            // Insert promotion
            int promotionId;
            try (PreparedStatement ps = connection.prepareStatement(insertPromotion, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, promotion.getPromoName());
                ps.setBigDecimal(2, promotion.getDiscountPercent());
                ps.setDate(3, new java.sql.Date(promotion.getStartDate().getTime()));
                ps.setDate(4, new java.sql.Date(promotion.getEndDate().getTime()));
                
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        promotionId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to get generated promotion ID");
                    }
                }
            }
            
            // Insert promotion branches
            if (branchIds != null && !branchIds.isEmpty()) {
                String insertBranches = "INSERT INTO PromotionBranches (PromotionID, BranchID) VALUES (?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insertBranches)) {
                    for (Integer branchId : branchIds) {
                        ps.setInt(1, promotionId);
                        ps.setInt(2, branchId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            
            // Insert promotion products
            if (productDetailIds != null && !productDetailIds.isEmpty()) {
                String insertProducts = "INSERT INTO PromotionProducts (PromotionID, ProductDetailID) VALUES (?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insertProducts)) {
                    for (Integer productDetailId : productDetailIds) {
                        ps.setInt(1, promotionId);
                        ps.setInt(2, productDetailId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deletePromotion(int promotionId) {
        String deleteBranches = "DELETE FROM PromotionBranches WHERE PromotionID = ?";
        String deleteProducts = "DELETE FROM PromotionProducts WHERE PromotionID = ?";
        String deletePromotion = "DELETE FROM Promotions WHERE PromotionID = ?";
        
        try {
            connection.setAutoCommit(false);
            
            // Delete branches
            try (PreparedStatement ps = connection.prepareStatement(deleteBranches)) {
                ps.setInt(1, promotionId);
                ps.executeUpdate();
            }
            
            // Delete products
            try (PreparedStatement ps = connection.prepareStatement(deleteProducts)) {
                ps.setInt(1, promotionId);
                ps.executeUpdate();
            }
            
            // Delete promotion
            try (PreparedStatement ps = connection.prepareStatement(deletePromotion)) {
                ps.setInt(1, promotionId);
                ps.executeUpdate();
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Promotion getPromotionById(int promotionId) {
        String sql = """
            SELECT p.*, 
                   COALESCE(pc.ProductCount, 0) as ProductCount,
                   COALESCE(bc.BranchCount, 0) as BranchCount
            FROM Promotions p
            LEFT JOIN (
                SELECT PromotionID, COUNT(*) as ProductCount 
                FROM PromotionProducts 
                GROUP BY PromotionID
            ) pc ON p.PromotionID = pc.PromotionID
            LEFT JOIN (
                SELECT PromotionID, COUNT(*) as BranchCount 
                FROM PromotionBranches 
                GROUP BY PromotionID
            ) bc ON p.PromotionID = bc.PromotionID
            WHERE p.PromotionID = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Promotion promotion = new Promotion();
                    promotion.setPromotionId(rs.getInt("PromotionID"));
                    promotion.setPromoName(rs.getString("PromoName"));
                    promotion.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                    promotion.setStartDate(rs.getDate("StartDate"));
                    promotion.setEndDate(rs.getDate("EndDate"));
                    promotion.setProductCount(rs.getInt("ProductCount"));
                    promotion.setBranchCount(rs.getInt("BranchCount"));
                    return promotion;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public boolean updatePromotion(Promotion promotion, List<Integer> branchIds, List<Integer> productDetailIds) {
        try {
            connection.setAutoCommit(false);
            
            // Update promotion
            String updatePromotion = "UPDATE Promotions SET PromoName = ?, DiscountPercent = ?, StartDate = ?, EndDate = ? WHERE PromotionID = ?";
            try (PreparedStatement ps = connection.prepareStatement(updatePromotion)) {
                ps.setString(1, promotion.getPromoName());
                ps.setBigDecimal(2, promotion.getDiscountPercent());
                ps.setDate(3, new java.sql.Date(promotion.getStartDate().getTime()));
                ps.setDate(4, new java.sql.Date(promotion.getEndDate().getTime()));
                ps.setInt(5, promotion.getPromotionId());
                
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    throw new SQLException("Failed to update promotion");
                }
            }
            
            // Delete existing branches and products
            String deleteBranches = "DELETE FROM PromotionBranches WHERE PromotionID = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteBranches)) {
                ps.setInt(1, promotion.getPromotionId());
                ps.executeUpdate();
            }
            
            String deleteProducts = "DELETE FROM PromotionProducts WHERE PromotionID = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteProducts)) {
                ps.setInt(1, promotion.getPromotionId());
                ps.executeUpdate();
            }
            
            // Insert new branches
            if (branchIds != null && !branchIds.isEmpty()) {
                String insertBranches = "INSERT INTO PromotionBranches (PromotionID, BranchID) VALUES (?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insertBranches)) {
                    for (Integer branchId : branchIds) {
                        ps.setInt(1, promotion.getPromotionId());
                        ps.setInt(2, branchId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            
            // Insert new products
            if (productDetailIds != null && !productDetailIds.isEmpty()) {
                String insertProducts = "INSERT INTO PromotionProducts (PromotionID, ProductDetailID) VALUES (?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insertProducts)) {
                    for (Integer productDetailId : productDetailIds) {
                        ps.setInt(1, promotion.getPromotionId());
                        ps.setInt(2, productDetailId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int countPromotionsByBranch(int branchId) {
        String sql = """
            SELECT COUNT(*) FROM Promotions p
            INNER JOIN PromotionBranches pb ON p.PromotionID = pb.PromotionID
            WHERE pb.BranchID = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countAllPromotions() {
        String sql = "SELECT COUNT(*) FROM Promotions";
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countActivePromotions() {
        String sql = "SELECT COUNT(*) FROM Promotions WHERE GETDATE() BETWEEN StartDate AND EndDate";
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countExpiredPromotions() {
        String sql = "SELECT COUNT(*) FROM Promotions WHERE GETDATE() > EndDate";
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countScheduledPromotions() {
        String sql = "SELECT COUNT(*) FROM Promotions WHERE StartDate > GETDATE()";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    public List<Integer> getPromotionBranchIds(int promotionId) {
        List<Integer> branchIds = new ArrayList<>();
        String sql = "SELECT BranchID FROM PromotionBranches WHERE PromotionID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    branchIds.add(rs.getInt("BranchID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return branchIds;
    }

    public List<Integer> getPromotionProductIds(int promotionId) {
        List<Integer> productIds = new ArrayList<>();
        String sql = "SELECT ProductDetailID FROM PromotionProducts WHERE PromotionID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productIds.add(rs.getInt("ProductDetailID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return productIds;
    }
}