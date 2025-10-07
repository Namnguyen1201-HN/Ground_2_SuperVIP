package DAL;

import Model.Receipt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiptDAO extends DataBaseContext {
    public List<Receipt> getAll() {
        List<Receipt> list = new ArrayList<>();
        String sql = "SELECT * FROM Receipts ORDER BY ReceiptId DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Receipt getById(int id) {
        String sql = "SELECT * FROM Receipts WHERE ReceiptId=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Receipt map(ResultSet rs) throws SQLException {
        Receipt r = new Receipt();
        r.setReceiptId(rs.getInt("ReceiptId"));
        r.setCustomerName(rs.getString("CustomerName"));
        r.setCreatedBy(rs.getInt("CreatedBy"));
        r.setCreatedAt(rs.getTimestamp("CreatedAt"));
        r.setStatus(rs.getString("Status"));
        r.setSubTotal(rs.getDouble("SubTotal"));
        r.setDiscount(rs.getDouble("Discount"));
        r.setTax(rs.getDouble("Tax"));
        r.setGrandTotal(rs.getDouble("GrandTotal"));
        return r;
    }

    public int insert(Receipt r) {
        String sql = "INSERT INTO Receipts (CustomerName, CreatedBy, CreatedAt, Status, SubTotal, Discount, Tax, GrandTotal) "
                + "VALUES (?, ?, GETDATE(), ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getCustomerName());
            ps.setInt(2, r.getCreatedBy());
            ps.setString(3, r.getStatus());
            ps.setDouble(4, r.getSubTotal());
            ps.setDouble(5, r.getDiscount());
            ps.setDouble(6, r.getTax());
            ps.setDouble(7, r.getGrandTotal());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
}


