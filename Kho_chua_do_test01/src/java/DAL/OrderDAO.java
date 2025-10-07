package DAL;

import Model.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends DataBaseContext {
    public List<Order> getAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders ORDER BY OrderId DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Order getById(int orderId) {
        String sql = "SELECT * FROM Orders WHERE OrderId=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Order o) {
        String sql = "INSERT INTO Orders (SupplierId, OrderedBy, OrderDate, Status) VALUES(?, ?, GETDATE(), ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, o.getSupplierId());
            ps.setInt(2, o.getOrderedBy());
            ps.setString(3, o.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE Orders SET Status=? WHERE OrderId=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int orderId) {
        String delDetails = "DELETE FROM OrderDetails WHERE OrderId=?";
        String delOrder = "DELETE FROM Orders WHERE OrderId=?";
        try (PreparedStatement ps1 = connection.prepareStatement(delDetails);
             PreparedStatement ps2 = connection.prepareStatement(delOrder)) {
            ps1.setInt(1, orderId);
            ps1.executeUpdate();
            ps2.setInt(1, orderId);
            return ps2.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Order map(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("OrderId"));
        o.setSupplierId(rs.getInt("SupplierId"));
        o.setOrderedBy(rs.getInt("OrderedBy"));
        o.setOrderDate(rs.getTimestamp("OrderDate"));
        o.setStatus(rs.getString("Status"));
        return o;
    }
}


