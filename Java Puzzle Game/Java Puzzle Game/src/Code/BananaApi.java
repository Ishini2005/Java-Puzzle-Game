package Code;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class BananaApi {

    private static final String BASE_API_URL = "https://marcconrad.com/uob/banana/api.php";

    public static boolean isApiAvailable() {
        try {
            String apiUrl = BASE_API_URL + "?out=csv&base64=yes";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);

            int responseCode = connection.getResponseCode();
            connection.disconnect();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public static String[] getGameData() throws Exception {
        String apiUrl = BASE_API_URL + "?out=csv&base64=yes";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = reader.readLine();
        reader.close();
        connection.disconnect();

        return response.split(",");
    }

    public static byte[] decodeImage(String base64Image) {
        return Base64.getDecoder().decode(base64Image);
    }
}