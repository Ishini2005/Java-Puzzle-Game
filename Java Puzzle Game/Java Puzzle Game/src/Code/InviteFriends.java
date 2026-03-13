package Code;

import Code.database.DataBaseManager;
import Code.banana.engine.UserFeedback;
import Code.banana.engine.UIStyles;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class InviteFriends extends JFrame {
    private DataBaseManager dbManager;
    private String username;
    private JTextField emailField;

    public InviteFriends(DataBaseManager dbManager, String username) {
        this.dbManager = dbManager;
        this.username = username;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Invite Friends - Puzzle Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyles.PANEL_BACKGROUND);

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = UIStyles.createTitleLabel("📧 INVITE FRIENDS 📧");
        titleLabel.setForeground(UIStyles.BUTTON_ORANGE);
        titlePanel.add(titleLabel);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Info message
        JLabel infoLabel = new JLabel(
                "<html><center><h3>Invite your friends to play!</h3>" +
                        "When they join using your invite, you both get bonus points!</center></html>"
        );
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(infoLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Invite code display
        String inviteCode = generateInviteCode();
        JPanel codePanel = new JPanel(new FlowLayout());
        codePanel.setOpaque(false);

        JLabel codeLabel = new JLabel("Your Invite Code:");
        codeLabel.setFont(UIStyles.LABEL_FONT);

        JTextField codeField = new JTextField(inviteCode, 10);
        codeField.setFont(new Font("Arial", Font.BOLD, 16));
        codeField.setEditable(false);
        codeField.setHorizontalAlignment(JTextField.CENTER);
        codeField.setBackground(new Color(255, 255, 200));
        codeField.setBorder(UIStyles.LINE_BORDER);

        // UPDATED: Copy button using UIStyles
        JButton copyCodeButton = UIStyles.createSmallButton("📋 COPY", UIStyles.BUTTON_BLUE);
        copyCodeButton.addActionListener(e -> {
            StringSelection selection = new StringSelection(inviteCode);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            UserFeedback.showInfo("Invite code copied to clipboard!");
        });

        codePanel.add(codeLabel);
        codePanel.add(codeField);
        codePanel.add(copyCodeButton);

        mainPanel.add(codePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Email input panel
        JPanel emailPanel = new JPanel(new FlowLayout());
        emailPanel.setOpaque(false);

        JLabel emailLabel = new JLabel("Friend's Email:");
        emailLabel.setFont(UIStyles.LABEL_FONT);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                UIStyles.LINE_BORDER,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        mainPanel.add(emailPanel);

        // UPDATED: Send invite button
        JButton sendButton = UIStyles.createStyledButton("📧 SEND INVITATION", UIStyles.BUTTON_GREEN, 250, 50);
        sendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sendButton.addActionListener(e -> sendInvitation(inviteCode));
        mainPanel.add(sendButton);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Share link button
        JButton shareButton = UIStyles.createStyledButton("🔗 COPY INVITE LINK", UIStyles.BUTTON_BLUE, 250, 50);
        shareButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        shareButton.addActionListener(e -> copyInviteLink(inviteCode));
        mainPanel.add(shareButton);

        // Bonus info
        JPanel bonusPanel = new JPanel();
        bonusPanel.setOpaque(false);
        bonusPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyles.BLACK, 2),
                "🎁 Bonus Rewards",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                UIStyles.LABEL_FONT,
                UIStyles.BUTTON_ORANGE
        ));
        bonusPanel.setLayout(new BoxLayout(bonusPanel, BoxLayout.Y_AXIS));

        JLabel bonus1 = new JLabel("• You get 50 bonus points when friend registers");
        bonus1.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel bonus2 = new JLabel("• Friend gets 25 bonus points");
        bonus2.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel bonus3 = new JLabel("• Both get 10% score boost for first game");
        bonus3.setFont(new Font("Arial", Font.BOLD, 12));

        bonusPanel.add(bonus1);
        bonusPanel.add(bonus2);
        bonusPanel.add(bonus3);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(bonusPanel);

        add(titlePanel, BorderLayout.NORTH);
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        setSize(600, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Note: createStyledButton method removed - now using UIStyles

    private void sendInvitation(String inviteCode) {
        String email = emailField.getText().trim();

        if (email.isEmpty() || !email.contains("@")) {
            UserFeedback.showWarning("Please enter a valid email address");
            return;
        }

        String code = dbManager.createInvitation(username, email);

        if (code != null) {
            String message = String.format(
                    "✅ Invitation sent to %s!\n\n" +
                            "Invite Code: %s\n\n" +
                            "Your friend will receive:\n" +
                            "• 25 bonus points on registration\n" +
                            "• 10% score boost for first game\n\n" +
                            "You'll get 50 bonus points when they join!",
                    email, code
            );
            UserFeedback.showInfo(message);
            emailField.setText("");
        } else {
            UserFeedback.showError("Failed to send invitation. Please try again.");
        }
    }

    private void copyInviteLink(String inviteCode) {
        String inviteLink = "https://puzzlegame.com/join?code=" + inviteCode + "&inviter=" + username;

        StringSelection stringSelection = new StringSelection(inviteLink);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        UserFeedback.showInfo(
                "✅ Invite link copied to clipboard!\n\n" +
                        "Share this link with your friends:\n" + inviteLink
        );
    }

    private String generateInviteCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = (int)(Math.random() * chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }
}