package Code;

import javax.swing.*;
import Code.database.DataBaseManager;
import Code.banana.engine.Session;
import Code.banana.engine.UserFeedback;
import java.awt.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private DataBaseManager dbManager;

    public Login(DataBaseManager dbManager) {
        this.dbManager = dbManager;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Puzzle Game - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 200),
                        0, h, new Color(200, 200, 150));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Title
        JLabel titleLabel = new JLabel("🍌 PUZZLE GAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(100, 50, 0));
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(30));

        // Username field
        JPanel usernamePanel = createInputPanel("Username:", usernameField = new JTextField(15));
        formPanel.add(usernamePanel);
        formPanel.add(Box.createVerticalStrut(10));

        // Password field
        JPanel passwordPanel = createInputPanel("Password:", passwordField = new JPasswordField(15));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        loginButton = createButton("Login", new Color(70, 130, 180));
        registerButton = createButton("Register", new Color(60, 179, 113));

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegistration());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        formPanel.add(buttonPanel);

        mainPanel.add(formPanel);
        add(mainPanel);

        setSize(400, 450);
        setLocationRelativeTo(null);
    }

    private JPanel createInputPanel(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(new Color(100, 50, 0));

        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 100)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // FIXED: Removed SQLException catch
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            UserFeedback.showWarning("Please enter both username and password");
            return;
        }

        try {
            if (dbManager.login(username, password)) {
                Session.setLoggedInUser(username);
                UserFeedback.showInfo("Login successful! Welcome " + username);
                new Levels(dbManager, username).setVisible(true);
                dispose();
            } else {
                UserFeedback.showError("Invalid username or password");
            }
        } catch (Exception ex) {
            UserFeedback.showError("Database error: " + ex.getMessage());
        }
    }

    // FIXED: Removed SQLException catch
    private void handleRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            UserFeedback.showWarning("Please enter both username and password");
            return;
        }

        if (password.length() < 4) {
            UserFeedback.showWarning("Password must be at least 4 characters long");
            return;
        }

        try {
            if (dbManager.register(username, password)) {
                UserFeedback.showInfo("Registration successful! You can now login.");
                // Clear fields after successful registration
                usernameField.setText("");
                passwordField.setText("");
                usernameField.requestFocus();
            } else {
                // Check if username already exists
                if (dbManager.userExists(username)) {
                    UserFeedback.showError("Username '" + username + "' already exists! Please choose another username.");
                    usernameField.selectAll();
                    usernameField.requestFocus();
                } else {
                    UserFeedback.showError("Registration failed. Please try again.");
                }
            }
        } catch (Exception ex) {
            UserFeedback.showError("Database error: " + ex.getMessage());
        }
    }
}