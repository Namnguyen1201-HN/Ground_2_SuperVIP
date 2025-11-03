package DAL;

import Model.CashFlowReportDTO;
import Model.User;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CashFlowDAO extends DataBaseContext {
    
    /**
     * Get revenue reports (FlowType = 'revenue')
     */
    public List<CashFlowReportDTO> getRevenueReports(
            Date fromDate, Date toDate, Integer branchId, 
            String employeeName, String paymentMethod) {
        return getCashFlowReports("revenue", fromDate, toDate, branchId, employeeName, paymentMethod);
    }
    
    /**
     * Get expense reports (FlowType = 'expense')
     */
    public List<CashFlowReportDTO> getExpenseReports(
            Date fromDate, Date toDate, Integer branchId, 
            String employeeName, String paymentMethod) {
        return getCashFlowReports("expense", fromDate, toDate, branchId, employeeName, paymentMethod);
    }
    
    /**
     * Common method to get cash flow reports
     */
    private List<CashFlowReportDTO> getCashFlowReports(
            String flowType, Date fromDate, Date toDate, 
            Integer branchId, String employeeName, String paymentMethod) {
        
        List<CashFlowReportDTO> list = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    cf.CashFlowID, ");
        sql.append("    cf.FlowType, ");
        sql.append("    cf.Amount, ");
        sql.append("    cf.Category, ");
        sql.append("    cf.PaymentMethod, ");
        sql.append("    cf.CreatedAt, ");
        sql.append("    cf.BranchID, ");
        sql.append("    ISNULL(b.BranchName, N'Không xác định') AS BranchName, ");
        sql.append("    cf.CreatedBy ");
        sql.append("FROM CashFlows cf ");
        sql.append("LEFT JOIN Branches b ON cf.BranchID = b.BranchID ");
        sql.append("WHERE cf.FlowType = ? ");
        
        List<Object> params = new ArrayList<>();
        params.add(flowType);
        
        // Date filter
        if (fromDate != null) {
            sql.append("AND CAST(cf.CreatedAt AS DATE) >= ? ");
            params.add(new java.sql.Date(fromDate.getTime()));
        }
        
        if (toDate != null) {
            sql.append("AND CAST(cf.CreatedAt AS DATE) <= ? ");
            params.add(new java.sql.Date(toDate.getTime()));
        }
        
        // Branch filter
        if (branchId != null && branchId > 0) {
            sql.append("AND cf.BranchID = ? ");
            params.add(branchId);
        }
        
        // Employee filter (CreatedBy)
        if (employeeName != null && !employeeName.trim().isEmpty()) {
            sql.append("AND cf.CreatedBy LIKE ? ");
            params.add("%" + employeeName.trim() + "%");
        }
        
        // Payment method filter
        if (paymentMethod != null && !paymentMethod.trim().isEmpty() && !"all".equals(paymentMethod)) {
            sql.append("AND cf.PaymentMethod = ? ");
            params.add(paymentMethod.trim());
        }
        
        sql.append("ORDER BY cf.CreatedAt DESC, cf.CashFlowID DESC");
        
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CashFlowReportDTO dto = new CashFlowReportDTO();
                dto.setCashFlowId(rs.getInt("CashFlowID"));
                dto.setFlowType(rs.getString("FlowType"));
                dto.setAmount(rs.getBigDecimal("Amount"));
                dto.setCategory(rs.getString("Category"));
                dto.setPaymentMethod(rs.getString("PaymentMethod"));
                
                Timestamp ts = rs.getTimestamp("CreatedAt");
                if (ts != null) {
                    dto.setCreatedAt(new Date(ts.getTime()));
                }
                
                dto.setBranchName(rs.getString("BranchName"));
                dto.setCreatedBy(rs.getString("CreatedBy"));
                
                Object bid = rs.getObject("BranchID");
                if (bid != null) {
                    dto.setBranchId((Integer) bid);
                }
                
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Get total amount for revenue reports
     */
    public BigDecimal getTotalRevenue(
            Date fromDate, Date toDate, Integer branchId, 
            String employeeName, String paymentMethod) {
        return getTotalAmount("revenue", fromDate, toDate, branchId, employeeName, paymentMethod);
    }
    
    /**
     * Get total amount for expense reports
     */
    public BigDecimal getTotalExpense(
            Date fromDate, Date toDate, Integer branchId, 
            String employeeName, String paymentMethod) {
        return getTotalAmount("expense", fromDate, toDate, branchId, employeeName, paymentMethod);
    }
    
    /**
     * Common method to get total amount
     */
    private BigDecimal getTotalAmount(
            String flowType, Date fromDate, Date toDate, 
            Integer branchId, String employeeName, String paymentMethod) {
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ISNULL(SUM(cf.Amount), 0) AS TotalAmount ");
        sql.append("FROM CashFlows cf ");
        sql.append("WHERE cf.FlowType = ? ");
        
        List<Object> params = new ArrayList<>();
        params.add(flowType);
        
        // Date filter
        if (fromDate != null) {
            sql.append("AND CAST(cf.CreatedAt AS DATE) >= ? ");
            params.add(new java.sql.Date(fromDate.getTime()));
        }
        
        if (toDate != null) {
            sql.append("AND CAST(cf.CreatedAt AS DATE) <= ? ");
            params.add(new java.sql.Date(toDate.getTime()));
        }
        
        // Branch filter
        if (branchId != null && branchId > 0) {
            sql.append("AND cf.BranchID = ? ");
            params.add(branchId);
        }
        
        // Employee filter
        if (employeeName != null && !employeeName.trim().isEmpty()) {
            sql.append("AND cf.CreatedBy LIKE ? ");
            params.add("%" + employeeName.trim() + "%");
        }
        
        // Payment method filter
        if (paymentMethod != null && !paymentMethod.trim().isEmpty() && !"all".equals(paymentMethod)) {
            sql.append("AND cf.PaymentMethod = ? ");
            params.add(paymentMethod.trim());
        }
        
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("TotalAmount");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Get employees by branch ID
     */
    public List<User> getEmployeesByBranch(Integer branchId) {
        List<User> list = new ArrayList<>();
        
        if (branchId == null || branchId <= 0) {
            return list;
        }
        
        String sql = "SELECT DISTINCT u.UserID, u.FullName " +
                    "FROM Users u " +
                    "WHERE u.BranchID = ? AND u.IsActive = 1 " +
                    "ORDER BY u.FullName";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setFullName(rs.getString("FullName"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Get employees who have created cash flows (for filtering)
     */
    public List<User> getEmployeesWithCashFlows(Integer branchId) {
        List<User> list = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT u.UserID, u.FullName ");
        sql.append("FROM Users u ");
        sql.append("INNER JOIN CashFlows cf ON cf.CreatedBy = u.FullName ");
        sql.append("WHERE u.IsActive = 1 ");
        
        if (branchId != null && branchId > 0) {
            sql.append("AND cf.BranchID = ? ");
        }
        
        sql.append("ORDER BY u.FullName");
        
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (branchId != null && branchId > 0) {
                ps.setInt(1, branchId);
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setFullName(rs.getString("FullName"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
}

