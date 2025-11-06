/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import Model.Product;
import Model.Supplier;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class SupplierDAO extends DataBaseContext {

    public List<Supplier> getAllSuppliers() {
        List<Supplier> data = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierId(rs.getInt("SupplierID"));
                supplier.setSupplierName(rs.getString("SupplierName"));
                supplier.setContactName(rs.getString("ContactName"));
                supplier.setEmail(rs.getString("Email"));
                supplier.setPhone(rs.getString("Phone"));
                supplier.setCreatedAt(rs.getTimestamp("CreatedAt"));
                data.add(supplier);
            }
            System.out.println("✅ [SupplierDAO] Loaded " + data.size() + " suppliers from database");
            return data;
        } catch (Exception e) {
            System.err.println("❌ [SupplierDAO] Error loading suppliers: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int getTotalSuppliers() {
        String sql = "SELECT COUNT(*) FROM Suppliers";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Supplier> getSuppliersPaged(int pageIndex, int pageSize) {
        List<Supplier> data = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers ORDER BY SupplierID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int offset = (pageIndex - 1) * pageSize;
            ps.setInt(1, offset);
            ps.setInt(2, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Supplier supplier = new Supplier();
                    supplier.setSupplierId(rs.getInt("SupplierID"));
                    supplier.setSupplierName(rs.getString("SupplierName"));
                    supplier.setContactName(rs.getString("ContactName"));
                    supplier.setEmail(rs.getString("Email"));
                    supplier.setPhone(rs.getString("Phone"));
                    supplier.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    supplier.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                    data.add(supplier);
                }
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getTotalSuppliersByKeyword(String keyword) {
        String sql = "SELECT COUNT(*) FROM Suppliers WHERE SupplierID LIKE ? OR SupplierName LIKE ? OR ContactName LIKE ? OR Phone LIKE ? OR Email LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, kw);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Supplier> searchSuppliersPaged(String keyword, int pageIndex, int pageSize) {
        List<Supplier> data = new ArrayList<>();
        String sql = """
        SELECT * FROM Suppliers
        WHERE SupplierID LIKE ? OR SupplierName LIKE ? OR ContactName LIKE ?
        ORDER BY SupplierID
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            for (int i = 1; i <= 3; i++) {
                ps.setString(i, kw);
            }
            int offset = (pageIndex - 1) * pageSize;
            ps.setInt(4, offset);
            ps.setInt(5, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Supplier s = new Supplier();
                s.setSupplierId(rs.getInt("SupplierID"));
                s.setSupplierName(rs.getString("SupplierName"));
                s.setContactName(rs.getString("ContactName"));
                s.setEmail(rs.getString("Email"));
                s.setPhone(rs.getString("Phone"));
                s.setCreatedAt(rs.getTimestamp("CreatedAt"));
                data.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Supplier getSupplierById(int supplierId) {
        System.out.println("[DEBUG SupplierDAO] getSupplierById called with ID: " + supplierId);
        String sql = "SELECT * FROM Suppliers WHERE SupplierID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[DEBUG SupplierDAO] Supplier found in database");
                    Supplier supplier = new Supplier();
                    supplier.setSupplierId(rs.getInt("SupplierID"));
                    supplier.setSupplierName(rs.getString("SupplierName"));
                    supplier.setContactName(rs.getString("ContactName"));
                    supplier.setEmail(rs.getString("Email"));
                    supplier.setPhone(rs.getString("Phone"));
                    supplier.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    supplier.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                    System.out.println("[DEBUG SupplierDAO] Mapped supplier - Name: " + supplier.getSupplierName());
                    return supplier;
                } else {
                    System.out.println("[DEBUG SupplierDAO] No supplier found with ID: " + supplierId);
                }
            }
        } catch (Exception e) {
            System.out.println("[DEBUG SupplierDAO] Exception in getSupplierById: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[DEBUG SupplierDAO] Returning null");
        return null;
    }

    public boolean updateSupplier(Supplier supplier) {
        System.out.println("[DEBUG SupplierDAO] updateSupplier called for ID: " + supplier.getSupplierId());
        String sql = """
            UPDATE Suppliers 
            SET SupplierName = ?, ContactName = ?, Email = ?, Phone = ?, UpdatedAt = GETDATE()
            WHERE SupplierID = ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, supplier.getSupplierName());
            ps.setString(2, supplier.getContactName());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getPhone());
            ps.setInt(5, supplier.getSupplierId());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("[DEBUG SupplierDAO] Update result: " + rowsAffected + " rows affected");
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("[DEBUG SupplierDAO] Exception in updateSupplier: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SupplierDAO sd = new SupplierDAO();
        List<Supplier> data = new ArrayList<>();
        data = sd.getAllSuppliers();
        for (int i = 0; i < data.size(); i++) {
            System.out.println(data.get(i).toString());
        }
    }
}
