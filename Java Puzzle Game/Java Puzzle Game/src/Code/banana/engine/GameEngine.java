package Code.banana.engine;

import java.awt.image.BufferedImage;

public class GameEngine {
    private String playerName;
    private int score = 0;
    private GameServer gameServer;
    private Game currentGame;

    public GameEngine(String playerName) {
        this.playerName = playerName;
        this.gameServer = new GameServer();
    }

    public BufferedImage nextGame() {
        currentGame = gameServer.getRandomGame();
        return currentGame != null ? currentGame.getImage() : null;
    }

    public boolean checkAnswer(int answer) {
        if (currentGame != null && answer == currentGame.getSolution()) {
            score++;
            return true;
        }
        return false;
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        score = 0;
    }
}