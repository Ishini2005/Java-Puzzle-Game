package Code;

import java.sql.*;

public class TestMySQLConnection {
    public static void main(String[] args) {
        System.out.println("=== TESTING MYSQL CONNECTION ===\n");

        String url = "jdbc:mysql://localhost:3306/game_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "";

        try {
            // Step 1: Load driver
            System.out.println("1. Loading MySQL driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("   ✅ Driver loaded successfully");

            // Step 2: Try to connect
            System.out.println("\n2. Attempting to connect to MySQL...");
            System.out.println("   URL: " + url);
            System.out.println("   User: " + user);

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("   ✅ CONNECTION SUCCESSFUL!");

            // Step 3: Check database
            System.out.println("\n3. Checking database...");
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("   Database: " + conn.getCatalog());
            System.out.println("   MySQL Version: " + meta.getDatabaseProductVersion());

            // Step 4: Check tables
            System.out.println("\n4. Checking tables...");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            System.out.println("   Tables in game_db:");
            boolean hasTables = false;
            while (rs.next()) {
                System.out.println("   - " + rs.getString(1));
                hasTables = true;
            }
            if (!hasTables) {
                System.out.println("   (no tables found)");
            }

            conn.close();
            System.out.println("\n✅ All tests passed!");

        } catch (ClassNotFoundException e) {
            System.out.println("\n❌ ERROR: MySQL JDBC Driver not found!");
            System.out.println("\n📌 FIX: Add MySQL Connector JAR to your project:");
            System.out.println("   1. Download from: https://dev.mysql.com/downloads/connector/j/");
            System.out.println("   2. In IntelliJ: File → Project Structure → Libraries");
            System.out.println("   3. Click + → Java → Select the JAR file");

        } catch (SQLException e) {
            System.out.println("\n❌ ERROR: " + e.getMessage());
            System.out.println("\n📌 Error Code: " + e.getErrorCode());
            System.out.println("📌 SQL State: " + e.getSQLState());

            if (e.getMessage().contains("Access denied")) {
                System.out.println("\n🔑 FIX: Wrong username or password");
                System.out.println("   Current user: '" + user + "'");
                System.out.println("   Try: user='root', password='' (empty)");
            }
            else if (e.getMessage().contains("Unknown database")) {
                System.out.println("\n🗄️ FIX: Database 'game_db' doesn't exist");
                System.out.println("   Create it in phpMyAdmin with:");
                System.out.println("   CREATE DATABASE game_db;");
            }
            else if (e.getMessage().contains("Connection refused")) {
                System.out.println("\n🔄 FIX: MySQL is not running!");
                System.out.println("   1. Open XAMPP Control Panel");
                System.out.println("   2. Click 'Start' next to MySQL");
                System.out.println("   3. Wait for it to turn green");
            }
            else if (e.getMessage().contains("Communications link failure")) {
                System.out.println("\n🌐 FIX: Check if MySQL is running on port 3306");
                System.out.println("   Run in CMD: netstat -ano | findstr :3306");
            }
        }
    }
}