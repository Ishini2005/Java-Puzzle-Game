package Code;

import javax.swing.*;
import Code.banana.engine.Images;
import Code.database.DataBaseManager;
import Code.banana.engine.UserFeedback;
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
    private JLabel hintLabel, timerLabel, scoreLabel;
    private Timer timer;
    private int timeLeft;
    private JButton firstClicked = null;
    private JButton secondClicked = null;
    private Images imageLoader;
    private int matchedPairs = 0;
    private String difficulty;
    private ArrayList<Integer> imageIds;

    private Timer hideTimer;

    public GameUI(DataBaseManager dbManager, String username, int gridSize, String difficulty) throws Exception {
        this.dbManager = dbManager;
        this.username = username;
        this.gridSize = gridSize;
        this.difficulty = difficulty;
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
            default:
                throw new IllegalArgumentException("Invalid difficulty level: " + difficulty);
        }

        initializeUI();
        loadImages();
        startTimer();
    }

    private void initializeUI() {
        setTitle("Picture Matching Game - " + difficulty.substring(0, 1).toUpperCase() +
                difficulty.substring(1) + " Mode");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for game info
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(255, 255, 200));

        hintLabel = createInfoLabel("💡 Hints: " + hintsLeft);
        scoreLabel = createInfoLabel("⭐ Score: " + score);
        timerLabel = createInfoLabel("⏱️ Time: " + timeLeft + "s");

        topPanel.add(hintLabel);
        topPanel.add(scoreLabel);
        topPanel.add(timerLabel);

        // Game grid panel
        JPanel gridPanel = new JPanel(new GridLayout(gridSize, gridSize, 5, 5));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.setBackground(new Color(200, 200, 255));

        // Create buttons
        for (int i = 0; i < gridSize * gridSize; i++) {
            JButton button = createGameButton();
            buttons.add(button);
            gridPanel.add(button);
        }

        // Bottom panel with hint button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(255, 255, 200));

        JButton hintButton = new JButton("🔍 Use Hint (" + hintsLeft + " left)");
        hintButton.setFont(new Font("Arial", Font.BOLD, 14));
        hintButton.setBackground(Color.YELLOW);
        hintButton.addActionListener(e -> useHint());
        bottomPanel.add(hintButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(800, 800);
        setLocationRelativeTo(null);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setOpaque(true);
        label.setBackground(new Color(255, 255, 200));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return label;
    }

    private JButton createGameButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(80, 80));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.addActionListener(e -> handleButtonClick(button));

        // Set initial logo (question mark)
        button.setText("?");
        button.setFont(new Font("Arial", Font.BOLD, 30));
        button.setForeground(Color.GRAY);

        return button;
    }

    private void loadImages() {
        ArrayList<Integer> idList = new ArrayList<>();

        // Create pairs of image IDs
        for (int i = 0; i < gridSize * gridSize / 2; i++) {
            idList.add(i);
            idList.add(i); // Add pair
        }

        Collections.shuffle(idList);
        imageIds = idList;
    }

    private void handleButtonClick(JButton clickedButton) {
        if (!clickedButton.isEnabled() || clickedButton == secondClicked) {
            return;
        }

        int index = buttons.indexOf(clickedButton);
        int imageId = imageIds.get(index);

        if (firstClicked == null) {
            // First card selected
            firstClicked = clickedButton;
            revealImage(clickedButton, imageId);
        } else if (secondClicked == null && clickedButton != firstClicked) {
            // Second card selected
            secondClicked = clickedButton;
            int firstIndex = buttons.indexOf(firstClicked);
            int firstImageId = imageIds.get(firstIndex);

            revealImage(clickedButton, imageId);

            // Check if they match
            if (firstImageId == imageId) {
                // Match found
                matchFound();
            } else {
                // No match - schedule hide - FIXED: Use class variables instead of local
                if (hideTimer != null && hideTimer.isRunning()) {
                    hideTimer.stop();
                }
                hideTimer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        hideUnmatched();
                    }
                });
                hideTimer.setRepeats(false);
                hideTimer.start();
            }
        }
    }

    private void revealImage(JButton button, int imageId) {
        // Create colored representation based on imageId
        Color color = new Color(
                (imageId * 70) % 255,
                (imageId * 100) % 255,
                (imageId * 130) % 255
        );

        button.setBackground(color);
        button.setText(String.valueOf(imageId + 1));
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
    }

    private void hideImage(JButton button) {
        button.setBackground(Color.WHITE);
        button.setText("?");
        button.setForeground(Color.GRAY);
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

        // Find first unmatched pair and reveal them
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).isEnabled()) {
                for (int j = i + 1; j < buttons.size(); j++) {
                    if (buttons.get(j).isEnabled() &&
                            imageIds.get(i).equals(imageIds.get(j))) {

                        // Found a matching pair
                        JButton firstButton = buttons.get(i);
                        JButton secondButton = buttons.get(j);
                        int firstId = imageIds.get(i);
                        int secondId = imageIds.get(j);

                        revealImage(firstButton, firstId);
                        revealImage(secondButton, secondId);

                        // FIXED: Use final copies for timer
                        Timer hintTimer = new Timer(2000, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                hideImage(firstButton);
                                hideImage(secondButton);
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
        // FIXED: Use instance variable for timer
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("⏱️ Time: " + timeLeft + "s");

                if (timeLeft <= 10) {
                    timerLabel.setForeground(Color.RED);
                }

                if (timeLeft <= 0) {
                    timer.stop();
                    endGame(false);
                }
            }
        });
        timer.start();
    }

    private void endGame(boolean won) {
        timer.stop();

        String message = won ?
                "🎉 Congratulations! You won!\nFinal Score: " + score :
                "⏰ Time's up! Game Over.\nFinal Score: " + score;

        JOptionPane.showMessageDialog(this, message, "Game Over",
                JOptionPane.INFORMATION_MESSAGE);

        // Save score
        try {
            int timeTaken = (difficulty.equals("easy") ? 120 :
                    difficulty.equals("intermediate") ? 90 : 60) - timeLeft;
            dbManager.saveScore(username, score, timeTaken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ask if they want to play equation game
        int choice = JOptionPane.showConfirmDialog(this,
                "Would you like to play the equation game?",
                "Next Challenge",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            new EquationGame(username).setVisible(true);
        } else {
            new Levels(dbManager, username).setVisible(true);
        }
        dispose();
    }
}