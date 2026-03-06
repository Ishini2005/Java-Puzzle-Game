package Code.banana.engine;

import java.sql.Timestamp;

public class UserScore {
    private String username;
    private int score;
    private int timeTaken;
    private Timestamp timestamp;

    // Constructor with all fields
    public UserScore(String username, int score, int timeTaken, Timestamp timestamp) {
        this.username = username;
        this.score = score;
        this.timeTaken = timeTaken;
        this.timestamp = timestamp;
    }

    // Constructor for score-based leaderboard
    public UserScore(String username, int score) {
        this.username = username;
        this.score = score;
        this.timeTaken = 0;
        this.timestamp = null;
    }

    // Constructor for time-based leaderboard
    public UserScore(String username, int timeTaken, boolean isTimeScore) {
        this.username = username;
        this.timeTaken = timeTaken;
        this.score = 0;
        this.timestamp = null;
    }

    // Getters
    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getTimeTaken() { return timeTaken; }
    public Timestamp getTimestamp() { return timestamp; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setScore(int score) { this.score = score; }
    public void setTimeTaken(int timeTaken) { this.timeTaken = timeTaken; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        if (timeTaken > 0) {
            return String.format("%s - Time: %ds", username, timeTaken);
        } else {
            return String.format("%s - Score: %d", username, score);
        }
    }
}