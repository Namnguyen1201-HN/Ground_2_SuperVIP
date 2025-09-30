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
        try ( PreparedStatement ps = connection.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierId(rs.getInt("SupplierId"));
                supplier.setSupplierName(rs.getString("SupplierName"));
                supplier.setContactName(rs.getString("ContactName"));
                supplier.setEmail(rs.getString("Email"));
                supplier.setPhone(rs.getString("Phone"));
                supplier.setAddress(rs.getString("Address"));
                supplier.setCreatedAt(rs.getTimestamp("CreatedAt"));
                data.add(supplier);
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getTotalSuppliers() {
        String sql = "SELECT COUNT(*) FROM Suppliers";
        try ( PreparedStatement ps = connection.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
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
        String sql = "SELECT * FROM Suppliers ORDER BY SupplierId OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try ( PreparedStatement ps = connection.prepareStatement(sql)) {
            int offset = (pageIndex - 1) * pageSize;
            ps.setInt(1, offset);
            ps.setInt(2, pageSize);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Supplier supplier = new Supplier();
                    supplier.setSupplierId(rs.getInt("SupplierId"));
                    supplier.setSupplierName(rs.getString("SupplierName"));
                    supplier.setContactName(rs.getString("ContactName"));
                    supplier.setEmail(rs.getString("Email"));
                    supplier.setPhone(rs.getString("Phone"));
                    supplier.setAddress(rs.getString("Address"));
                    supplier.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    data.add(supplier);
                }
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Supplier getSupplierById(int supplierId) {
        String sql = "SELECT * FROM Suppliers WHERE SupplierId = ?";
        try ( PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Supplier supplier = new Supplier();
                    supplier.setSupplierId(rs.getInt("SupplierId"));
                    supplier.setSupplierName(rs.getString("SupplierName"));
                    supplier.setContactName(rs.getString("ContactName"));
                    supplier.setEmail(rs.getString("Email"));
                    supplier.setPhone(rs.getString("Phone"));
                    supplier.setAddress(rs.getString("Address"));
                    supplier.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    Product product = (Product) getAllProductsBySupplierId(supplierId);
                    supplier.setProduct(product);
                    return supplier;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> getAllProductsBySupplierId(int supplierId) {
        List<Product> data = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE supplierId = ?";
        try ( PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setProductId(rs.getInt("ProductId"));
                    product.setProductName(rs.getString("ProductName"));
                    product.setCategoryId(rs.getInt("CategoryId"));
                    product.setSupplierId(rs.getInt("SupplierId"));
                    product.setPrice(rs.getDouble("Price"));
                    product.setQuantity(rs.getInt("Quantity"));
                    product.setExpiryDate(rs.getDate("ExpiryDate"));
                    product.setCreatedAt(rs.getDate("CreateAt"));
                    product.setUpdatedAt(rs.getDate("UpdateAt"));
                    data.add(product);
                }
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
