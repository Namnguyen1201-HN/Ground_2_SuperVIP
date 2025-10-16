package DAL;

import Model.OrderDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAO extends DataBaseContext {
    private OrderDetail map(ResultSet rs) throws SQLException {
        OrderDetail d = new OrderDetail();
        d.setOrderDetailId(rs.getInt("OrderDetailID"));
        d.setOrderId(rs.getInt("OrderID"));
        d.setProductDetailId(rs.getInt("ProductDetailID"));
        d.setQuantity(rs.getInt("Quantity"));
        return d;
    }

    public List<OrderDetail> getByOrder(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderDetails WHERE OrderID=? ORDER BY OrderDetailID";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(OrderDetail d) {
        String sql = "INSERT INTO OrderDetails (OrderID, ProductDetailID, Quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, d.getOrderId());
            ps.setInt(2, d.getProductDetailId());
            ps.setInt(3, d.getQuantity());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteByOrder(int orderId) {
        String sql = "DELETE FROM OrderDetails WHERE OrderID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}


