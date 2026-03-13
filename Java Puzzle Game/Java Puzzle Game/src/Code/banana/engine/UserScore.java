package Code.banana.engine;

public class UserScore {
    private String playerName;
    private String level;
    private int totalScore;
    private int timeTaken;

    public UserScore(String playerName, String level, int totalScore, int timeTaken) {
        this.playerName = playerName;
        this.level = level;
        this.totalScore = totalScore;
        this.timeTaken = timeTaken;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }
}