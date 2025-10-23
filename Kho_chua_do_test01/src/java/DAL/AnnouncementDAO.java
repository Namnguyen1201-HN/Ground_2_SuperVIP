package DAL;

import Model.Announcement;
import Model.AnnouncementDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO extends DataBaseContext {

    public List<Announcement> getLatestAnnouncements(int limit) {
        List<Announcement> list = new ArrayList<>();

        String sql = """
            SELECT TOP (?) 
                a.AnnouncementID,
                a.Title,
                a.Description,
                a.CreatedAt,
                u.FullName AS FromUserName
            FROM Announcements a
            LEFT JOIN Users u ON a.FromUserID = u.UserID
            ORDER BY a.CreatedAt DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Announcement a = new Announcement();
                a.setAnnouncementId(rs.getInt("AnnouncementID"));
                a.setTitle(rs.getString("Title"));
                a.setDescription(rs.getString("Description"));
                a.setCreatedAt(rs.getTimestamp("CreatedAt"));
                a.setFromUserName(rs.getString("FromUserName"));
                list.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertAnnouncement(Announcement a) {
        String sql = """
            INSERT INTO Announcements 
            (FromUserID, FromBranchID, FromWarehouseID, ToBranchID, ToWarehouseID, Title, Description, CreatedAt)
            VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, a.getFromUserId());
            ps.setObject(2, a.getFromBranchId(), Types.INTEGER);
            ps.setObject(3, a.getFromWarehouseId(), Types.INTEGER);
            ps.setObject(4, a.getToBranchId(), Types.INTEGER);
            ps.setObject(5, a.getToWarehouseId(), Types.INTEGER);
            ps.setString(6, a.getTitle());
            ps.setString(7, a.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // === 2️⃣ Hoạt động gần đây ===
    public List<AnnouncementDTO> getRecentActivities(int limit, Integer branchId) {
        List<AnnouncementDTO> list = new ArrayList<>();

        String branchFilterOrder = (branchId != null) ? " WHERE o.BranchID = ?" : "";
        String branchFilterStock = (branchId != null) ? " WHERE (smr.FromBranchID = ? OR smr.ToBranchID = ?)" : "";
        String branchFilterCash = (branchId != null) ? " WHERE cf.BranchID = ?" : "";

        String sql = """
        SELECT TOP (?) * FROM (
            -- 1️⃣ Đơn hàng
            SELECT 
                o.OrderID AS AnnouncementID,
                N'Đơn hàng' AS Category,
                o.OrderStatus AS Status,
                CONVERT(NVARCHAR(50), o.GrandTotal) AS Description,
                CONVERT(NVARCHAR(50), o.OrderID) AS RawDescription,
                CONVERT(NVARCHAR(100), u.FullName) AS senderName,
                CONVERT(NVARCHAR(100), b.BranchName) AS locationName,
                CONVERT(NVARCHAR(100), NULL) AS fromLocation,
                CONVERT(NVARCHAR(100), NULL) AS toLocation,
                o.CreatedAt AS createdAt
            FROM Orders o
            JOIN Users u ON u.UserID = o.CreatedBy
            LEFT JOIN Branches b ON b.BranchID = o.BranchID
        """ + branchFilterOrder + """

            UNION ALL

            -- 2️⃣ Nhập/Xuất kho
            SELECT 
                smr.MovementID AS AnnouncementID,
                CASE 
                    WHEN smr.FromSupplierID IS NOT NULL THEN N'Nhập hàng'
                    WHEN smr.ToWarehouseID IS NOT NULL THEN N'Xuất kho'
                    ELSE N'Dịch chuyển kho'
                END AS Category,
                smr.MovementType AS Status,
                ISNULL(smr.Note, '') AS Description,
                CONVERT(NVARCHAR(50), smr.MovementID) AS RawDescription,
                CONVERT(NVARCHAR(100), u.FullName) AS senderName,
                CONVERT(NVARCHAR(100), fb.BranchName) AS locationName,
                CONVERT(NVARCHAR(100), fb.BranchName) AS fromLocation,
                CONVERT(NVARCHAR(100), tb.BranchName) AS toLocation,
                smr.CreatedAt AS createdAt
            FROM StockMovementsRequest smr
            JOIN Users u ON u.UserID = smr.CreatedBy
            LEFT JOIN Branches fb ON fb.BranchID = smr.FromBranchID
            LEFT JOIN Branches tb ON tb.BranchID = smr.ToBranchID
        """ + branchFilterStock + """

            UNION ALL

            -- 3️⃣ Thu / Chi tiền
            SELECT 
                cf.CashFlowID AS AnnouncementID,
                N'Thu/Chi' AS Category,
                cf.FlowType AS Status,
                CONVERT(NVARCHAR(50), cf.Amount) AS Description,
                cf.Description AS RawDescription,
                CONVERT(NVARCHAR(100), cf.CreatedBy) AS senderName,
                CONVERT(NVARCHAR(100), b.BranchName) AS locationName,
                CONVERT(NVARCHAR(100), NULL) AS fromLocation,
                CONVERT(NVARCHAR(100), NULL) AS toLocation,
                cf.CreatedAt AS createdAt
            FROM CashFlows cf
            LEFT JOIN Branches b ON b.BranchID = cf.BranchID
        """ + branchFilterCash + """
        ) AS Combined
        ORDER BY createdAt DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, limit);
            
            if (branchId != null) {
                // Order filter
                ps.setInt(paramIndex++, branchId);
                // Stock movement filter (2 lần vì có FromBranchID và ToBranchID)
                ps.setInt(paramIndex++, branchId);
                ps.setInt(paramIndex++, branchId);
                // Cash flow filter
                ps.setInt(paramIndex++, branchId);
            }
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AnnouncementDTO dto = new AnnouncementDTO();
                dto.setAnnouncementID(rs.getInt("AnnouncementID"));
                dto.setCategory(rs.getString("Category"));
                dto.setStatus(rs.getString("Status"));
                dto.setDescription(rs.getString("Description"));
                dto.setRawDescription(rs.getString("RawDescription"));
                dto.setSenderName(rs.getString("senderName"));
                dto.setLocationName(rs.getString("locationName"));
                dto.setFromLocation(rs.getString("fromLocation"));
                dto.setToLocation(rs.getString("toLocation"));
                dto.setCreatedAt(rs.getTimestamp("createdAt"));
                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

//    public static void main(String[] args) {
//        AnnouncementDAO dao = new AnnouncementDAO();
//
//        System.out.println("=== TEST: getLatestAnnouncements ===");
//        List<Announcement> anns = dao.getLatestAnnouncements(10);
//        System.out.println("Số lượng thông báo: " + anns.size());
//        for (Announcement a : anns) {
//            System.out.println("- [" + a.getCreatedAt() + "] " + a.getTitle() + " | " + a.getDescription());
//        }
//
//        System.out.println("\n=== TEST: getRecentActivities ===");
//        List<AnnouncementDTO> logs = dao.getRecentActivities(10);
//        System.out.println("Số lượng hoạt động gần đây: " + logs.size());
//        for (AnnouncementDTO log : logs) {
//            System.out.println(
//                    String.format("[%s] %s | %s | %s | %s | %s",
//                            new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(log.getCreatedAt()),
//                            log.getCategory(),
//                            log.getStatus(),
//                            log.getSenderName(),
//                            log.getLocationName(),
//                            log.getDescription()
//                    )
//            );
//        }
//    }

}
