package Code;

import Code.banana.engine.GameEngine;
import Code.database.DataBaseManager;
import Code.banana.engine.UserFeedback;
import Code.banana.engine.UIStyles;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EquationGame extends JFrame {
    private GameEngine gameEngine;
    private String username;
    private boolean isGuest;
    private DataBaseManager dbManager;
    private int previousScore;
    private int timeLeft = 60;
    private JLabel timerLabel, scoreLabel, levelLabel;
    private Timer timer;
    private JPanel equationPanel;
    private String level;
    private int equationScore = 0;

    public EquationGame(String username, boolean isGuest, DataBaseManager dbManager, int previousScore, String level) {
        this.username = username;
        this.isGuest = isGuest;
        this.dbManager = dbManager;
        this.previousScore = previousScore;
        this.level = level;
        this.gameEngine = new GameEngine(username);

        initializeUI();
        loadEquation();
        startTimer();
    }

    private void initializeUI() {
        setTitle("Equation Challenge - " + (isGuest ? "Guest: " : "Player: ") + username);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyles.PANEL_BACKGROUND);

        // Top panel
        JPanel topPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel playerLabel = UIStyles.createInfoLabel("👤 " + username + (isGuest ? " (G)" : ""));
        levelLabel = UIStyles.createInfoLabel("📊 " + level);
        scoreLabel = UIStyles.createInfoLabel("💰 Score: " + previousScore);
        timerLabel = UIStyles.createInfoLabel("⏱️ Time: " + timeLeft + "s");

        topPanel.add(playerLabel);
        topPanel.add(levelLabel);
        topPanel.add(scoreLabel);
        topPanel.add(timerLabel);

        // Center panel with equation
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        equationPanel = new JPanel();
        equationPanel.setOpaque(true);
        equationPanel.setBackground(UIStyles.WHITE);
        equationPanel.setBorder(UIStyles.THICK_LINE_BORDER);
        equationPanel.setPreferredSize(new Dimension(550, 300));
        centerPanel.add(equationPanel);

        // Bottom panel with number buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 5, 15, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));

        // UPDATED: Number buttons using UIStyles
        for (int i = 0; i < 10; i++) {
            final int number = i;
            JButton button = UIStyles.createStyledButton(String.valueOf(number), UIStyles.BUTTON_BLUE, 80, 80);
            button.setFont(new Font("Arial", Font.BOLD, 28));
            button.addActionListener(e -> checkAnswer(number));
            buttonPanel.add(button);
        }

        // UPDATED: Exit button
        JButton exitButton = UIStyles.createStyledButton("EXIT TO LEVELS", UIStyles.RED, 250, 50);

        exitButton.addActionListener(e -> returnToLevels());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        exitPanel.setOpaque(false);
        exitPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        exitPanel.add(exitButton);
        bottomPanel.add(exitPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    // Note: createNumberButton method removed - now using UIStyles

    private void loadEquation() {
        BufferedImage equationImage = gameEngine.nextGame();
        if (equationImage != null) {
            equationPanel.removeAll();

            // Scale image to fit panel
            Image scaledImage = equationImage.getScaledInstance(500, 250, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setBorder(UIStyles.LINE_BORDER);

            equationPanel.setLayout(new GridBagLayout());
            equationPanel.add(imageLabel);

            revalidate();
            repaint();
        } else {
            UserFeedback.showError("Error loading equation from API!");
            returnToLevels();
        }
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("⏱️ Time: " + timeLeft + "s");

            if (timeLeft <= 10) {
                timerLabel.setForeground(UIStyles.RED);
                timerLabel.setBackground(timeLeft % 2 == 0 ? UIStyles.GOLD : UIStyles.WHITE);
            }

            if (timeLeft <= 0) {
                timer.stop();
                endGame(false);
            }
        });
        timer.start();
    }

    private void checkAnswer(int answer) {
        if (gameEngine.checkAnswer(answer)) {
            equationScore += 50;
            timer.stop();
            endGame(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Incorrect answer. Try again!",
                    "Wrong Answer",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void endGame(boolean won) {
        int totalScore = previousScore + equationScore;
        int timeTaken = 60 - timeLeft;

        if (won) {
            UserFeedback.showInfo(
                    "🎉 Congratulations! You solved the equation!\n\n" +
                            "Equation Score: +50\n" +
                            "Total Score: " + totalScore
            );
        } else {
            UserFeedback.showInfo(
                    "⏰ Time's up!\n\n" +
                            "Final Score: " + totalScore
            );
        }

        // Save final score
        try {
            dbManager.saveScore(
                    isGuest ? null : username,
                    isGuest,
                    isGuest ? username : null,
                    level + " Equation",
                    totalScore,
                    timeTaken,
                    0,
                    equationScore
            );
            System.out.println("Equation score saved successfully");
        } catch (Exception e) {
            System.err.println("Error saving equation score: " + e.getMessage());
        }

        returnToLevels();
        dispose();
    }

    private void returnToLevels() {
        new Levels(dbManager, username, isGuest).setVisible(true);
        dispose();
    }
}