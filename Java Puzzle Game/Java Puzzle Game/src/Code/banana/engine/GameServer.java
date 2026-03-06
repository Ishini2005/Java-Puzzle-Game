package Code.banana.engine;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import javax.imageio.ImageIO;

/**
 * Game that interfaces to an external Server to retrieve a game.
 */
public class GameServer {

    /**
     * Basic utility method to read string for URL.
     */
    private static String readUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (Exception e) {
            System.out.println("An Error occurred: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a random game from the web site.
     * @return A random game or null if a game cannot be found.
     */
    public Game getRandomGame() {
        try {
            String tomatoApi = "https://marcconrad.com/uob/banana/api.php?out=csv&base64=yes";
            String dataRaw = readUrl(tomatoApi);

            if (dataRaw == null || dataRaw.isEmpty()) {
                return createFallbackGame();
            }

            String[] data = dataRaw.split(",");

            byte[] decodeImg = Base64.getDecoder().decode(data[0]);
            ByteArrayInputStream quest = new ByteArrayInputStream(decodeImg);
            int solution = Integer.parseInt(data[1]);

            BufferedImage img = ImageIO.read(quest);
            return new Game(img, solution);

        } catch (Exception e) {
            e.printStackTrace();
            return createFallbackGame();
        }
    }

    /**
     * Creates a fallback game when API fails
     */
    private Game createFallbackGame() {
        // Create a simple image with a math problem
        BufferedImage fallbackImage = new BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = fallbackImage.createGraphics();

        // Draw background
        g2d.setColor(java.awt.Color.WHITE);
        g2d.fillRect(0, 0, 400, 200);

        // Draw a simple equation
        g2d.setColor(java.awt.Color.BLACK);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
        g2d.drawString("5 + 3 = ?", 120, 100);

        g2d.dispose();

        return new Game(fallbackImage, 8); // 5+3=8
    }
}