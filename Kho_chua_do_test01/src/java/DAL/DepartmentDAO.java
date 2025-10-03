package DAL;

import Model.Department;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO extends DataBaseContext {

    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT DepartmentId, DepartmentName, BranchId, CreatedAt FROM Departments";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Department d = new Department(
                        rs.getInt("DepartmentId"),
                        rs.getString("DepartmentName"),
                        rs.getInt("BranchId"),
                        rs.getTimestamp("CreatedAt")
                );
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Department getDepartmentById(int id) {
        String sql = "SELECT DepartmentId, DepartmentName, BranchId, CreatedAt FROM Departments WHERE DepartmentId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Department(
                        rs.getInt("DepartmentId"),
                        rs.getString("DepartmentName"),
                        rs.getInt("BranchId"),
                        rs.getTimestamp("CreatedAt")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
