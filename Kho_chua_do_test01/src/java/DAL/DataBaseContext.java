package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DataBaseContext {
    public Connection connection;
    private static final Logger LOGGER = Logger.getLogger(DataBaseContext.class.getName());

    public DataBaseContext() {
        try {
            // Database connection information
            String user = "sa";
            String pass = "123"; // CHANGE THIS TO YOUR SQL SERVER PASSWORD
            String url = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;" +
                        "databaseName=swp391;" +
                        "encrypt=false;" +
                        "trustServerCertificate=true;" +
                        "connectionTimeout=30000";
            
            // Load SQL Server JDBC Driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            // Create connection
            connection = DriverManager.getConnection(url, user, pass);
            
            LOGGER.log(Level.INFO, "✅ Connected to SQL Server successfully!");
            System.out.println("✅ Database connection established!");
            
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "❌ SQL Server JDBC Driver not found!");
            System.out.println("❌ Error: SQL Server driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Database connection failed!");
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Unexpected error during database initialization!");
            System.out.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error checking connection status: " + e.getMessage());
            return false;
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.log(Level.INFO, "✅ Connection closed successfully!");
                System.out.println("✅ Database connection closed!");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error closing connection: " + e.getMessage());
            System.out.println("❌ Error closing connection: " + e.getMessage());
        }
    }
    
    public Connection getConnection() {
        return connection;
    }
}
