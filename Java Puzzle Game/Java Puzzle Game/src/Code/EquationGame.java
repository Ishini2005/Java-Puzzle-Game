package Code;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;
import Code.banana.engine.GameEngine;
import Code.database.DataBaseManager;

public class EquationGame extends JFrame {
    private JLabel questionLabel;
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private JButton[] numberButtons;
    private JButton nextButton;
    private JButton leaderboardButton;

    private GameEngine gameEngine;
    private Leaderboard leaderboard;
    private String playerName;
    private Timer gameTimer;
    private int remainingTime;
    private int score = 0;

    public EquationGame(String player) {
        super("Equation Game - Find the Missing Number");
        this.playerName = player != null ? player : "Player";

        DataBaseManager dbManager = new DataBaseManager();
        leaderboard = new Leaderboard(dbManager, player);
        gameEngine = new GameEngine(playerName);

        remainingTime = 60; // 60 seconds
        initializeUI();
        loadNextGame();
        startTimer();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel with game info
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(255, 255, 200));

        timerLabel = new JLabel("Time: " + remainingTime + "s", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setOpaque(true);
        timerLabel.setBackground(new Color(255, 255, 200));
        timerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(new Color(255, 255, 200));
        scoreLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        topPanel.add(timerLabel);
        topPanel.add(scoreLabel);

        // Center panel with equation
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 240, 255));

        questionLabel = new JLabel();
        questionLabel.setPreferredSize(new Dimension(500, 300));
        questionLabel.setHorizontalAlignment(JLabel.CENTER);
        questionLabel.setVerticalAlignment(JLabel.CENTER);
        questionLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        questionLabel.setBackground(Color.WHITE);
        questionLabel.setOpaque(true);

        centerPanel.add(questionLabel);

        // Bottom panel with number buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(new Color(255, 255, 200));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        buttonPanel.setBackground(new Color(255, 255, 200));

        numberButtons = new JButton[10];
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = createNumberButton(i);
            buttonPanel.add(numberButtons[i]);
        }

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(new Color(255, 255, 200));

        nextButton = new JButton("Next Game");
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.setBackground(new Color(100, 149, 237));
        nextButton.setForeground(Color.WHITE);
        nextButton.addActionListener(e -> loadNextGame());

        leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setFont(new Font("Arial", Font.BOLD, 16));
        leaderboardButton.setBackground(new Color(255, 215, 0));
        leaderboardButton.addActionListener(e -> openLeaderboard());

        controlPanel.add(nextButton);
        controlPanel.add(leaderboardButton);

        bottomPanel.add(buttonPanel);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(controlPanel);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(800, 700);
        setLocationRelativeTo(null);
    }

    private JButton createNumberButton(int number) {
        JButton button = new JButton(String.valueOf(number));
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(new Color(220, 220, 250));
        button.setFocusPainted(false);
        button.addActionListener(e -> checkAnswer(number));
        return button;
    }

    private void startTimer() {
        gameTimer = new Timer(1000, e -> {
            remainingTime--;
            timerLabel.setText("Time: " + remainingTime + "s");

            if (remainingTime <= 10) {
                timerLabel.setForeground(Color.RED);
                timerLabel.setBackground(remainingTime % 2 == 0 ? Color.YELLOW : Color.RED);
            }

            if (remainingTime <= 0) {
                gameTimer.stop();
                endGame();
            }
        });
        gameTimer.start();
    }

    private void loadNextGame() {
        try {
            BufferedImage image = gameEngine.nextGame();
            if (image != null) {
                Image scaled = image.getScaledInstance(450, 250, Image.SCALE_SMOOTH);
                questionLabel.setIcon(new ImageIcon(scaled));
                questionLabel.setText("");
            } else {
                questionLabel.setText("No more games available!");
            }
        } catch (Exception e) {
            questionLabel.setText("Error loading game: " + e.getMessage());
        }
    }

    private void checkAnswer(int answer) {
        if (gameEngine.checkAnswer(answer)) {
            score += 10;
            scoreLabel.setText("Score: " + score);
            JOptionPane.showMessageDialog(this, "Correct! +10 points",
                    "Correct", JOptionPane.INFORMATION_MESSAGE);
            loadNextGame();
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect, try again",
                    "Wrong Answer", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void endGame() {
        for (JButton button : numberButtons) {
            button.setEnabled(false);
        }

        // Save score
        try {
            DataBaseManager dbManager = new DataBaseManager();
            int timeTaken = 60 - remainingTime;
            dbManager.saveScore(playerName, score, timeTaken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Time's up! Final Score: " + score + "\nWould you like to view leaderboard?",
                "Game Over",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            openLeaderboard();
        } else {
            new Login(new DataBaseManager()).setVisible(true);
        }
        dispose();
    }

    private void openLeaderboard() {
        // Just create and show the Leaderboard - it shows itself in constructor
        DataBaseManager dbManager = new DataBaseManager();
        Leaderboard leaderboard = new Leaderboard(dbManager, playerName);
        leaderboard.setVisible(true);  // Make sure it's visible
        dispose();
    }
}