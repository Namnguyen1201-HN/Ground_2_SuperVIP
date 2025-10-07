package DAL;

import Model.ReceiptDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiptDetailDAO extends DataBaseContext {
    public List<ReceiptDetail> getByReceipt(int receiptId) {
        List<ReceiptDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM ReceiptDetails WHERE ReceiptId=? ORDER BY ReceiptDetailId DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, receiptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private ReceiptDetail map(ResultSet rs) throws SQLException {
        ReceiptDetail d = new ReceiptDetail();
        d.setReceiptDetailId(rs.getInt("ReceiptDetailId"));
        d.setReceiptId(rs.getInt("ReceiptId"));
        d.setProductId(rs.getInt("ProductId"));
        d.setQuantity(rs.getInt("Quantity"));
        d.setUnitPrice(rs.getDouble("UnitPrice"));
        d.setTotal(rs.getDouble("Total"));
        return d;
    }
}


