package Code.database;

import Code.banana.engine.UserScore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {
    private Connection connection;
    private boolean connected = false;

    public DataBaseManager() {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL Driver loaded");

            // Try to connect - UPDATE THESE CREDENTIALS!
            String url = "jdbc:mysql://localhost:3306/game_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            String user = "root";  // XAMPP default username
            String password = "";   // XAMPP default password (empty)

            connection = DriverManager.getConnection(url, user, password);
            connected = true;
            System.out.println("✅ Connected to MySQL database");

            // Create tables if they don't exist
            createTablesIfNotExist();

        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found!");
            System.out.println("Please add mysql-connector-java JAR to project libraries.");
            e.printStackTrace();
            connected = false;
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to MySQL database!");
            System.out.println("Error: " + e.getMessage());
            System.out.println("\nPlease check:");
            System.out.println("1. Is MySQL running? (Start XAMPP MySQL)");
            System.out.println("2. Is database 'game_db' created?");
            System.out.println("3. Are username/password correct? (default: root/empty)");
            e.printStackTrace();
            connected = false;
        }
    }

    public boolean isConnected() {
        return connected && connection != null;
    }

    private void createTablesIfNotExist() {
        if (!isConnected()) return;

        try (Statement stmt = connection.createStatement()) {
            // Create users table if not exists
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            stmt.executeUpdate(createUsersTable);

            // Create scores table if not exists
            String createScoresTable = "CREATE TABLE IF NOT EXISTS scores (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL, " +
                    "score INT NOT NULL, " +
                    "time_taken INT, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE)";
            stmt.executeUpdate(createScoresTable);

            System.out.println("✅ Database tables verified");

        } catch (SQLException e) {
            System.out.println("❌ Error creating tables: " + e.getMessage());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean userExists(String username) {
        if (!isConnected()) return false;

        String sql = "SELECT username FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username.trim());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("❌ Error checking user: " + e.getMessage());
            return false;
        }
    }

    public boolean register(String username, String password) {
        if (!isConnected()) {
            System.out.println("❌ No database connection");
            return false;
        }

        if (username == null || username.trim().isEmpty()) {
            System.out.println("❌ Username cannot be empty");
            return false;
        }

        if (password == null || password.length() < 4) {
            System.out.println("❌ Password must be at least 4 characters");
            return false;
        }

        username = username.trim();

        // Check if user exists
        if (userExists(username)) {
            System.out.println("❌ Username already exists: " + username);
            return false;
        }

        String hashedPassword = hashPassword(password);
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ User registered: " + username);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error registering: " + e.getMessage());
        }
        return false;
    }

    public boolean login(String username, String password) {
        if (!isConnected()) return false;

        if (username == null || username.trim().isEmpty() || password == null) {
            return false;
        }

        username = username.trim();
        String hashedPassword = hashPassword(password);
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✅ Login successful: " + username);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Login error: " + e.getMessage());
        }
        return false;
    }

    public void saveScore(String username, int score) {
        if (!isConnected()) return;

        String query = "INSERT INTO scores (username, score, timestamp) VALUES (?, ?, NOW())";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setInt(2, score);
            stmt.executeUpdate();
            System.out.println("✅ Score saved for " + username + ": " + score);
        } catch (SQLException e) {
            System.out.println("❌ Error saving score: " + e.getMessage());
        }
    }

    public void saveScore(String username, int score, int timeTaken) {
        if (!isConnected()) return;

        String query = "INSERT INTO scores (username, score, time_taken, timestamp) VALUES (?, ?, ?, NOW())";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setInt(2, score);
            stmt.setInt(3, timeTaken);
            stmt.executeUpdate();
            System.out.println("✅ Score saved for " + username + " - Score: " + score + ", Time: " + timeTaken + "s");
        } catch (SQLException e) {
            System.out.println("❌ Error saving score: " + e.getMessage());
        }
    }

    public List<UserScore> getTopScoresByTime() {
        List<UserScore> topScores = new ArrayList<>();
        if (!isConnected()) return topScores;

        String query = "SELECT username, time_taken FROM scores WHERE time_taken IS NOT NULL ORDER BY time_taken ASC LIMIT 10";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String username = rs.getString("username");
                int timeTaken = rs.getInt("time_taken");
                topScores.add(new UserScore(username, 0, timeTaken, null));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching top times: " + e.getMessage());
        }
        return topScores;
    }

    public List<UserScore> getTopScores() {
        List<UserScore> topScores = new ArrayList<>();
        if (!isConnected()) return topScores;

        String query = "SELECT username, score FROM scores ORDER BY score DESC LIMIT 10";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String username = rs.getString("username");
                int score = rs.getInt("score");
                topScores.add(new UserScore(username, score, 0, null));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching top scores: " + e.getMessage());
        }
        return topScores;
    }

    public int getUserScore(String username) {
        if (!isConnected()) return 0;

        String query = "SELECT score FROM scores WHERE username = ? ORDER BY timestamp DESC LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("score");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching user score: " + e.getMessage());
        }
        return 0;
    }

    public List<UserScore> getUserScores(String username) {
        List<UserScore> scores = new ArrayList<>();
        if (!isConnected()) return scores;

        String query = "SELECT username, score, time_taken, timestamp FROM scores WHERE username = ? ORDER BY timestamp DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                scores.add(new UserScore(
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getInt("time_taken"),
                        rs.getTimestamp("timestamp")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching user scores: " + e.getMessage());
        }
        return scores;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}