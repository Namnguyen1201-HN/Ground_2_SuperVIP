package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseContext {
    protected Connection connection;

    public DataBaseContext() {
        try {
            String user = "sa";
            String pass = "123"; // thay đúng mật khẩu SQL Server của bạn
            String url = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=Lan1;encrypt=false";
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("✅ Kết nối thành công tới SQL Server!");
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("❌ Kết nối thất bại!");
            ex.printStackTrace();
            Logger.getLogger(DataBaseContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
