package Code.banana.engine;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class UIStyles {

    // Colors
    public static final Color DARK_BLUE = new Color(0, 102, 204);
    public static final Color LIGHT_BLUE = new Color(173, 216, 230);
    public static final Color GOLD = new Color(255, 215, 0);
    public static final Color WHITE = Color.WHITE;
    public static final Color BLACK = Color.BLACK;
    public static final Color RED = new Color(220, 20, 60);
    public static final Color GREEN = new Color(34, 139, 34);
    public static final Color PANEL_BACKGROUND = new Color(255, 255, 200);
    public static final Color BUTTON_BLUE = new Color(70, 130, 180);    // Steel Blue
    public static final Color BUTTON_GREEN = new Color(60, 179, 113);   // Medium Sea Green
    public static final Color BUTTON_ORANGE = new Color(255, 165, 0);   // Orange
    public static final Color BUTTON_PURPLE = new Color(128, 0, 128);   // Purple
    public static final Color BUTTON_BROWN = new Color(139, 69, 19);    // Brown
    public static final Color BUTTON_GRAY = new Color(128, 128, 128);   // Gray

    // Fonts - ALL BOLD
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font SMALL_BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font GAME_BUTTON_FONT = new Font("Arial", Font.BOLD, 36);

    // Borders
    public static final Border RAISED_BORDER = BorderFactory.createRaisedBevelBorder();
    public static final Border LOWERED_BORDER = BorderFactory.createLoweredBevelBorder();
    public static final Border LINE_BORDER = BorderFactory.createLineBorder(BLACK, 2);
    public static final Border THICK_LINE_BORDER = BorderFactory.createLineBorder(BLACK, 3);

    /**
     * Create a styled button with BOLD BLACK text
     */
    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(BLACK);  // BLACK text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                THICK_LINE_BORDER,
                RAISED_BORDER
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 50));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Create a styled button with custom size and BOLD BLACK text
     */
    public static JButton createStyledButton(String text, Color bgColor, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(BLACK);  // BLACK text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                THICK_LINE_BORDER,
                RAISED_BORDER
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Create a small styled button with BOLD BLACK text
     */
    public static JButton createSmallButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(SMALL_BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(BLACK);  // BLACK text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                LINE_BORDER,
                RAISED_BORDER
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 40));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Create a login button with BOLD BLACK text
     */
    public static JButton createLoginButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(BLACK);  // BLACK text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                THICK_LINE_BORDER,
                RAISED_BORDER
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 50));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Create a game tile button with BOLD BLACK text
     */
    public static JButton createGameButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(100, 100));
        button.setBackground(WHITE);
        button.setFont(GAME_BUTTON_FONT);
        button.setForeground(BLACK);  // BLACK text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                THICK_LINE_BORDER,
                RAISED_BORDER
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Create an info label with BOLD text
     */
    public static JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(LABEL_FONT);
        label.setOpaque(true);
        label.setBackground(PANEL_BACKGROUND);
        label.setForeground(BLACK);
        label.setBorder(BorderFactory.createCompoundBorder(
                LINE_BORDER,
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return label;
    }

    /**
     * Create a title label with BOLD text
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(TITLE_FONT);
        label.setForeground(DARK_BLUE);
        return label;
    }
}