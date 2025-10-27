package DAL;

import Model.DashboardStatsDTO;
import Model.Order;
import Model.RevenueStatisticDTO;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends DataBaseContext {

    private Order map(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("OrderID"));
        Object bid = rs.getObject("BranchID");
        if (bid != null) {
            o.setBranchId((Integer) bid);
        }
        o.setCreatedBy(rs.getInt("CreatedBy"));
        o.setOrderStatus(rs.getString("OrderStatus"));
        o.setCreatedAt(rs.getTimestamp("CreatedAt"));
        Object cid = rs.getObject("CustomerID");
        if (cid != null) {
            o.setCustomerId((Integer) cid);
        }
        o.setPaymentMethod(rs.getString("PaymentMethod"));
        o.setNotes(rs.getString("Notes"));
        o.setGrandTotal(rs.getBigDecimal("GrandTotal"));
        o.setCustomerPay(rs.getBigDecimal("CustomerPay"));
        o.setChangeAmount(rs.getBigDecimal("Change"));
        return o;
    }

    public List<Order> getAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders ORDER BY OrderID DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class PagedOrders {

        public final List<Order> orders;
        public final int total;
        public final int page;
        public final int pageSize;

        public PagedOrders(List<Order> orders, int total, int page, int pageSize) {
            this.orders = orders;
            this.total = total;
            this.page = page;
            this.pageSize = pageSize;
        }
    }

    public PagedOrders search(
            Integer branchId,
            String status,
            String keyword,
            java.sql.Timestamp fromDate,
            java.sql.Timestamp toDate,
            Double minSpent,
            Double maxSpent,
            int page,
            int pageSize,
            int userId,
            int roleId) {

        List<Order> list = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");

        if (branchId != null) {
            where.append(" AND o.BranchID = ?");
            params.add(branchId);
        }
        if (status != null && !status.isBlank()) {
            where.append(" AND o.OrderStatus = ?");
            params.add(status);
        }
        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND (CAST(o.OrderID AS NVARCHAR(20)) LIKE ? OR o.PaymentMethod LIKE ? OR o.Notes LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        if (fromDate != null) {
            where.append(" AND o.CreatedAt >= ?");
            params.add(fromDate);
        }
        if (toDate != null) {
            where.append(" AND o.CreatedAt <= ?");
            params.add(toDate);
        }
        if (minSpent != null) {
            where.append(" AND o.GrandTotal >= ?");
            params.add(BigDecimal.valueOf(minSpent));
        }
        if (maxSpent != null) {
            where.append(" AND o.GrandTotal <= ?");
            params.add(BigDecimal.valueOf(maxSpent));
        }
        if (roleId != 0){
            where.append(" AND u.UserID = ?");
            params.add(userId);
        }

        int offset = Math.max(0, (page - 1) * pageSize);

        String sql = """
        select * from Branches as br
        join Users as u on br.BranchID = u.BranchID
        join Orders as o on o.BranchID = br.BranchID
    """ + where + " ORDER BY o.OrderID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        String countSql = "SELECT COUNT(*) FROM Orders o" + where;

        int total = 0;

        // Đếm tổng
        try (PreparedStatement cps = connection.prepareStatement(countSql)) {
            for (int i = 0; i < params.size(); i++) {
                cps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = cps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Lấy danh sách đơn hàng
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;
            for (Object p : params) {
                ps.setObject(idx++, p);
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PagedOrders(list, total, page, pageSize);
    }

    public Order getById(int id) {
        String sql = "SELECT * FROM Orders WHERE OrderID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insert(Order o) {
        String sql = "INSERT INTO Orders (BranchID, CreatedBy, OrderStatus, CreatedAt, CustomerID, PaymentMethod, Notes, GrandTotal, CustomerPay, Change) "
                + "VALUES (?, ?, ?, GETDATE(), ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (o.getBranchId() != null) {
                ps.setInt(1, o.getBranchId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, o.getCreatedBy());
            ps.setString(3, o.getOrderStatus());
            if (o.getCustomerId() != null) {
                ps.setInt(4, o.getCustomerId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, o.getPaymentMethod());
            ps.setString(6, o.getNotes());
            ps.setBigDecimal(7, o.getGrandTotal() != null ? o.getGrandTotal() : BigDecimal.ZERO);
            ps.setBigDecimal(8, o.getCustomerPay() != null ? o.getCustomerPay() : BigDecimal.ZERO);
            ps.setBigDecimal(9, o.getChangeAmount() != null ? o.getChangeAmount() : BigDecimal.ZERO);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE Orders SET OrderStatus=? WHERE OrderID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBranch(int orderId, Integer branchId) {
        String sql = "UPDATE Orders SET BranchID=? WHERE OrderID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (branchId == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, branchId);
            }
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int orderId) {
        String delDetails = "DELETE FROM OrderDetails WHERE OrderID=?";
        String delOrder = "DELETE FROM Orders WHERE OrderID=?";
        try (PreparedStatement a = connection.prepareStatement(delDetails); PreparedStatement b = connection.prepareStatement(delOrder)) {
            a.setInt(1, orderId);
            a.executeUpdate();
            b.setInt(1, orderId);
            return b.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy doanh thu theo ngày trong tháng hiện tại hoặc tháng trước
    public List<RevenueStatisticDTO> getRevenueByDay(String period, Integer branchId) {
        List<RevenueStatisticDTO> list = new ArrayList<>();
        String branchFilter = (branchId != null) ? " AND BranchID = ?" : "";
        String sql = """
            SELECT CONVERT(VARCHAR(10), CreatedAt, 23) AS Label, SUM(GrandTotal) AS Total
            FROM Orders
            WHERE 
                OrderStatus = 'Completed' AND
                CreatedAt >= DATEADD(MONTH, CASE WHEN ? = 'this_month' THEN 0 ELSE -1 END, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
                AND CreatedAt < DATEADD(MONTH, CASE WHEN ? = 'this_month' THEN 1 ELSE 0 END, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
        """ + branchFilter + """
            GROUP BY CONVERT(VARCHAR(10), CreatedAt, 23)
            ORDER BY Label;
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, period);
            ps.setString(2, period);
            if (branchId != null) ps.setInt(3, branchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RevenueStatisticDTO(rs.getString("Label"), rs.getDouble("Total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy doanh thu theo giờ trong ngày hôm nay
    public List<RevenueStatisticDTO> getRevenueByHour(String period, Integer branchId) {
        List<RevenueStatisticDTO> list = new ArrayList<>();
        String branchFilter = (branchId != null) ? " AND BranchID = ?" : "";
        String sql = """
            SELECT DATEPART(HOUR, CreatedAt) AS HourLabel, SUM(GrandTotal) AS Total
            FROM Orders
            WHERE 
                OrderStatus = 'Completed' AND
                CreatedAt >= DATEADD(MONTH, CASE WHEN ? = 'this_month' THEN 0 ELSE -1 END, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
                AND CreatedAt < DATEADD(MONTH, CASE WHEN ? = 'this_month' THEN 1 ELSE 0 END, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
        """ + branchFilter + """
            GROUP BY DATEPART(HOUR, CreatedAt)
            ORDER BY HourLabel;
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, period);
            ps.setString(2, period);
            if (branchId != null) ps.setInt(3, branchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RevenueStatisticDTO(rs.getInt("HourLabel") + "h", rs.getDouble("Total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy doanh thu theo thứ trong tuần
    public List<RevenueStatisticDTO> getRevenueByWeekday(String period, Integer branchId) {
        List<RevenueStatisticDTO> list = new ArrayList<>();
        String branchFilter = (branchId != null) ? " AND BranchID = ?" : "";
        String sql = """
            SELECT DATENAME(WEEKDAY, CreatedAt) AS DayLabel, SUM(GrandTotal) AS Total
            FROM Orders
            WHERE 
                OrderStatus = 'Completed' AND
                CreatedAt >= DATEADD(MONTH, CASE WHEN ? = 'this_month' THEN 0 ELSE -1 END, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
                AND CreatedAt < DATEADD(MONTH, CASE WHEN ? = 'this_month' THEN 1 ELSE 0 END, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
        """ + branchFilter + """
            GROUP BY DATENAME(WEEKDAY, CreatedAt), DATEPART(WEEKDAY, CreatedAt)
            ORDER BY DATEPART(WEEKDAY, CreatedAt);
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, period);
            ps.setString(2, period);
            if (branchId != null) ps.setInt(3, branchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RevenueStatisticDTO(rs.getString("DayLabel"), rs.getDouble("Total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy doanh thu của 3 tháng gần đây
    public List<RevenueStatisticDTO> getRevenueLast3Months(Integer branchId) {
        List<RevenueStatisticDTO> list = new ArrayList<>();
        String branchFilter = (branchId != null) ? " AND BranchID = ?" : "";
        String sql = """
            SELECT FORMAT(CreatedAt, 'MM/yyyy') AS MonthLabel, SUM(GrandTotal) AS Total
            FROM Orders
            WHERE 
                OrderStatus = 'Completed' AND
                CreatedAt >= DATEADD(MONTH, -2, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
        """ + branchFilter + """
            GROUP BY FORMAT(CreatedAt, 'MM/yyyy'), YEAR(CreatedAt), MONTH(CreatedAt)
            ORDER BY YEAR(CreatedAt), MONTH(CreatedAt);
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (branchId != null) ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RevenueStatisticDTO(rs.getString("MonthLabel"), rs.getDouble("Total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public DashboardStatsDTO getDashboardStats(Integer branchId) {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        System.out.println("=== getDashboardStats called ===");
        System.out.println("branchId: " + branchId);

        String branchFilter = (branchId != null) ? " AND BranchID = ?" : "";

        String sqlToday = """
            SELECT ISNULL(SUM(GrandTotal), 0) AS TodayRevenue
            FROM Orders
            WHERE CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE)
              AND OrderStatus = 'Completed'
        """ + branchFilter;

        String sqlYesterday = """
            SELECT ISNULL(SUM(GrandTotal), 0) AS YesterdayRevenue
            FROM Orders
            WHERE CAST(CreatedAt AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE)
              AND OrderStatus = 'Completed'
        """ + branchFilter;

        String sqlLastMonthSameDay = """
            SELECT ISNULL(SUM(GrandTotal), 0) AS LastMonthSameDayRevenue
            FROM Orders
            WHERE 
                OrderStatus = 'Completed'
                AND DAY(CreatedAt) = DAY(GETDATE())
                AND MONTH(CreatedAt) = MONTH(DATEADD(MONTH, -1, GETDATE()))
                AND YEAR(CreatedAt) = YEAR(DATEADD(MONTH, -1, GETDATE()))
        """ + branchFilter;

        String sqlReturn = """
            SELECT COUNT(*) AS ReturnCount
            FROM Orders
            WHERE CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE)
              AND OrderStatus = 'Cancelled'
        """ + branchFilter;

        // Thêm các query mới
        String sqlTodayOrders = """
            SELECT COUNT(*) AS TodayOrders
            FROM Orders
            WHERE CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE)
        """ + branchFilter;

        String sqlWeekOrders = """
            SELECT COUNT(*) AS WeekOrders
            FROM Orders
            WHERE DATEPART(WEEK, CreatedAt) = DATEPART(WEEK, GETDATE())
              AND YEAR(CreatedAt) = YEAR(GETDATE())
        """ + branchFilter;

        String sqlMonthOrders = """
            SELECT COUNT(*) AS MonthOrders
            FROM Orders
            WHERE MONTH(CreatedAt) = MONTH(GETDATE())
              AND YEAR(CreatedAt) = YEAR(GETDATE())
        """ + branchFilter;

        String sqlWeekRevenue = """
            SELECT ISNULL(SUM(GrandTotal), 0) AS WeekRevenue
            FROM Orders
            WHERE DATEPART(WEEK, CreatedAt) = DATEPART(WEEK, GETDATE())
              AND YEAR(CreatedAt) = YEAR(GETDATE())
              AND OrderStatus = 'Completed'
        """ + branchFilter;

        String sqlMonthRevenue = """
            SELECT ISNULL(SUM(GrandTotal), 0) AS MonthRevenue
            FROM Orders
            WHERE MONTH(CreatedAt) = MONTH(GETDATE())
              AND YEAR(CreatedAt) = YEAR(GETDATE())
              AND OrderStatus = 'Completed'
        """ + branchFilter;

        try {
            double today = 0, yesterday = 0, lastMonthSameDay = 0;
            int returnCount = 0;
            int todayOrders = 0, weekOrders = 0, monthOrders = 0;
            double weekRevenue = 0, monthRevenue = 0;

            try (PreparedStatement ps1 = connection.prepareStatement(sqlToday)) {
                if (branchId != null) ps1.setInt(1, branchId);
                System.out.println("SQL Today Revenue: " + sqlToday);
                ResultSet rs1 = ps1.executeQuery();
                if (rs1.next()) {
                    today = rs1.getDouble("TodayRevenue");
                    System.out.println("Today Revenue: " + today);
                }
            }

            try (PreparedStatement ps2 = connection.prepareStatement(sqlYesterday)) {
                if (branchId != null) ps2.setInt(1, branchId);
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    yesterday = rs2.getDouble("YesterdayRevenue");
                }
            }

            try (PreparedStatement ps3 = connection.prepareStatement(sqlLastMonthSameDay)) {
                if (branchId != null) ps3.setInt(1, branchId);
                ResultSet rs3 = ps3.executeQuery();
                if (rs3.next()) {
                    lastMonthSameDay = rs3.getDouble("LastMonthSameDayRevenue");
                }
            }

            try (PreparedStatement ps4 = connection.prepareStatement(sqlReturn)) {
                if (branchId != null) ps4.setInt(1, branchId);
                ResultSet rs4 = ps4.executeQuery();
                if (rs4.next()) {
                    returnCount = rs4.getInt("ReturnCount");
                }
            }

            // Lấy các thống kê mới
            try (PreparedStatement ps5 = connection.prepareStatement(sqlTodayOrders)) {
                if (branchId != null) ps5.setInt(1, branchId);
                System.out.println("SQL Today Orders: " + sqlTodayOrders);
                ResultSet rs5 = ps5.executeQuery();
                if (rs5.next()) {
                    todayOrders = rs5.getInt("TodayOrders");
                    System.out.println("Today Orders: " + todayOrders);
                }
            }

            try (PreparedStatement ps6 = connection.prepareStatement(sqlWeekOrders)) {
                if (branchId != null) ps6.setInt(1, branchId);
                System.out.println("SQL Week Orders: " + sqlWeekOrders);
                ResultSet rs6 = ps6.executeQuery();
                if (rs6.next()) {
                    weekOrders = rs6.getInt("WeekOrders");
                    System.out.println("Week Orders: " + weekOrders);
                }
            }

            try (PreparedStatement ps7 = connection.prepareStatement(sqlMonthOrders)) {
                if (branchId != null) ps7.setInt(1, branchId);
                System.out.println("SQL Month Orders: " + sqlMonthOrders);
                ResultSet rs7 = ps7.executeQuery();
                if (rs7.next()) {
                    monthOrders = rs7.getInt("MonthOrders");
                    System.out.println("Month Orders: " + monthOrders);
                }
            }

            try (PreparedStatement ps8 = connection.prepareStatement(sqlWeekRevenue)) {
                if (branchId != null) ps8.setInt(1, branchId);
                System.out.println("SQL Week Revenue: " + sqlWeekRevenue);
                ResultSet rs8 = ps8.executeQuery();
                if (rs8.next()) {
                    weekRevenue = rs8.getDouble("WeekRevenue");
                    System.out.println("Week Revenue: " + weekRevenue);
                }
            }

            try (PreparedStatement ps9 = connection.prepareStatement(sqlMonthRevenue)) {
                if (branchId != null) ps9.setInt(1, branchId);
                System.out.println("SQL Month Revenue: " + sqlMonthRevenue);
                ResultSet rs9 = ps9.executeQuery();
                if (rs9.next()) {
                    monthRevenue = rs9.getDouble("MonthRevenue");
                    System.out.println("Month Revenue: " + monthRevenue);
                }
            }

            double percentYesterday = (yesterday > 0) ? ((today - yesterday) / yesterday * 100) : (today > 0 ? 100 : 0);
            double percentLastMonth = (lastMonthSameDay > 0) ? ((today - lastMonthSameDay) / lastMonthSameDay * 100) : (today > 0 ? 100 : 0);

            stats = new DashboardStatsDTO(today, returnCount, percentYesterday, percentLastMonth);
            stats.setTodayOrders(todayOrders);
            stats.setWeekOrders(weekOrders);
            stats.setMonthOrders(monthOrders);
            stats.setWeekRevenue(weekRevenue);
            stats.setMonthRevenue(monthRevenue);
            
            System.out.println("=== Final Stats ===");
            System.out.println("Today Revenue: " + today);
            System.out.println("Today Orders: " + todayOrders);
            System.out.println("Week Orders: " + weekOrders);
            System.out.println("Month Orders: " + monthOrders);
            System.out.println("Week Revenue: " + weekRevenue);
            System.out.println("Month Revenue: " + monthRevenue);
            System.out.println("===================");
        } catch (SQLException e) {
            System.err.println("ERROR in getDashboardStats: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }

}
