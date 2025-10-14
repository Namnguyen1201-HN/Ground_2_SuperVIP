package DAL;

import Model.Branch;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO extends DataBaseContext {

    public List<Branch> getAllBranches() {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT BranchID, BranchName, Address, Phone, IsActive FROM Branches";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Branch b = new Branch();
                b.setBranchId(rs.getInt("BranchID"));
                b.setBranchName(rs.getString("BranchName"));
                b.setAddress(rs.getString("Address"));
                b.setPhone(rs.getString("Phone"));
                b.setActive(rs.getBoolean("IsActive"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // không biết của ai nên chưa sửa sau khi thay Database mới
    public Branch getBranchById(int id) {
        String sql = "SELECT BranchId, BranchName, Location, CreatedAt FROM CompanyBranches WHERE BranchId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Branch(
                        rs.getInt("BranchId"),
                        rs.getString("BranchName"),
                        rs.getString("Location"),
                        rs.getTimestamp("CreatedAt")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
