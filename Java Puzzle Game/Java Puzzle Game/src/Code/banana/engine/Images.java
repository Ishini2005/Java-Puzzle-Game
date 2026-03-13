package Code.banana.engine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Images {
    private List<BufferedImage> images;
    private Random random;

    public Images() {
        images = new ArrayList<>();
        random = new Random();
        loadImagesFromUrls();  // <-- CALL THIS METHOD HERE
    }

    /**
     * NEW METHOD: Load images from internet URLs
     */
    private void loadImagesFromUrls() {
        String[] imageUrls = {
                "https://cdn-icons-png.flaticon.com/512/415/415682.png",  // Apple
                "https://cdn-icons-png.flaticon.com/512/415/415731.png",  // Banana
                "https://cdn-icons-png.flaticon.com/512/415/415733.png",  // Cherry
                "https://cdn-icons-png.flaticon.com/512/415/415710.png",  // Grapes
                "https://cdn-icons-png.flaticon.com/512/415/415689.png",  // Orange
                "https://cdn-icons-png.flaticon.com/512/415/415692.png",  // Pear
                "https://cdn-icons-png.flaticon.com/512/415/415698.png",  // Pineapple
                "https://cdn-icons-png.flaticon.com/512/415/415704.png"   // Strawberry
        };

        System.out.println("Loading images from URLs...");

        for (String url : imageUrls) {
            try {
                URL imageUrl = new URL(url);
                BufferedImage img = ImageIO.read(imageUrl);
                if (img != null) {
                    BufferedImage resized = resizeImage(img, 100, 100);
                    images.add(resized);
                    System.out.println("✅ Loaded: " + url.substring(url.lastIndexOf("/") + 1));
                }
            } catch (Exception e) {
                System.err.println("❌ Error loading from URL: " + url);
                System.err.println("   Error: " + e.getMessage());
            }
        }

        // If URLs failed, create default colored images
        if (images.isEmpty()) {
            System.out.println("No images loaded from URLs, creating default images");
            createDefaultImages();
        } else {
            System.out.println("✅ Successfully loaded " + images.size() + " images");
        }
    }

    /**
     * Helper method to resize images
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resizedImage;
    }

    /**
     * Fallback method to create colored images if URLs fail
     */
    private void createDefaultImages() {
        int[][] colors = {
                {0xFF5733, 0x33FF57, 0x3357FF, 0xFF33F5},
                {0xFFD733, 0x33FFF5, 0xFF8333, 0x8E44AD}
        };

        for (int[] colorRow : colors) {
            for (int color : colorRow) {
                images.add(createColoredImage(100, 100, color));
            }
        }
        System.out.println("Created " + images.size() + " default colored images");
    }

    private BufferedImage createColoredImage(int width, int height, int rgb) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setColor(new Color(rgb));
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, width-1, height-1);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String text = String.valueOf(images.size() + 1);
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);

        g2d.dispose();
        return img;
    }

    public BufferedImage getRandomImage() {
        if (images.isEmpty()) return null;
        return images.get(random.nextInt(images.size()));
    }

    public int getImageCount() {
        return images.size();
    }

    /**
     * Get image by ID (for matching game)
     */
    public BufferedImage getImageById(int id) {
        if (images.isEmpty() || id < 0 || id >= images.size()) {
            return null;
        }
        return images.get(id);
    }
}