package Code.banana.engine;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import javax.imageio.ImageIO;

public class GameServer {

    private String readUrl(String urlString) {
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
            System.out.println("Error reading URL: " + e.getMessage());
            return null;
        }
    }

    public Game getRandomGame() {
        try {
            String apiUrl = "https://marcconrad.com/uob/banana/api.php?out=csv&base64=yes";
            String dataRaw = readUrl(apiUrl);

            if (dataRaw == null) return null;

            String[] data = dataRaw.split(",");
            if (data.length < 2) return null;

            byte[] decodeImg = Base64.getDecoder().decode(data[0]);
            ByteArrayInputStream quest = new ByteArrayInputStream(decodeImg);
            int solution = Integer.parseInt(data[1].trim());

            BufferedImage img = ImageIO.read(quest);
            return new Game(img, solution);

        } catch (Exception e) {
            System.out.println("Error getting game: " + e.getMessage());
            return null;
        }
    }
}