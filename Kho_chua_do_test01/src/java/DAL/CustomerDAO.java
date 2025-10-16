package DAL;

import Model.Customer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer Data Access Object Lấy danh sách customer theo user hoặc branch
 */
public class CustomerDAO extends DataBaseContext {

    /**
     * Lấy danh sách toàn bộ khách hàng thuộc branch của user đăng nhập
     */
    public List<Customer> getCustomersByBranchId(int branchId) {
        List<Customer> list = new ArrayList<>();
        String sql = """
            SELECT c.*
            FROM Customers c
            INNER JOIN Branches b ON c.BranchId = b.BranchId
            WHERE b.BranchId = ?
            ORDER BY c.CreatedAt DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy danh sách khách hàng có phân trang
     */
    public List<Customer> getCustomersByBranchPaged(int branchId, int pageIndex, int pageSize) {
        List<Customer> list = new ArrayList<>();
        String sql = """
            SELECT c.*
            FROM Customers c
            INNER JOIN Branches b ON c.BranchId = b.BranchId
            WHERE b.BranchId = ?
            ORDER BY c.CreatedAt DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, (pageIndex - 1) * pageSize);
            ps.setInt(3, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm tổng số customer thuộc branch
     */
    public int countCustomersByBranchId(int branchId) {
        String sql = "SELECT COUNT(*) FROM Customers WHERE BranchId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy thông tin chi tiết 1 customer theo ID
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM Customers WHERE CustomerID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Thêm mới customer
     */
    public boolean insertCustomer(Customer c, int branchId) {
        String sql = """
            INSERT INTO Customers (FullName, PhoneNumber, Email, Address, Gender, DateOfBirth, CreatedAt, UpdatedAt, BranchId)
            VALUES (?, ?, ?, ?, ?, ?, GETDATE(), GETDATE(), ?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getFullname());
            ps.setString(2, c.getPhoneNumber());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getAddress());
            ps.setBoolean(5, c.getGender() != null ? c.getGender() : false);
            if (c.getDateOfBirth() != null) {
                ps.setDate(6, new java.sql.Date(c.getDateOfBirth().getTime()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            ps.setInt(7, branchId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật customer
     */
    public boolean updateCustomer(Customer c) {
        String sql = """
            UPDATE Customers
            SET FullName = ?, PhoneNumber = ?, Email = ?, Address = ?, Gender = ?, DateOfBirth = ?, UpdatedAt = GETDATE()
            WHERE CustomerID = ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getFullname());
            ps.setString(2, c.getPhoneNumber());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getAddress());
            ps.setBoolean(5, c.getGender() != null ? c.getGender() : false);
            if (c.getDateOfBirth() != null) {
                ps.setDate(6, new java.sql.Date(c.getDateOfBirth().getTime()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            ps.setInt(7, c.getCustomerID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa customer
     */
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM Customers WHERE CustomerID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Helper: Map dữ liệu ResultSet sang Customer object
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerID(rs.getInt("CustomerID"));
        c.setFullname(rs.getString("FullName"));
        c.setPhoneNumber(rs.getString("PhoneNumber"));
        c.setEmail(rs.getString("Email"));
        c.setAddress(rs.getString("Address"));
        c.setGender(rs.getBoolean("Gender"));
        c.setDateOfBirth(rs.getDate("DateOfBirth"));
        c.setCreatedAt(rs.getTimestamp("CreatedAt"));
        c.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        return c;
    }

    public List<Customer> searchAndFilterCustomers(String keyword, String gender,
            Integer branchId, Double minSpent, Double maxSpent) {
        List<Customer> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("""
        SELECT t.CustomerID, t.CustomerName, t.Phone, t.Gender, t.Address, t.TotalSpent
        FROM (
            SELECT 
                c.CustomerID,
                c.FullName AS CustomerName,
                c.PhoneNumber AS Phone,
                c.Gender,
                c.Address,
                COALESCE(SUM(o.GrandTotal), 0) AS TotalSpent,
                SUM(CASE WHEN o.BranchID = ? THEN 1 ELSE 0 END) AS OrdersInBranch
            FROM Customers c
            LEFT JOIN Orders o ON c.CustomerID = o.CustomerID
            GROUP BY c.CustomerID, c.FullName, c.PhoneNumber, c.Gender, c.Address
        ) t
        WHERE 1=1
    """);

        // Nếu branchId != 0 thì chỉ lấy khách có đơn ở chi nhánh đó
        if (branchId != null && branchId != 0) {
            sql.append(" AND t.OrdersInBranch > 0 ");
        }

        // Keyword (áp dụng trên các trường non-aggregate + TotalSpent)
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
            AND (
                CAST(t.CustomerID AS NVARCHAR(50)) LIKE ? OR
                t.CustomerName LIKE ? OR
                t.Phone LIKE ? OR
                t.Address LIKE ? OR
                CAST(t.TotalSpent AS NVARCHAR(50)) LIKE ?
            )
        """);
        }

        // Gender
        if (gender != null && !gender.equalsIgnoreCase("all")) {
            sql.append(" AND t.Gender = ? ");
        }

        // Min / Max spent (đã là giá trị aggregated)
        if (minSpent != null) {
            sql.append(" AND t.TotalSpent >= ? ");
        }
        if (maxSpent != null) {
            sql.append(" AND t.TotalSpent <= ? ");
        }

        sql.append(" ORDER BY t.TotalSpent DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int index = 1;

            // 1) Param cho SUM(CASE WHEN o.BranchID = ? ... ) — luôn bind (nếu branchId == null thì bind 0)
            ps.setInt(index++, (branchId == null) ? 0 : branchId);

            // 2) Keyword params (nếu có)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(index++, kw); // CustomerID cast
                ps.setString(index++, kw); // CustomerName
                ps.setString(index++, kw); // Phone
                ps.setString(index++, kw); // Address
                ps.setString(index++, kw); // TotalSpent cast
            }

            // 3) Gender
            if (gender != null && !gender.equalsIgnoreCase("all")) {
                // map "male"/"nam" -> true, "female"/"nu"/"nữ" -> false
                boolean genderBit = gender.equalsIgnoreCase("male")
                        || gender.equalsIgnoreCase("nam")
                        || gender.equalsIgnoreCase("m");
                ps.setBoolean(index++, genderBit);
            }

            // 4) Min / Max spent
            if (minSpent != null) {
                ps.setDouble(index++, minSpent);
            }
            if (maxSpent != null) {
                ps.setDouble(index++, maxSpent);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Customer c = new Customer();
                    c.setCustomerID(rs.getInt("CustomerID"));
                    c.setFullname(rs.getString("CustomerName"));
                    c.setPhoneNumber(rs.getString("Phone"));
                    c.setGender(rs.getBoolean("Gender"));
                    c.setAddress(rs.getString("Address"));
                    c.setTotalSpent(rs.getDouble("TotalSpent"));
                    list.add(c);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getTotalCustomers() {
        String sql = "SELECT COUNT(*) FROM Customers";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Customer> getAllCustomersPaged(int page, int pageSize) {
        List<Customer> list = new ArrayList<>();
        String sql = """
        SELECT t.CustomerID, t.FullName, t.PhoneNumber, t.Gender, t.Address, t.TotalSpent
        FROM (
            SELECT 
                c.CustomerID,
                c.FullName,
                c.PhoneNumber,
                c.Gender,
                c.Address,
                COALESCE(SUM(o.GrandTotal), 0) AS TotalSpent
            FROM Customers c
            LEFT JOIN Orders o ON c.CustomerID = o.CustomerID
            GROUP BY c.CustomerID, c.FullName, c.PhoneNumber, c.Gender, c.Address
        ) t
        ORDER BY t.CustomerID
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Customer c = new Customer();
                    c.setCustomerID(rs.getInt("CustomerID"));
                    c.setFullname(rs.getString("FullName"));
                    c.setPhoneNumber(rs.getString("PhoneNumber"));
                    c.setGender(rs.getBoolean("Gender"));
                    c.setAddress(rs.getString("Address"));
                    c.setTotalSpent(rs.getDouble("TotalSpent"));
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        List<Customer> data = new ArrayList();
        CustomerDAO cd = new CustomerDAO();
        data = cd.searchAndFilterCustomers("Nguyen", "female", 3, 500000.0, 5000000.0);
        System.out.println(data.size());
    }

}
