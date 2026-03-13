package Code;

import Code.banana.engine.UserScore;
import Code.database.DataBaseManager;
import Code.banana.engine.Session;
import Code.banana.engine.UserFeedback;
import Code.banana.engine.UIStyles;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class Leaderboard extends JFrame {
    private DataBaseManager dbManager;
    private String username;
    private boolean isGuest;
    private JTable leaderboardTable;
    private JScrollPane scrollPane;

    public Leaderboard(DataBaseManager dbManager, String username, boolean isGuest) {
        this.dbManager = dbManager;
        this.username = username;
        this.isGuest = isGuest;

        System.out.println("=== LEADERBOARD OPENED ===");
        System.out.println("Username: " + username);
        System.out.println("isGuest: " + isGuest);
        System.out.println("dbManager: " + (dbManager != null ? "OK" : "NULL"));

        initializeUI();
        loadLeaderboard();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("Leaderboard - Top Players");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyles.PANEL_BACKGROUND);

        // Title
        JLabel titleLabel = UIStyles.createTitleLabel("🏆 LEADERBOARD 🏆");
        titleLabel.setForeground(UIStyles.GOLD);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Create table
        leaderboardTable = new JTable();
        leaderboardTable.setFont(new Font("Arial", Font.BOLD, 14));
        leaderboardTable.setRowHeight(35);
        leaderboardTable.setGridColor(UIStyles.BLACK);
        leaderboardTable.setShowGrid(true);
        leaderboardTable.setSelectionBackground(new Color(255, 215, 0, 50));

        scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        scrollPane.getViewport().setBackground(UIStyles.WHITE);

        add(scrollPane, BorderLayout.CENTER);

        // UPDATED Bottom panel with styled buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton playAgainButton = UIStyles.createStyledButton("🎮 PLAY AGAIN", UIStyles.BUTTON_BLUE, 200, 50);
        playAgainButton.addActionListener(e -> {
            System.out.println("Play Again clicked");
            new Levels(dbManager, username, isGuest).setVisible(true);
            dispose();
        });

        JButton logoutButton = UIStyles.createStyledButton("🚪 LOGOUT", UIStyles.RED, 200, 50);
        logoutButton.addActionListener(e -> {
            System.out.println("Logout clicked");
            Session.logout();
            new Login(dbManager).setVisible(true);
            dispose();
        });

        bottomPanel.add(playAgainButton);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    // Note: createStyledButton method removed - now using UIStyles

    private void loadLeaderboard() {
        try {
            List<UserScore> scores = dbManager.getTopScores();
            System.out.println("Retrieved " + scores.size() + " scores");

            if (scores == null || scores.isEmpty()) {
                showEmptyLeaderboard();
                return;
            }

            // Create table data
            String[] columns = {"Rank", "Player", "Level", "Score"};
            Object[][] data = new Object[scores.size()][4];

            int rank = 1;
            for (int i = 0; i < scores.size(); i++) {
                UserScore score = scores.get(i);

                // Add medal emoji for top 3
                String rankText;
                if (rank == 1) {
                    rankText = "🥇 #1";
                } else if (rank == 2) {
                    rankText = "🥈 #2";
                } else if (rank == 3) {
                    rankText = "🥉 #3";
                } else {
                    rankText = "#" + rank;
                }

                data[i][0] = rankText;
                data[i][1] = score.getPlayerName();
                data[i][2] = score.getLevel();
                data[i][3] = score.getTotalScore() + " pts";
                rank++;
            }

            // Create table model
            DefaultTableModel model = new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            leaderboardTable.setModel(model);

            // Style the table header
            JTableHeader header = leaderboardTable.getTableHeader();
            header.setFont(new Font("Arial", Font.BOLD, 16));
            header.setBackground(UIStyles.BUTTON_BLUE);
            header.setForeground(UIStyles.WHITE);
            header.setBorder(UIStyles.LINE_BORDER);

            // Center align all columns
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            centerRenderer.setFont(new Font("Arial", Font.BOLD, 14));

            for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
                leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

            // Set column widths
            leaderboardTable.getColumnModel().getColumn(0).setPreferredWidth(60);
            leaderboardTable.getColumnModel().getColumn(1).setPreferredWidth(200);
            leaderboardTable.getColumnModel().getColumn(2).setPreferredWidth(150);
            leaderboardTable.getColumnModel().getColumn(3).setPreferredWidth(100);

            // Apply row highlighting
            applyRowHighlighting(scores);

        } catch (Exception e) {
            System.err.println("ERROR loading leaderboard: " + e.getMessage());
            e.printStackTrace();
            showErrorPanel("Error loading leaderboard: " + e.getMessage());
        }
    }

    private void applyRowHighlighting(List<UserScore> scores) {
        // Custom renderer to highlight current user's row
        DefaultTableCellRenderer highlightRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                String playerName = (String) table.getValueAt(row, 1);
                String currentPlayer = isGuest ? username + " (Guest)" : username;

                if (playerName != null && playerName.equals(currentPlayer)) {
                    c.setBackground(new Color(255, 215, 0, 50)); // Gold highlight
                    c.setFont(new Font("Arial", Font.BOLD, 14));
                } else {
                    c.setBackground(row % 2 == 0 ? UIStyles.WHITE : new Color(240, 248, 255));
                    c.setFont(new Font("Arial", Font.PLAIN, 14));
                }

                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return c;
            }
        };

        // Apply the renderer to all columns
        for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
            leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(highlightRenderer);
        }
    }

    private void showEmptyLeaderboard() {
        String[] columns = {"Rank", "Player", "Level", "Score"};
        Object[][] data = {{"", "No scores yet!", "", ""}};

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leaderboardTable.setModel(model);

        // Center the message
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setFont(new Font("Arial", Font.BOLD, 16));
        leaderboardTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
    }

    private void showErrorPanel(String message) {
        String[] columns = {"Error"};
        Object[][] data = {{"❌ " + message}};

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leaderboardTable.setModel(model);

        DefaultTableCellRenderer errorRenderer = new DefaultTableCellRenderer();
        errorRenderer.setHorizontalAlignment(JLabel.CENTER);
        errorRenderer.setForeground(UIStyles.RED);
        errorRenderer.setFont(new Font("Arial", Font.BOLD, 14));
        leaderboardTable.getColumnModel().getColumn(0).setCellRenderer(errorRenderer);
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JLabel createRowLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }
}