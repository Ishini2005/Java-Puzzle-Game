package Code.banana.engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Images {
    private ArrayList<BufferedImage> imageArray;
    private Random random;

    public Images() throws IOException {
        imageArray = new ArrayList<>();
        random = new Random();

        // Try to load images from resources
        loadImagesFromResources();

        // If no images loaded, create fallback images
        if (imageArray.isEmpty()) {
            createFallbackImages();
        }
    }

    private void loadImagesFromResources() {
        try {
            // Try to load from classpath
            java.net.URL resource = getClass().getResource("/images/");
            if (resource != null) {
                File imagesDir = new File(resource.toURI());
                File[] files = imagesDir.listFiles((dir, name) ->
                        name.toLowerCase().endsWith(".png") ||
                                name.toLowerCase().endsWith(".jpg") ||
                                name.toLowerCase().endsWith(".jpeg"));

                if (files != null) {
                    for (File file : files) {
                        BufferedImage img = ImageIO.read(file);
                        if (img != null) {
                            imageArray.add(img);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Could not load images from resources: " + e.getMessage());
        }
    }

    private void createFallbackImages() {
        // Create 10 different colored images with patterns
        for (int i = 0; i < 10; i++) {
            imageArray.add(createPatternImage(i));
        }
    }

    private BufferedImage createPatternImage(int seed) {
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = img.createGraphics();

        // Create gradient background
        java.awt.Color color1 = new java.awt.Color(
                (seed * 50) % 255,
                (seed * 80) % 255,
                (seed * 110) % 255
        );
        java.awt.Color color2 = new java.awt.Color(
                (seed * 70) % 255,
                (seed * 40) % 255,
                (seed * 130) % 255
        );

        java.awt.GradientPaint gradient = new java.awt.GradientPaint(
                0, 0, color1, 100, 100, color2
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 100, 100);

        // Draw pattern
        g2d.setColor(java.awt.Color.WHITE);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
        g2d.drawString(String.valueOf(seed + 1), 35, 60);

        g2d.dispose();
        return img;
    }

    public BufferedImage getRandomImage() {
        if (!imageArray.isEmpty()) {
            return imageArray.get(random.nextInt(imageArray.size()));
        }
        return null;
    }

    public BufferedImage getImageByHashCode(int hashCode) {
        for (BufferedImage img : imageArray) {
            if (img.hashCode() == hashCode) {
                return img;
            }
        }
        return null;
    }

    public int getImageCount() {
        return imageArray.size();
    }
}