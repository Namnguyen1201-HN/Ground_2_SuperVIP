/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import Model.StockMovementsRequest;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StockMovementsRequestDAO extends DataBaseContext{

//Được sử dụng khi export tạo ra một MovementRequest
public int insertExportMovementRequest(
        String dbName,
        int fromBranchId,
        int toWarehouseId,
        String movementType,
        String note,
        int createdBy
) throws SQLException {
    String sql = """
        INSERT INTO StockMovementsRequest (
            FromSupplierID, FromBranchID, FromWarehouseID, 
            ToBranchID, ToWarehouseID, 
            MovementType, Note, CreatedBy, CreatedAt
        ) VALUES (NULL, ?, NULL, NULL, ?, ?, ?, ?, GETDATE());
    """;

    try (
         PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, fromBranchId);     // FromBranchID
        ps.setInt(2, toWarehouseId);    // ToWarehouseID
        ps.setString(3, movementType);  // "export"
        ps.setString(4, note);
        ps.setInt(5, createdBy);

        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1); // MovementID
            }
        }
    }

    throw new SQLException("Không thể lấy MovementID sau khi insert StockMovementsRequest.");
}



public  void insertMovementResponse(
        String dbName,
        int movementId,
        int userId,
        String status,
        String note
) throws SQLException {
    String sql = """
        INSERT INTO StockMovementResponses (
            MovementID, ResponsedBy, ResponseAt, ResponseStatus, Note
        ) VALUES (?, ?, GETDATE(), ?, ?);
    """;

    try (
         PreparedStatement ps = connection.prepareStatement(sql)) {

        ps.setInt(1, movementId);
        ps.setInt(2, userId);
        ps.setString(3, status);
        if (note != null) {
            ps.setString(4, note);
        } else {
            ps.setNull(4, java.sql.Types.NVARCHAR);
        }

        ps.executeUpdate();
    }
}


}
