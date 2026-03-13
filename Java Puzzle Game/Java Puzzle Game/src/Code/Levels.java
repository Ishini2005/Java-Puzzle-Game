package Code;

import Code.database.DataBaseManager;
import Code.banana.engine.Session;
import Code.banana.engine.UserFeedback;
import Code.banana.engine.UIStyles;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Levels extends JFrame {
    private DataBaseManager dbManager;
    private String username;
    private boolean isGuest;
    private Map<String, LevelButton> levelButtons;

    public Levels(DataBaseManager dbManager, String username, boolean isGuest) {
        this.dbManager = dbManager;
        this.username = username;
        this.isGuest = isGuest;
        this.levelButtons = new HashMap<>();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Game Levels - " + (isGuest ? "Guest: " : "Player: ") + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyles.PANEL_BACKGROUND);

        // Top panel with user info and buttons
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center panel with levels
        JPanel centerPanel = createLevelsPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with stats
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(1000, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Welcome message with avatar
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setOpaque(false);

        JLabel avatarLabel = new JLabel(isGuest ? "🎮" : "👤");
        avatarLabel.setFont(new Font("Arial", Font.PLAIN, 30));

        JLabel welcomeLabel = new JLabel(
                "<html><h2>Welcome, " + username +
                        (isGuest ? " (Guest Mode)</h2>" : "</h2>") +
                        "<p style='font-size:12px;color:gray;'>" +
                        (isGuest ? "Playing as guest - Create an account to save progress!" :
                                "Ready to test your skills?") + "</p></html>"
        );
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        welcomePanel.add(avatarLabel);
        welcomePanel.add(welcomeLabel);

        // Right panel with buttons - UPDATED with UIStyles
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        if (!isGuest) {
            JButton inviteButton = UIStyles.createSmallButton("📧 INVITE FRIENDS", UIStyles.BUTTON_ORANGE);
            inviteButton.addActionListener(e -> openInviteScreen());
            rightPanel.add(inviteButton);
        }

        JButton leaderboardButton = UIStyles.createSmallButton("🏆 LEADERBOARD", UIStyles.BUTTON_BLUE);
        leaderboardButton.addActionListener(e -> {
            new Leaderboard(dbManager, username, isGuest);
            dispose();
        });
        rightPanel.add(leaderboardButton);

        JButton logoutButton = UIStyles.createSmallButton("🚪 LOGOUT", UIStyles.RED);
        logoutButton.addActionListener(e -> handleLogout());
        rightPanel.add(logoutButton);

        topPanel.add(welcomePanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createLevelsPanel() {
        JPanel levelsPanel = new JPanel(new GridBagLayout());
        levelsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Level 1 - Beginner
        LevelButton beginnerBtn = createLevelButton(
                "🌟 BEGINNER 🌟",
                "4x4 Grid · 2 Minutes · 6 Hints",
                "Perfect for new players!",
                "Easy matches to get started",
                new Color(144, 238, 144),
                4, "easy"
        );
        gbc.gridx = 0;
        gbc.gridy = 0;
        levelsPanel.add(beginnerBtn, gbc);
        levelButtons.put("easy", beginnerBtn);

        // Level 2 - Intermediate
        LevelButton intermediateBtn = createLevelButton(
                "⭐ INTERMEDIATE ⭐",
                "6x6 Grid · 90 Seconds · 4 Hints",
                "For experienced players!",
                "More challenging combinations",
                new Color(255, 228, 181),
                6, "intermediate"
        );
        gbc.gridx = 1;
        gbc.gridy = 0;
        levelsPanel.add(intermediateBtn, gbc);
        levelButtons.put("intermediate", intermediateBtn);

        // Level 3 - Advanced
        LevelButton advancedBtn = createLevelButton(
                "⚡ ADVANCED ⚡",
                "8x8 Grid · 60 Seconds · 3 Hints",
                "Only for experts!",
                "Test your memory limits",
                new Color(255, 160, 122),
                8, "advanced"
        );
        gbc.gridx = 2;
        gbc.gridy = 0;
        levelsPanel.add(advancedBtn, gbc);
        levelButtons.put("advanced", advancedBtn);

        return levelsPanel;
    }

    private LevelButton createLevelButton(String title, String details, String desc,
                                          String tip, Color color, int gridSize, String difficulty) {
        LevelButton button = new LevelButton();
        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));
        button.setBackground(color);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setPreferredSize(new Dimension(250, 250));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel detailsLabel = new JLabel(details);
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        detailsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        descLabel.setForeground(new Color(80, 80, 80));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tipLabel = new JLabel("💡 " + tip);
        tipLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        tipLabel.setForeground(new Color(100, 100, 100));
        tipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // UPDATED: PLAY NOW button with UIStyles
        JButton playButton = UIStyles.createStyledButton("PLAY NOW", UIStyles.BUTTON_BLUE, 150, 40);
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.addActionListener(e -> {
            System.out.println("=== PLAY BUTTON CLICKED ===");
            System.out.println("Grid Size: " + gridSize);
            System.out.println("Difficulty: " + difficulty);
            startGame(gridSize, difficulty);
        });

        button.add(Box.createVerticalGlue());
        button.add(titleLabel);
        button.add(Box.createRigidArea(new Dimension(0, 5)));
        button.add(detailsLabel);
        button.add(Box.createRigidArea(new Dimension(0, 5)));
        button.add(descLabel);
        button.add(tipLabel);
        button.add(Box.createRigidArea(new Dimension(0, 15)));
        button.add(playButton);
        button.add(Box.createVerticalGlue());

        return button;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        if (!isGuest) {
            try {
                DataBaseManager.UserStats stats = dbManager.getUserStats(username);
                JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
                statsPanel.setOpaque(false);

                statsPanel.add(createStatBox("Games Played", String.valueOf(stats.gamesPlayed), UIStyles.BUTTON_BLUE));
                statsPanel.add(createStatBox("Avg Score", String.format("%.1f", stats.avgScore), UIStyles.BUTTON_GREEN));
                statsPanel.add(createStatBox("Best Score", String.valueOf(stats.bestScore), UIStyles.BUTTON_ORANGE));

                bottomPanel.add(statsPanel);
            } catch (Exception e) {
                System.err.println("Error loading stats: " + e.getMessage());
            }
        } else {
            JPanel guestPanel = new JPanel();
            guestPanel.setOpaque(false);
            JLabel guestLabel = new JLabel(
                    "<html><center>🎮 Playing as Guest<br>" +
                            "<span style='font-size:11px;color:gray;'>Create an account to save your progress and appear on leaderboard!</span></center></html>"
            );
            guestLabel.setFont(new Font("Arial", Font.BOLD, 12));
            guestPanel.add(guestLabel);
            bottomPanel.add(guestPanel);
        }

        return bottomPanel;
    }

    private JPanel createStatBox(String label, String value, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 255, 255, 200));
        panel.setBorder(BorderFactory.createLineBorder(color, 2));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Arial", Font.BOLD, 11));
        labelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(5));
        panel.add(valueLabel);
        panel.add(labelLabel);
        panel.add(Box.createVerticalStrut(5));

        return panel;
    }

    private void startGame(int gridSize, String difficulty) {
        try {
            System.out.println("\n=== STARTING GAME ===");
            System.out.println("Parameters received:");
            System.out.println("  - username: " + username);
            System.out.println("  - isGuest: " + isGuest);
            System.out.println("  - gridSize: " + gridSize);
            System.out.println("  - difficulty: " + difficulty);
            System.out.println("  - dbManager: " + (dbManager != null ? "OK" : "NULL"));

            if (username == null || username.isEmpty()) {
                System.err.println("ERROR: Username is null or empty!");
                UserFeedback.showError("Username error! Please login again.");
                return;
            }

            if (dbManager == null) {
                System.err.println("ERROR: Database manager is null!");
                UserFeedback.showError("Database connection error!");
                return;
            }

            if (dbManager.isConnected()) {
                System.out.println("✅ Database connection verified");
            } else {
                System.err.println("⚠️ WARNING: Database not connected! Game will continue but scores won't be saved.");
            }

            System.out.println("Creating GameUI instance...");
            GameUI gameUI = new GameUI(dbManager, username, isGuest, gridSize, difficulty);
            System.out.println("✅ GameUI created successfully!");
            System.out.println("Disposing Levels screen...");
            dispose();
            System.out.println("✅ Game started successfully!\n");

        } catch (Exception e) {
            System.err.println("\n!!! ERROR STARTING GAME !!!");
            e.printStackTrace();
            UserFeedback.showError("Error starting game: " + e.getMessage() +
                    "\nPlease check console for details.");
        }
    }

    private void openInviteScreen() {
        try {
            new InviteFriends(dbManager, username);
        } catch (Exception e) {
            System.err.println("Error opening invite screen: " + e.getMessage());
            UserFeedback.showError("Cannot open invite screen: " + e.getMessage());
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Session.logout();
            new Login(dbManager);
            dispose();
        }
    }

    // Custom button class for levels
    private class LevelButton extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        }
    }
}