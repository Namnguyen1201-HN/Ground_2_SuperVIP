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

    public boolean deleteBranch(int id) {
        String sql = "DELETE FROM Branches WHERE BranchID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBranch(Branch b) {
        String sql = "UPDATE Branches SET BranchName=?, Address=?, Phone=?, IsActive=? WHERE BranchID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, b.getBranchName());
            ps.setString(2, b.getAddress());
            ps.setString(3, b.getPhone());
            ps.setBoolean(4, b.isActive());
            ps.setInt(5, b.getBranchId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertBranch(Branch b) {
        String sql = "INSERT INTO Branches (BranchName, Address, Phone, IsActive) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, b.getBranchName());
            ps.setString(2, b.getAddress());
            ps.setString(3, b.getPhone());
            ps.setBoolean(4, true); // mặc định chi nhánh mới hoạt động
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
}
