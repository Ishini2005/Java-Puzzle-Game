package Code;

import Code.database.DataBaseManager;
import Code.banana.engine.Session;
import Code.banana.engine.UserFeedback;
import Code.banana.engine.UIStyles;  // Added UIStyles import
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private DataBaseManager dbManager;

    public Login(DataBaseManager dbManager) {
        this.dbManager = dbManager;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Login - Puzzle Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(255, 255, 204));

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // Title - Using UIStyles
        JLabel titleLabel = UIStyles.createTitleLabel("🧩 PUZZLE GAME 🧩");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 30, 20);
        mainPanel.add(titleLabel, gbc);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(5, 5, 5, 5);

        // Username label - Updated font
        fgbc.gridx = 0;
        fgbc.gridy = 0;
        fgbc.anchor = GridBagConstraints.EAST;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(usernameLabel, fgbc);

        fgbc.gridx = 1;
        fgbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(usernameField, fgbc);

        // Password label - Updated font
        fgbc.gridx = 0;
        fgbc.gridy = 1;
        fgbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(passwordLabel, fgbc);

        fgbc.gridx = 1;
        fgbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(passwordField, fgbc);

        gbc.gridy = 1;
        mainPanel.add(formPanel, gbc);

        // Button panel - Using UIStyles for all buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        // Login button - Using UIStyles
        JButton loginButton = UIStyles.createLoginButton("LOGIN", UIStyles.BUTTON_BLUE);
        loginButton.addActionListener(e -> handleLogin());
        buttonPanel.add(loginButton);

        // Register button - Using UIStyles
        JButton registerButton = UIStyles.createLoginButton("REGISTER", UIStyles.BUTTON_GREEN);
        registerButton.addActionListener(e -> handleRegister());
        buttonPanel.add(registerButton);

        // Guest button - Using UIStyles
        JButton guestButton = UIStyles.createLoginButton("PLAY AS GUEST", UIStyles.BUTTON_ORANGE);
        guestButton.addActionListener(e -> {
            new GuestLogin(dbManager);
            dispose();
        });
        buttonPanel.add(guestButton);

        gbc.gridy = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        setSize(650, 550);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Note: createStyledButton method removed - now using UIStyles

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
                Session.setGuestMode(false);
                UserFeedback.showInfo("Welcome back, " + username + "!");
                new Levels(dbManager, username, false);
                dispose();
            } else {
                UserFeedback.showError("Invalid username or password");
            }
        } catch (SQLException ex) {
            UserFeedback.showError("Database error: " + ex.getMessage());
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            UserFeedback.showWarning("Please enter both username and password");
            return;
        }

        if (password.length() < 8) {
            UserFeedback.showWarning("Password must be at least 8 characters long");
            return;
        }

        try {
            String email = JOptionPane.showInputDialog(this, "Enter your email:",
                    "Registration", JOptionPane.QUESTION_MESSAGE);

            if (email == null || email.trim().isEmpty()) {
                UserFeedback.showWarning("Email is required for registration");
                return;
            }

            if (dbManager.register(username, password, email)) {
                UserFeedback.showInfo("Registration successful! Please login.");
                usernameField.setText("");
                passwordField.setText("");
            } else {
                UserFeedback.showError("Registration failed. Username might already exist.");
            }
        } catch (SQLException ex) {
            UserFeedback.showError("Database error: " + ex.getMessage());
        }
    }
}