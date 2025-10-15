package DAL;

import Model.Announcement;
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
}
