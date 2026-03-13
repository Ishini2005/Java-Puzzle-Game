package Code.banana.engine;

import javax.swing.*;

public class UserFeedback {
    public static void showInfo(String message) {
        JOptionPane.showMessageDialog(null, message, "Information",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(String message) {
        JOptionPane.showMessageDialog(null, message, "Warning",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showConfirm(String message) {
        return JOptionPane.showConfirmDialog(null, message, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}