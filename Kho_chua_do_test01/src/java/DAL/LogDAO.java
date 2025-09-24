/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;
import Model.Log;
import java.sql.*;
import java.util.*;

/**
 *
 * @author TieuPham
 */
public class LogDAO extends DataBaseContext{
    
    public List<Log> getRecentLogs(int limit) {
        List<Log> list = new ArrayList<>();
        String sql = "SELECT TOP (?) * FROM Logs ORDER BY CreatedAt DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Log log = new Log();
                log.setLogId(rs.getInt("LogId"));
                log.setUserId(rs.getInt("UserId"));
                log.setAction(rs.getString("Action"));
                log.setTableName(rs.getString("TableName"));
                log.setRecordId(rs.getInt("RecordId"));
                log.setCreatedAt(rs.getTimestamp("CreatedAt"));
                list.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
}
