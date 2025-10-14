package DAL;

import Model.Shift;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ShiftDAO extends DataBaseContext {

    // Lấy toàn bộ danh sách ca làm
    public List<Shift> getAll() {
        List<Shift> list = new ArrayList<>();
        String sql = "SELECT * FROM Shift ORDER BY ShiftID ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Shift s = new Shift();
                s.setShiftID(rs.getInt("ShiftID"));
                s.setShiftName(rs.getString("ShiftName"));
                s.setStartTime(rs.getTime("StartTime").toLocalTime());
                s.setEndTime(rs.getTime("EndTime").toLocalTime());
                list.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy ca làm theo ID
    public Shift getById(int id) {
        String sql = "SELECT * FROM Shift WHERE ShiftID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Shift s = new Shift();
                    s.setShiftID(rs.getInt("ShiftID"));
                    s.setShiftName(rs.getString("ShiftName"));
                    s.setStartTime(rs.getTime("StartTime").toLocalTime());
                    s.setEndTime(rs.getTime("EndTime").toLocalTime());
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm ca làm mới
    public void insert(Shift s) {
        String sql = "INSERT INTO Shift (ShiftName, StartTime, EndTime) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, s.getShiftName());
            ps.setTime(2, Time.valueOf(s.getStartTime()));
            ps.setTime(3, Time.valueOf(s.getEndTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật ca làm
    public void update(Shift s) {
        String sql = "UPDATE Shift SET ShiftName=?, StartTime=?, EndTime=? WHERE ShiftID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, s.getShiftName());
            ps.setTime(2, Time.valueOf(s.getStartTime()));
            ps.setTime(3, Time.valueOf(s.getEndTime()));
            ps.setInt(4, s.getShiftID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa ca làm
    public void delete(int id) {
        String sql = "DELETE FROM Shift WHERE ShiftID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- HÀM MAIN TEST DỮ LIỆU ---
    public static void main(String[] args) {
        ShiftDAO dao = new ShiftDAO();
        List<Shift> list = dao.getAll();

        if (list == null || list.isEmpty()) {
            System.out.println("⚠️ Không có dữ liệu nào trong bảng Shift!");
        } else {
            System.out.println("✅ Lấy được " + list.size() + " ca làm việc:");
            for (Shift s : list) {
                System.out.println(
                        "ID: " + s.getShiftID()
                        + " | Tên ca: " + s.getShiftName()
                        + " | Bắt đầu: " + s.getStartTime()
                        + " | Kết thúc: " + s.getEndTime()
                );
            }
        }
    }
}
