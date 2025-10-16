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

    public PagedOrders search(Integer branchId, String status, String keyword,
            String customerName, String productName,
            java.sql.Timestamp fromDate, java.sql.Timestamp toDate,
            int page, int pageSize) {
        List<Order> list = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        if (branchId != null) {
            where.append(" AND BranchID = ?");
            params.add(branchId);
        }
        if (status != null && !status.isBlank()) {
            where.append(" AND OrderStatus = ?");
            params.add(status);
        }
        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND (CAST(OrderID AS NVARCHAR(20)) LIKE ? OR PaymentMethod LIKE ? OR Notes LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        if (customerName != null && !customerName.isBlank()) {
            where.append(" AND EXISTS (SELECT 1 FROM Customers c WHERE c.CustomerID = Orders.CustomerID AND c.FullName LIKE ?) ");
            params.add("%" + customerName.trim() + "%");
        }
        if (productName != null && !productName.isBlank()) {
            where.append(" AND EXISTS (SELECT 1 FROM OrderDetails od JOIN ProductDetails pd ON pd.ProductDetailID = od.ProductDetailID JOIN Products p ON p.ProductID = pd.ProductID WHERE od.OrderID = Orders.OrderID AND p.ProductName LIKE ?) ");
            params.add("%" + productName.trim() + "%");
        }
        if (fromDate != null) {
            where.append(" AND CreatedAt >= ?");
            params.add(fromDate);
        }
        if (toDate != null) {
            where.append(" AND CreatedAt <= ?");
            params.add(toDate);
        }

        int offset = Math.max(0, (page - 1) * pageSize);

        String sql = "SELECT * FROM Orders" + where + " ORDER BY OrderID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        String countSql = "SELECT COUNT(*) FROM Orders" + where;
        int total = 0;
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
    public List<RevenueStatisticDTO> getRevenueByDay(String period) {
        List<RevenueStatisticDTO> list = new ArrayList<>();
        String sql = """
            SELECT CONVERT(VARCHAR(10), CreatedAt, 23) AS Label, SUM(GrandTotal) AS Total
            FROM Orders
            WHERE 
                OrderStatus = N'Hoàn thành' AND
                CreatedAt >= DATEADD(MONTH, CASE WHEN ? = 'this_month' THEN 0 ELSE -1 END, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
                AND CreatedAt < DATEADD(MONTH, CASE WHEN ? = 'this_month' THEN 1 ELSE 0 END, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
            GROUP BY CONVERT(VARCHAR(10), CreatedAt, 23)
            ORDER BY Label;
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, period);
            ps.setString(2, period);
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
    public List<RevenueStatisticDTO> getRevenueByHour(String period) {
        List<RevenueStatisticDTO> list = new ArrayList<>();
        String sql = """
            SELECT DATEPART(HOUR, CreatedAt) AS HourLabel, SUM(GrandTotal) AS Total
            FROM Orders
            WHERE 
                OrderStatus = N'Hoàn thành' AND
                CreatedAt >= DATEADD(MONTH, CASE WHEN ? = 'this_month' THEN 0 ELSE -1 END, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
                AND CreatedAt < DATEADD(MONTH, CASE WHEN ? = 'this_month' THEN 1 ELSE 0 END, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
            GROUP BY DATEPART(HOUR, CreatedAt)
            ORDER BY HourLabel;
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, period);
            ps.setString(2, period);
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
    public List<RevenueStatisticDTO> getRevenueByWeekday(String period) {
        List<RevenueStatisticDTO> list = new ArrayList<>();
        String sql = """
            SELECT DATENAME(WEEKDAY, CreatedAt) AS DayLabel, SUM(GrandTotal) AS Total
            FROM Orders
            WHERE 
                OrderStatus = N'Hoàn thành' AND
                CreatedAt >= DATEADD(MONTH, CASE WHEN ? = 'this_month' THEN 0 ELSE -1 END, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
                AND CreatedAt < DATEADD(MONTH, CASE WHEN ? = 'this_month' THEN 1 ELSE 0 END, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
            GROUP BY DATENAME(WEEKDAY, CreatedAt), DATEPART(WEEKDAY, CreatedAt)
            ORDER BY DATEPART(WEEKDAY, CreatedAt);
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, period);
            ps.setString(2, period);
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
    public List<RevenueStatisticDTO> getRevenueLast3Months() {
        List<RevenueStatisticDTO> list = new ArrayList<>();
        String sql = """
            SELECT FORMAT(CreatedAt, 'MM/yyyy') AS MonthLabel, SUM(GrandTotal) AS Total
            FROM Orders
            WHERE 
                OrderStatus = N'Hoàn thành' AND
                CreatedAt >= DATEADD(MONTH, -2, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
            GROUP BY FORMAT(CreatedAt, 'MM/yyyy'), YEAR(CreatedAt), MONTH(CreatedAt)
            ORDER BY YEAR(CreatedAt), MONTH(CreatedAt);
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RevenueStatisticDTO(rs.getString("MonthLabel"), rs.getDouble("Total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        String sqlToday = """
            SELECT ISNULL(SUM(GrandTotal), 0) AS TodayRevenue
            FROM Orders
            WHERE CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE)
              AND OrderStatus = N'Hoàn thành'
        """;

        String sqlYesterday = """
            SELECT ISNULL(SUM(GrandTotal), 0) AS YesterdayRevenue
            FROM Orders
            WHERE CAST(CreatedAt AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE)
              AND OrderStatus = N'Hoàn thành'
        """;

        String sqlLastMonthSameDay = """
            SELECT ISNULL(SUM(GrandTotal), 0) AS LastMonthSameDayRevenue
            FROM Orders
            WHERE 
                OrderStatus = N'Hoàn thành'
                AND DAY(CreatedAt) = DAY(GETDATE())
                AND MONTH(CreatedAt) = MONTH(DATEADD(MONTH, -1, GETDATE()))
                AND YEAR(CreatedAt) = YEAR(DATEADD(MONTH, -1, GETDATE()))
        """;

        String sqlReturn = """
            SELECT COUNT(*) AS ReturnCount
            FROM Orders
            WHERE CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE)
              AND OrderStatus = N'Trả hàng'
        """;

        try (
                Statement st1 = connection.createStatement(); Statement st2 = connection.createStatement(); Statement st3 = connection.createStatement(); Statement st4 = connection.createStatement()) {
            double today = 0, yesterday = 0, lastMonthSameDay = 0;
            int returnCount = 0;

            ResultSet rs1 = st1.executeQuery(sqlToday);
            if (rs1.next()) {
                today = rs1.getDouble("TodayRevenue");
            }

            ResultSet rs2 = st2.executeQuery(sqlYesterday);
            if (rs2.next()) {
                yesterday = rs2.getDouble("YesterdayRevenue");
            }

            ResultSet rs3 = st3.executeQuery(sqlLastMonthSameDay);
            if (rs3.next()) {
                lastMonthSameDay = rs3.getDouble("LastMonthSameDayRevenue");
            }

            ResultSet rs4 = st4.executeQuery(sqlReturn);
            if (rs4.next()) {
                returnCount = rs4.getInt("ReturnCount");
            }

            double percentYesterday = (yesterday > 0) ? ((today - yesterday) / yesterday * 100) : (today > 0 ? 100 : 0);
            double percentLastMonth = (lastMonthSameDay > 0) ? ((today - lastMonthSameDay) / lastMonthSameDay * 100) : (today > 0 ? 100 : 0);

            stats = new DashboardStatsDTO(today, returnCount, percentYesterday, percentLastMonth);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

}
