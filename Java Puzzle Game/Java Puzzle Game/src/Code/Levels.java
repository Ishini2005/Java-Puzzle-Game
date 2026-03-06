package Code;

import javax.swing.*;
import Code.database.DataBaseManager;
import Code.banana.engine.Session;
import Code.banana.engine.UserFeedback;
import java.awt.*;

public class Levels extends JFrame {
    private DataBaseManager dbManager;
    private String username;

    public Levels(DataBaseManager dbManager, String username) {
        this.dbManager = dbManager;
        this.username = username;

        // Check Banana API status in background
        checkBananaApi();

        initializeUI();
    }

    private void checkBananaApi() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return BananaApi.isApiAvailable();
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        System.out.println("✅ Banana API ready for equation game");
                    } else {
                        System.out.println("⚠️ Banana API unavailable - equation game may use fallback");
                    }
                } catch (Exception e) {
                    System.out.println("❌ Could not check Banana API");
                }
            }
        };
        worker.execute();
    }

    private void initializeUI() {
        setTitle("Select Game Mode");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(135, 206, 250),
                        0, h, new Color(255, 255, 200));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "! 👋");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(0, 100, 0));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(255, 99, 71));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Center panel with game modes
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel modesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        modesPanel.setOpaque(false);
        modesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Picture Matching Mode
        modesPanel.add(createModeCard(
                "🎮 Picture Matching",
                "Match pairs of identical images",
                e -> openPictureMatching()
        ));

        // Equation Game Mode
        modesPanel.add(createModeCard(
                "🧮 Equation Challenge",
                "Solve math equations from Banana API",
                e -> openEquationGame()
        ));

        centerPanel.add(modesPanel);

        // Leaderboard button
        JButton leaderboardButton = new JButton("🏆 View Leaderboard");
        leaderboardButton.setFont(new Font("Arial", Font.BOLD, 16));
        leaderboardButton.setBackground(new Color(255, 215, 0));
        leaderboardButton.setForeground(Color.BLACK);
        leaderboardButton.addActionListener(e -> openLeaderboard());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(leaderboardButton);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(800, 500);
        setLocationRelativeTo(null);
    }

    private JPanel createModeCard(String title, String description, java.awt.event.ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>" +
                description + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton playButton = new JButton("Play ▶");
        playButton.setFont(new Font("Arial", Font.BOLD, 16));
        playButton.setBackground(new Color(50, 150, 50));
        playButton.setForeground(Color.WHITE);
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.addActionListener(action);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(playButton);

        return card;
    }

    private void openPictureMatching() {
        String[] difficulties = {"Easy (4x4)", "Intermediate (6x6)", "Advanced (8x8)"};
        int choice = JOptionPane.showOptionDialog(this,
                "Select Difficulty Level",
                "Picture Matching",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                difficulties,
                difficulties[0]);

        if (choice >= 0) {
            try {
                int gridSize;
                String difficulty;
                switch(choice) {
                    case 0:
                        gridSize = 4;
                        difficulty = "easy";
                        break;
                    case 1:
                        gridSize = 6;
                        difficulty = "intermediate";
                        break;
                    case 2:
                        gridSize = 8;
                        difficulty = "advanced";
                        break;
                    default:
                        return;
                }
                new GameUI(dbManager, username, gridSize, difficulty).setVisible(true);
                dispose();
            } catch (Exception ex) {
                UserFeedback.showError("Error starting game: " + ex.getMessage());
            }
        }
    }

    private void openEquationGame() {
        new EquationGame(username).setVisible(true);
        dispose();
    }

    private void openLeaderboard() {
        new Leaderboard(dbManager, username).setVisible(true);
        dispose();
    }

    private void logout() {
        Session.logout();
        new Login(dbManager).setVisible(true);
        dispose();
    }
}