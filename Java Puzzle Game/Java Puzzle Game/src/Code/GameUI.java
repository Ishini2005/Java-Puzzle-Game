package Code;

import javax.swing.*;
import Code.banana.engine.Images;
import Code.database.DataBaseManager;
import Code.banana.engine.UserFeedback;
import Code.banana.engine.UIStyles;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public class GameUI extends JFrame {

    private int gridSize;
    private ArrayList<JButton> buttons;
    private String username;
    private DataBaseManager dbManager;
    private int score;
    private int hintsLeft;
    private JLabel hintLabel, timerLabel, scoreLabel, levelLabel;
    private Timer timer;
    private int timeLeft;
    private JButton firstClicked = null;
    private JButton secondClicked = null;
    private Images imageLoader;
    private int matchedPairs = 0;
    private String difficulty;
    private ArrayList<Integer> imageIds;
    private boolean isGuest;
    private int equationScore = 0;
    private String level;
    private Timer hideTimer;

    public GameUI(DataBaseManager dbManager, String username, boolean isGuest, int gridSize, String difficulty) {
        try {
            this.dbManager = dbManager;
            this.username = username;
            this.isGuest = isGuest;
            this.gridSize = gridSize;
            this.difficulty = difficulty;
            this.level = getLevelName(difficulty);

            // Initialize image loader
            this.imageLoader = new Images();

            this.buttons = new ArrayList<>();
            this.imageIds = new ArrayList<>();
            this.score = 0;
            this.matchedPairs = 0;

            // Set game parameters based on difficulty
            switch (difficulty.toLowerCase()) {
                case "easy":
                    this.hintsLeft = 6;
                    this.timeLeft = 120;
                    break;
                case "intermediate":
                    this.hintsLeft = 4;
                    this.timeLeft = 90;
                    break;
                case "advanced":
                    this.hintsLeft = 3;
                    this.timeLeft = 60;
                    break;
            }

            initializeUI();
            loadImages();
            startTimer();
            this.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error starting game: " + e.getMessage(),
                    "Game Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getLevelName(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy": return "Beginner";
            case "intermediate": return "Intermediate";
            case "advanced": return "Advanced";
            default: return "Beginner";
        }
    }

    private void initializeUI() {
        setTitle("Picture Matching Game - " + difficulty.substring(0, 1).toUpperCase() +
                difficulty.substring(1) + " Mode");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set background
        getContentPane().setBackground(UIStyles.DARK_BLUE);

        // Top panel
        JPanel topPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        topPanel.setBackground(UIStyles.LIGHT_BLUE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create info labels
        JLabel playerLabel = UIStyles.createInfoLabel("👤 " + username + (isGuest ? " (Guest)" : ""));
        levelLabel = UIStyles.createInfoLabel("📊 " + level);
        hintLabel = UIStyles.createInfoLabel("💡 Hints: " + hintsLeft);
        scoreLabel = UIStyles.createInfoLabel("⭐ Score: " + score);
        timerLabel = UIStyles.createInfoLabel("⏱️ Time: " + timeLeft + "s");

        topPanel.add(playerLabel);
        topPanel.add(levelLabel);
        topPanel.add(hintLabel);
        topPanel.add(scoreLabel);
        topPanel.add(timerLabel);

        // Game grid
        JPanel gridPanel = new JPanel(new GridLayout(gridSize, gridSize, 10, 10));
        gridPanel.setBackground(UIStyles.DARK_BLUE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Game buttons
        for (int i = 0; i < gridSize * gridSize; i++) {
            JButton button = UIStyles.createGameButton();
            button.setText("?");
            button.addActionListener(e -> handleButtonClick(button));
            buttons.add(button);
            gridPanel.add(button);
        }

        // UPDATED Bottom panel with centered buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(UIStyles.DARK_BLUE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        // Hint button - Gold with BLACK text for contrast
        JButton hintButton = UIStyles.createStyledButton("🔍 USE HINT (" + hintsLeft + ")", UIStyles.GOLD, 200, 50);
        hintButton.setForeground(UIStyles.BLACK); // Black text on gold for better visibility
        hintButton.addActionListener(e -> useHint());
        bottomPanel.add(hintButton);

        // Exit button - Red with WHITE text
        JButton exitButton = UIStyles.createStyledButton("🚪 EXIT GAME", UIStyles.RED, 200, 50);
        exitButton.addActionListener(e -> confirmExit());
        bottomPanel.add(exitButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(900, 900);
        setLocationRelativeTo(null);
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            if (timer != null) timer.stop();
            returnToLevels();
        }
    }

    private void loadImages() {
        ArrayList<Integer> idList = new ArrayList<>();
        int numPairs = (gridSize * gridSize) / 2;

        for (int i = 0; i < numPairs; i++) {
            int imageId = i % imageLoader.getImageCount();
            idList.add(imageId);
            idList.add(imageId);
        }

        Collections.shuffle(idList);
        imageIds = idList;
    }

    private void handleButtonClick(JButton clickedButton) {
        if (!clickedButton.isEnabled() || clickedButton == secondClicked) return;

        int index = buttons.indexOf(clickedButton);
        int imageId = imageIds.get(index);

        if (firstClicked == null) {
            firstClicked = clickedButton;
            revealImage(clickedButton, imageId);
        } else if (secondClicked == null && clickedButton != firstClicked) {
            secondClicked = clickedButton;
            int firstIndex = buttons.indexOf(firstClicked);
            int firstImageId = imageIds.get(firstIndex);

            revealImage(clickedButton, imageId);

            if (firstImageId == imageId) {
                matchFound();
            } else {
                disableAllButtons();

                hideTimer = new Timer(1000, e -> {
                    hideUnmatched();
                    enableAllUnmatchedButtons();
                });
                hideTimer.setRepeats(false);
                hideTimer.start();
            }
        }
    }

    private void disableAllButtons() {
        for (JButton button : buttons) button.setEnabled(false);
    }

    private void enableAllUnmatchedButtons() {
        for (JButton button : buttons) {
            if (button != firstClicked && button != secondClicked) {
                button.setEnabled(true);
            }
        }
    }

    private void revealImage(JButton button, int imageId) {
        BufferedImage img = imageLoader.getImageById(imageId);
        if (img != null) {
            Image scaled = img.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaled));
            button.setText("");
            button.setBorder(BorderFactory.createLineBorder(UIStyles.GREEN, 3));
        } else {
            Color color = new Color(
                    (imageId * 70) % 200 + 55,
                    (imageId * 100) % 200 + 55,
                    (imageId * 130) % 200 + 55
            );
            button.setBackground(color);
            button.setText(String.valueOf(imageId + 1));
            button.setForeground(UIStyles.WHITE);
            button.setIcon(null);
        }
    }

    private void hideImage(JButton button) {
        button.setIcon(null);
        button.setBackground(UIStyles.WHITE);
        button.setText("?");
        button.setForeground(UIStyles.BLACK);
        button.setBorder(UIStyles.RAISED_BORDER);
    }

    private void matchFound() {
        firstClicked.setEnabled(false);
        secondClicked.setEnabled(false);
        score += 10;
        scoreLabel.setText("⭐ Score: " + score);
        matchedPairs++;

        if (matchedPairs == (gridSize * gridSize) / 2) {
            endGame(true);
        }

        firstClicked = null;
        secondClicked = null;
    }

    private void hideUnmatched() {
        hideImage(firstClicked);
        hideImage(secondClicked);
        firstClicked = null;
        secondClicked = null;
    }

    private void useHint() {
        if (hintsLeft <= 0) {
            UserFeedback.showWarning("No hints left!");
            return;
        }

        hintsLeft--;
        hintLabel.setText("💡 Hints: " + hintsLeft);

        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).isEnabled()) {
                for (int j = i + 1; j < buttons.size(); j++) {
                    if (buttons.get(j).isEnabled() &&
                            imageIds.get(i).equals(imageIds.get(j))) {

                        JButton firstButton = buttons.get(i);
                        JButton secondButton = buttons.get(j);

                        firstButton.setEnabled(false);
                        secondButton.setEnabled(false);

                        revealImage(firstButton, imageIds.get(i));
                        revealImage(secondButton, imageIds.get(j));

                        Timer hintTimer = new Timer(2000, e -> {
                            if (firstButton != firstClicked && firstButton != secondClicked) {
                                hideImage(firstButton);
                                firstButton.setEnabled(true);
                            }
                            if (secondButton != firstClicked && secondButton != secondClicked) {
                                hideImage(secondButton);
                                secondButton.setEnabled(true);
                            }
                        });
                        hintTimer.setRepeats(false);
                        hintTimer.start();
                        return;
                    }
                }
            }
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

    private void endGame(boolean won) {
        if (timer != null) timer.stop();

        int totalScore = score + (matchedPairs * 5) + ((getInitialTime() - timeLeft) * 2);

        String message = won ?
                "🎉 You Won! Score: " + totalScore :
                "⏰ Time's Up! Score: " + totalScore;

        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);

        int choice = JOptionPane.showConfirmDialog(this,
                "Play Equation Challenge?",
                "Next Level",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            new EquationGame(username, isGuest, dbManager, totalScore, level).setVisible(true);
        } else {
            returnToLevels();
        }
        dispose();
    }

    private int getInitialTime() {
        switch (difficulty.toLowerCase()) {
            case "easy": return 120;
            case "intermediate": return 90;
            case "advanced": return 60;
            default: return 120;
        }
    }

    private void returnToLevels() {
        new Levels(dbManager, username, isGuest).setVisible(true);
    }
}