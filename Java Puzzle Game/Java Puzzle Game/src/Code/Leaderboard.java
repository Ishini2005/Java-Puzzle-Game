package Code;

import javax.swing.*;
import Code.banana.engine.UserScore;
import Code.database.DataBaseManager;
import java.awt.*;
import java.util.List;

public class Leaderboard extends JFrame {
    private DataBaseManager dbManager;
    private JPanel leaderboardPanel;
    private String currentUser;
    private boolean showTimeLeaderboard = true;

    public Leaderboard(DataBaseManager dbManager, String currentUser) {
        this.dbManager = dbManager;
        this.currentUser = currentUser;

        setTitle("🏆 Leaderboard");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeUI();
        loadLeaderboard();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255));

        // Header with toggle buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 248, 255));

        JLabel headerLabel = new JLabel("🏆 TOP PLAYERS 🏆", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(72, 61, 139));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        headerPanel.add(headerLabel, BorderLayout.NORTH);

        // Toggle buttons
        JPanel togglePanel = new JPanel(new FlowLayout());
        togglePanel.setBackground(new Color(240, 248, 255));

        JButton timeButton = new JButton("⏱️ Fastest Times");
        timeButton.setFont(new Font("Arial", Font.BOLD, 14));
        timeButton.setBackground(showTimeLeaderboard ? new Color(255, 215, 0) : Color.LIGHT_GRAY);

        JButton scoreButton = new JButton("⭐ Highest Scores");
        scoreButton.setFont(new Font("Arial", Font.BOLD, 14));
        scoreButton.setBackground(showTimeLeaderboard ? Color.LIGHT_GRAY : new Color(255, 215, 0));

        timeButton.addActionListener(e -> {
            showTimeLeaderboard = true;
            timeButton.setBackground(new Color(255, 215, 0));
            scoreButton.setBackground(Color.LIGHT_GRAY);
            loadLeaderboard();
        });

        scoreButton.addActionListener(e -> {
            showTimeLeaderboard = false;
            scoreButton.setBackground(new Color(255, 215, 0));
            timeButton.setBackground(Color.LIGHT_GRAY);
            loadLeaderboard();
        });

        togglePanel.add(timeButton);
        togglePanel.add(scoreButton);
        headerPanel.add(togglePanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Leaderboard panel
        leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        leaderboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(leaderboardPanel), BorderLayout.CENTER);

        // Footer with buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(100, 149, 237));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadLeaderboard());

        JButton backButton = new JButton("🔙 Back to Levels");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(255, 223, 0));
        backButton.addActionListener(e -> {
            dispose();
            new Levels(dbManager, currentUser).setVisible(true);
        });

        footerPanel.add(refreshButton);
        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void loadLeaderboard() {
        leaderboardPanel.removeAll();

        // Header row
        JPanel headerRow = new JPanel(new GridLayout(1, 3, 10, 10));
        headerRow.add(createLabel("Rank", true));
        headerRow.add(createLabel("Player", true));

        if (showTimeLeaderboard) {
            headerRow.add(createLabel("Time (s)", true));
        } else {
            headerRow.add(createLabel("Score", true));
        }

        headerRow.setBackground(new Color(135, 206, 250));
        leaderboardPanel.add(headerRow);

        // Get scores
        List<UserScore> scores;
        if (showTimeLeaderboard) {
            scores = dbManager.getTopScoresByTime();
        } else {
            scores = dbManager.getTopScores();
        }

        if (scores.isEmpty()) {
            JPanel emptyRow = new JPanel(new GridLayout(1, 1));
            JLabel emptyLabel = createLabel("No scores yet. Play a game!", false);
            emptyLabel.setForeground(Color.GRAY);
            emptyRow.add(emptyLabel);
            leaderboardPanel.add(emptyRow);
        } else {
            int rank = 1;
            for (UserScore score : scores) {
                JPanel row = new JPanel(new GridLayout(1, 3, 10, 10));

                // Highlight current user
                if (score.getUsername().equals(currentUser)) {
                    row.setBackground(new Color(255, 223, 186));
                    row.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
                } else {
                    row.setBackground(rank % 2 == 0 ? new Color(224, 255, 255) : new Color(173, 216, 230));
                }

                // Add medal emojis for top 3
                String rankText;
                if (rank == 1) rankText = "🥇 1";
                else if (rank == 2) rankText = "🥈 2";
                else if (rank == 3) rankText = "🥉 3";
                else rankText = String.valueOf(rank);

                row.add(createLabel(rankText, false));
                row.add(createLabel(score.getUsername(), false));

                if (showTimeLeaderboard) {
                    row.add(createLabel(String.valueOf(score.getTimeTaken()), false));
                } else {
                    row.add(createLabel(String.valueOf(score.getScore()), false));
                }

                leaderboardPanel.add(row);
                rank++;
            }
        }

        leaderboardPanel.revalidate();
        leaderboardPanel.repaint();
    }

    private JLabel createLabel(String text, boolean isHeader) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        if (isHeader) {
            label.setFont(new Font("Arial", Font.BOLD, 18));
        } else {
            label.setFont(new Font("Arial", Font.PLAIN, 16));
        }
        return label;
    }
}