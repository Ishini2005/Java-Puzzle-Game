package Code.banana.engine;

import java.awt.image.BufferedImage;

/**
 * Main class where the games are coming from.
 */
public class GameEngine {
    private String thePlayer = null;
    private int score = 0;
    private GameServer theGames = new GameServer();
    private Game current = null;

    /**
     * Each player has their own game engine.
     * @param player
     */
    public GameEngine(String player) {
        this.thePlayer = player;
    }

    /**
     * Retrieves a game from the server.
     */
    public BufferedImage nextGame() {
        current = theGames.getRandomGame();
        if (current != null) {
            return current.getImage();
        }
        return null;
    }

    /**
     * Checks if the parameter i is a solution to the game.
     * @param i The player's answer to check.
     * @return true if the answer is correct, false otherwise.
     */
    public boolean checkAnswer(int i) {
        if (current != null && i == current.getSolution()) {
            score++;
            return true;
        }
        return false;
    }

    /**
     * Submits the player's answer and checks if it's correct.
     * @param number The player's answer.
     */
    public void submitAnswer(int number) {
        if (checkAnswer(number)) {
            System.out.println("Correct answer! Score: " + score);
        } else {
            System.out.println("Incorrect answer. Try again.");
        }
    }

    /**
     * Retrieves the current score.
     * @return the player's score.
     */
    public int getScore() {
        return score;
    }

    public String getPlayerName() {
        return thePlayer;
    }
}