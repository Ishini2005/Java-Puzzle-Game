package Code;

import Code.database.DataBaseManager;
import Code.banana.engine.UserFeedback;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            System.out.println("🍌 Starting Puzzle Game...");

            // Initialize database connection
            DataBaseManager dbManager = new DataBaseManager();

            if (dbManager.isConnected()) {
                System.out.println("✅ Database connected successfully");

                // Test Banana API connection in background
                testBananaApi();

                // Start with login screen
                SwingUtilities.invokeLater(() -> {
                    new Login(dbManager).setVisible(true);
                });
            } else {
                UserFeedback.showError("Cannot connect to database. Please make sure MySQL is running.");
                System.exit(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            UserFeedback.showError("Error starting application: " + e.getMessage());
        }
    }

    private static void testBananaApi() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return BananaApi.isApiAvailable();
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        System.out.println("✅ Banana API is available");
                    } else {
                        System.out.println("⚠️ Banana API is not available - using fallback mode");
                    }
                } catch (Exception e) {
                    System.out.println("❌ Could not check Banana API");
                }
            }
        };
        worker.execute();
    }
}