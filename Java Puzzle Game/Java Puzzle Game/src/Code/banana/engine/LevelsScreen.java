package Code.banana.engine;

import javax.swing.JOptionPane;

public class LevelsScreen {

    public void showWelcomeMessage() {
        String username = Session.getLoggedInUser();

        if (Session.isFirstLogin()) {
            // Show the "Welcome" message for the first login
            JOptionPane.showMessageDialog(null, "Welcome " + username,
                    "Welcome", JOptionPane.INFORMATION_MESSAGE);
            // Set the flag to false to indicate it's no longer the first login
            Session.setFirstLoginFalse();
        } else {
            // Show the "Welcome Back" message for subsequent logins
            JOptionPane.showMessageDialog(null, "Welcome back " + username,
                    "Welcome", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showLevelSelection() {
        String message = "Select a difficulty level to begin:\n" +
                "1. Easy - 4x4 grid, 120 seconds\n" +
                "2. Intermediate - 6x6 grid, 90 seconds\n" +
                "3. Advanced - 8x8 grid, 60 seconds";

        JOptionPane.showMessageDialog(null, message,
                "Level Selection", JOptionPane.INFORMATION_MESSAGE);
    }
}