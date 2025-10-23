package DAL;

import java.sql.*;
import java.util.*;
import Model.StockMovementDetail;
import Model.ProductDetailSerialNumber;

public class StockMovementDetailDAO extends DataBaseContext {

    public void insertMovementDetail(String dbName, int movementId, int productDetailId, int quantity) throws SQLException {
        String sql = """
            INSERT INTO StockMovementDetail (
            MovementID, ProductDetailID, Quantity, QuantityScanned
        ) VALUES (?, ?, ?, 0);
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            ps.setInt(2, productDetailId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        }
    }

}
