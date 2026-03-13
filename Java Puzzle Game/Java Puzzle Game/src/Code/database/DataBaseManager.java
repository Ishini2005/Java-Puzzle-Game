package Code.database;

import Code.banana.engine.UserScore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataBaseManager {
    private Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/game_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public DataBaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database");
            e.printStackTrace();
        }
    }

    /**
     * NEW METHOD: Check if database is connected
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
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

    public String createGuestUser(String guestName) {
        String sessionId = UUID.randomUUID().toString();
        String sql = "INSERT INTO guest_users (guest_name, session_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, guestName);
            pstmt.setString(2, sessionId);
            pstmt.executeUpdate();
            return sessionId;
        } catch (SQLException e) {
            System.err.println("Error creating guest user: " + e.getMessage());
            return null;
        }
    }

    public boolean guestExists(String guestName) {
        String sql = "SELECT * FROM guest_users WHERE guest_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, guestName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean register(String username, String password, String email) throws SQLException {
        if (connection == null) return false;

        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters");
            return false;
        }

        String sql = "INSERT INTO users (username, password, email, is_guest) VALUES (?, ?, ?, false)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Username already exists or database error");
            return false;
        }
    }

    public boolean login(String username, String password) throws SQLException {
        if (connection == null) return false;

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    updateLastLogin(username);
                    return true;
                }
                return false;
            }
        }
    }

    private void updateLastLogin(String username) {
        String sql = "UPDATE users SET last_login = NOW() WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }

    public void saveScore(String username, boolean isGuest, String guestName,
                          String level, int totalScore, int timeTaken,
                          int matchesFound, int equationScore) {
        String sql = "INSERT INTO scores (username, is_guest, guest_name, level, score, " +
                "time_taken, matches_found, equation_score, total_score) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isGuest ? null : username);
            pstmt.setBoolean(2, isGuest);
            pstmt.setString(3, isGuest ? guestName : null);
            pstmt.setString(4, level);
            pstmt.setInt(5, totalScore);
            pstmt.setInt(6, timeTaken);
            pstmt.setInt(7, matchesFound);
            pstmt.setInt(8, equationScore);
            pstmt.setInt(9, totalScore);
            pstmt.executeUpdate();
            System.out.println("Score saved for " + (isGuest ? guestName : username));
        } catch (SQLException e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }

    public List<UserScore> getTopScores() {
        List<UserScore> topScores = new ArrayList<>();
        String sql = "SELECT \n" +
                "  CASE WHEN is_guest = 1 THEN CONCAT(guest_name, ' (Guest)') ELSE username END as player_name,\n" +
                "  level,\n" +
                "  total_score,\n" +
                "  time_taken\n" +
                "FROM scores \n" +
                "ORDER BY total_score DESC, time_taken ASC \n" +
                "LIMIT 20";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                topScores.add(new UserScore(
                        rs.getString("player_name"),
                        rs.getString("level"),
                        rs.getInt("total_score"),
                        rs.getInt("time_taken")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching top scores: " + e.getMessage());
        }
        return topScores;
    }

    public LevelInfo getLevelInfo(String levelName) {
        String sql = "SELECT * FROM game_levels WHERE level_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, levelName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new LevelInfo(
                        rs.getString("level_name"),
                        rs.getInt("grid_size"),
                        rs.getInt("time_limit"),
                        rs.getInt("hints_count"),
                        rs.getInt("base_score")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching level info: " + e.getMessage());
        }
        return null;
    }

    public String createInvitation(String inviterName, String inviteeEmail) {
        String inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String sql = "INSERT INTO invitations (inviter_name, invitee_email, invite_code, expires_at) " +
                "VALUES (?, ?, ?, DATE_ADD(NOW(), INTERVAL 7 DAY))";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, inviterName);
            pstmt.setString(2, inviteeEmail);
            pstmt.setString(3, inviteCode);
            pstmt.executeUpdate();
            return inviteCode;
        } catch (SQLException e) {
            System.err.println("Error creating invitation: " + e.getMessage());
            return null;
        }
    }

    public UserStats getUserStats(String username) {
        String sql = "SELECT \n" +
                "  COUNT(*) as games_played,\n" +
                "  COALESCE(AVG(total_score), 0) as avg_score,\n" +
                "  COALESCE(MAX(total_score), 0) as best_score \n" +
                "FROM scores \n" +
                "WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new UserStats(
                        rs.getInt("games_played"),
                        rs.getDouble("avg_score"),
                        rs.getInt("best_score")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user stats: " + e.getMessage());
        }
        return new UserStats(0, 0, 0);
    }

    public static class LevelInfo {
        public String levelName;
        public int gridSize;
        public int timeLimit;
        public int hintsCount;
        public int baseScore;

        public LevelInfo(String levelName, int gridSize, int timeLimit, int hintsCount, int baseScore) {
            this.levelName = levelName;
            this.gridSize = gridSize;
            this.timeLimit = timeLimit;
            this.hintsCount = hintsCount;
            this.baseScore = baseScore;
        }
    }

    public static class UserStats {
        public int gamesPlayed;
        public double avgScore;
        public int bestScore;

        public UserStats(int gamesPlayed, double avgScore, int bestScore) {
            this.gamesPlayed = gamesPlayed;
            this.avgScore = avgScore;
            this.bestScore = bestScore;
        }
    }
}