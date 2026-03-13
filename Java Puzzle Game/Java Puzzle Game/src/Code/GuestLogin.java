package Code;

import Code.database.DataBaseManager;
import Code.banana.engine.Session;
import Code.banana.engine.UserFeedback;
import Code.banana.engine.UIStyles;
import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class GuestLogin extends JFrame {
    private JTextField guestNameField;
    private DataBaseManager dbManager;

    public GuestLogin(DataBaseManager dbManager) {
        this.dbManager = dbManager;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Guest Login - Puzzle Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyles.PANEL_BACKGROUND);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // Title
        JLabel titleLabel = UIStyles.createTitleLabel("🎮 PLAY AS GUEST 🎮");
        titleLabel.setForeground(UIStyles.BUTTON_ORANGE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 10, 20);
        mainPanel.add(titleLabel, gbc);

        // Info label
        JLabel infoLabel = new JLabel("<html><center>Play without registration!<br>Your scores will be saved with your guest name.</center></html>");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setForeground(UIStyles.BLACK);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 20, 20);
        mainPanel.add(infoLabel, gbc);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(5, 5, 5, 5);

        // Guest name field
        fgbc.gridx = 0;
        fgbc.gridy = 0;
        fgbc.anchor = GridBagConstraints.EAST;
        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setFont(UIStyles.LABEL_FONT);
        formPanel.add(nameLabel, fgbc);

        fgbc.gridx = 1;
        fgbc.anchor = GridBagConstraints.WEST;
        guestNameField = new JTextField(15);
        guestNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        guestNameField.setBorder(BorderFactory.createCompoundBorder(
                UIStyles.LINE_BORDER,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        guestNameField.setText("Guest_" + UUID.randomUUID().toString().substring(0, 5));
        formPanel.add(guestNameField, fgbc);

        gbc.gridy = 2;
        mainPanel.add(formPanel, gbc);

        // Button panel - UPDATED with UIStyles buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton playButton = UIStyles.createStyledButton("▶ START PLAYING", UIStyles.BUTTON_GREEN, 200, 50);
        playButton.addActionListener(e -> handleGuestLogin());
        buttonPanel.add(playButton);

        JButton backButton = UIStyles.createStyledButton("← BACK TO LOGIN", UIStyles.BUTTON_BLUE, 200, 50);
        backButton.addActionListener(e -> {
            new Login(dbManager);
            dispose();
        });
        buttonPanel.add(backButton);

        gbc.gridy = 3;
        gbc.insets = new Insets(20, 20, 20, 20);
        mainPanel.add(buttonPanel, gbc);

        // Features panel
        JPanel featuresPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        featuresPanel.setOpaque(false);
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        featuresPanel.add(createFeaturePanel("🎯", "Play Games", "Access all levels"));
        featuresPanel.add(createFeaturePanel("📊", "Save Scores", "Track your progress"));
        featuresPanel.add(createFeaturePanel("🏆", "Leaderboard", "Compete with others"));

        gbc.gridy = 4;
        mainPanel.add(featuresPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        setSize(700, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createFeaturePanel(String icon, String title, String desc) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(UIStyles.LINE_BORDER);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyles.SMALL_BUTTON_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(10));
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(titleLabel);
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    // Note: createStyledButton method removed - now using UIStyles

    private void handleGuestLogin() {
        String guestName = guestNameField.getText().trim();

        if (guestName.isEmpty()) {
            UserFeedback.showWarning("Please enter a name to continue");
            return;
        }

        if (dbManager.guestExists(guestName)) {
            int option = JOptionPane.showConfirmDialog(this,
                    "This guest name already exists. Do you want to continue?\n" +
                            "Your scores will be saved under this name.",
                    "Name Exists",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }

        String sessionId = dbManager.createGuestUser(guestName);
        if (sessionId != null) {
            Session.setLoggedInUser(guestName);
            Session.setGuestMode(true);
            Session.setSessionId(sessionId);

            UserFeedback.showInfo("Welcome, " + guestName + "! (Guest Mode)\n\n" +
                    "You can now play all levels and your scores will be saved.");

            new Levels(dbManager, guestName, true);
            dispose();
        } else {
            UserFeedback.showError("Failed to create guest session. Please try again.");
        }
    }
}