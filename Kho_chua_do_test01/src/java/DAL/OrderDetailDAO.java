package DAL;

import Model.OrderDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAO extends DataBaseContext {
    public List<OrderDetail> getByOrder(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderDetails WHERE OrderId=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(OrderDetail d) {
        String sql = "INSERT INTO OrderDetails(OrderId, ProductId, Quantity, Price) VALUES(?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, d.getOrderId());
            ps.setInt(2, d.getProductId());
            ps.setInt(3, d.getQuantity());
            ps.setDouble(4, d.getPrice());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean insertReceiptDetail(int receiptId, int productId, int quantity, double unitPrice) {
        String sql = "INSERT INTO ReceiptDetails(ReceiptId, ProductId, Quantity, UnitPrice, Total) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, receiptId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setDouble(4, unitPrice);
            ps.setDouble(5, unitPrice * quantity);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteByOrder(int orderId) {
        String sql = "DELETE FROM OrderDetails WHERE OrderId=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private OrderDetail map(ResultSet rs) throws SQLException {
        OrderDetail d = new OrderDetail();
        d.setOrderDetailId(rs.getInt("OrderDetailId"));
        d.setOrderId(rs.getInt("OrderId"));
        d.setProductId(rs.getInt("ProductId"));
        d.setQuantity(rs.getInt("Quantity"));
        d.setPrice(rs.getDouble("Price"));
        return d;
    }
}


