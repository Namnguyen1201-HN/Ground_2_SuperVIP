/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import DAL.DataBaseContext;
import Model.StockMovementResponse;
import java.sql.*;
import java.util.*;

/**
 *
 * @author TieuPham
 */
public class StockMovementResponseDAO extends DataBaseContext {

    /**
     * 🔹 Lấy phản hồi theo movementId
     */
    public StockMovementResponse getResponseByMovementId(int movementId) {
        String sql = """
            SELECT ResponseID, MovementID, ResponsedBy, ResponseAt, ResponseStatus, Note
            FROM StockMovementResponse
            WHERE MovementID = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StockMovementResponse response = new StockMovementResponse();
                    response.setResponseId(rs.getInt("ResponseID"));
                    response.setMovementId(rs.getInt("MovementID"));
                    response.setResponsedBy(rs.getInt("ResponsedBy"));

                    Timestamp ts = rs.getTimestamp("ResponseAt");
                    if (ts != null) {
                        response.setResponseAt(new java.util.Date(ts.getTime()));
                    }

                    response.setResponseStatus(rs.getString("ResponseStatus"));
                    response.setNote(rs.getString("Note"));

                    return response;
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi lấy phản hồi theo movementId: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 🔹 Thêm phản hồi mới từ kho (khi xử lý đơn lần đầu)
     */
    public boolean insertResponse(StockMovementResponse response) {
        String sql = """
            INSERT INTO StockMovementResponse (MovementID, ResponsedBy, ResponseAt, ResponseStatus, Note)
            VALUES (?, ?, GETDATE(), ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, response.getMovementId());
            ps.setInt(2, response.getResponsedBy());
            ps.setString(3, response.getResponseStatus());
            ps.setString(4, response.getNote());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi thêm phản hồi mới: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 🔹 Cập nhật trạng thái và ghi chú phản hồi (khi kho xử lý tiếp)
     */
    public boolean updateResponseStatus(int movementId, String responseStatus, String note, int responsedBy) {
        String sql = """
            UPDATE StockMovementResponse
            SET ResponseStatus = ?, Note = ?, ResponseAt = GETDATE(), ResponsedBy = ?
            WHERE MovementID = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, responseStatus);
            ps.setString(2, note);
            ps.setInt(3, responsedBy);
            ps.setInt(4, movementId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi cập nhật phản hồi: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 🔹 Xóa phản hồi theo MovementID (dùng khi xóa phiếu nhập/xuất)
     */
    public boolean deleteResponseByMovementId(int movementId) {
        String sql = "DELETE FROM StockMovementResponse WHERE MovementID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi xóa phản hồi theo MovementID: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 🔹 Lấy toàn bộ phản hồi (cho mục debug hoặc quản trị)
     */
    public List<StockMovementResponse> getAllResponses() {
        List<StockMovementResponse> list = new ArrayList<>();

        String sql = """
            SELECT ResponseID, MovementID, ResponsedBy, ResponseAt, ResponseStatus, Note
            FROM StockMovementResponse
            ORDER BY ResponseAt DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StockMovementResponse r = new StockMovementResponse();
                r.setResponseId(rs.getInt("ResponseID"));
                r.setMovementId(rs.getInt("MovementID"));
                r.setResponsedBy(rs.getInt("ResponsedBy"));

                Timestamp ts = rs.getTimestamp("ResponseAt");
                if (ts != null) {
                    r.setResponseAt(new java.util.Date(ts.getTime()));
                }

                r.setResponseStatus(rs.getString("ResponseStatus"));
                r.setNote(rs.getString("Note"));

                list.add(r);
            }

        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi lấy toàn bộ phản hồi: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    // 🔹 Thêm phản hồi (response) mới khi kho hoàn tất đơn nhập hàng
    public boolean insertStockMovementResponse(StockMovementResponse response) {
        String sql = """
            INSERT INTO StockMovementResponses 
            (MovementID, ResponsedBy, ResponseAt, ResponseStatus, Note)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, response.getMovementId());
            ps.setInt(2, response.getResponsedBy());
            ps.setTimestamp(3, new Timestamp(response.getResponseAt().getTime()));
            ps.setString(4, response.getResponseStatus());
            ps.setString(5, response.getNote());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi thêm phản hồi StockMovementResponse: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy trạng thái mới nhất của 1 Movement, ví dụ: "processing", "completed",
     * hoặc null nếu chưa có.
     */
    public String getLatestStatusByMovementId(int movementId) {
        final String sql
                = "SELECT TOP 1 ResponseStatus "
                + "FROM StockMovementResponses "
                + "WHERE MovementID = ? "
                + "ORDER BY ResponseAt DESC, ResponseID DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kiểm tra đã completed chưa
    public boolean hasCompletedStatus(int movementId) {
        final String sql
                = "SELECT 1 FROM StockMovementResponses WHERE MovementID = ? AND ResponseStatus = 'completed'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, movementId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
