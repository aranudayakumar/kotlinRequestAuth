import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PostRequest {
    public static void main(String[] args) {
        try {
            // Step 1: Create a new user by sending a POST request to the /users/ endpoint
            URL userUrl = new URL("http://127.0.0.1:8000/users/");
            HttpURLConnection conUser = (HttpURLConnection) userUrl.openConnection();

            conUser.setRequestMethod("POST");
            conUser.setRequestProperty("Content-Type", "application/json; utf-8");
            conUser.setRequestProperty("Accept", "application/json");
            conUser.setDoOutput(true);

            String jsonUserInputString = "{\"username\": \"new_us399wddwer\", \"password\": \"new_password123\"}";

            try (DataOutputStream out = new DataOutputStream(conUser.getOutputStream())) {
                out.writeBytes(jsonUserInputString);
                out.flush();
            }

            int userStatus = conUser.getResponseCode();
            System.out.println("User Creation Response Code: " + userStatus);

            BufferedReader inUser;
            if (userStatus >= 200 && userStatus < 300) {
                inUser = new BufferedReader(new InputStreamReader(conUser.getInputStream(), StandardCharsets.UTF_8));
            } else {
                inUser = new BufferedReader(new InputStreamReader(conUser.getErrorStream(), StandardCharsets.UTF_8));
            }

            String inputLine;
            StringBuilder userContent = new StringBuilder();
            while ((inputLine = inUser.readLine()) != null) {
                userContent.append(inputLine);
            }

            inUser.close();
            conUser.disconnect();

            System.out.println("User Creation Response: " + userContent.toString());

            // Step 2: Obtain a token by sending a POST request to the /api/token endpoint
            URL tokenUrl = new URL("http://127.0.0.1:8000/api/token");
            HttpURLConnection conToken = (HttpURLConnection) tokenUrl.openConnection();

            conToken.setRequestMethod("POST");
            conToken.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; utf-8");
            conToken.setRequestProperty("Accept", "application/json");
            conToken.setDoOutput(true);

            String formInputString = "username=new_us399wddwer&password=new_password123";

            try (DataOutputStream out = new DataOutputStream(conToken.getOutputStream())) {
                out.writeBytes(formInputString);
                out.flush();
            }

            int tokenStatus = conToken.getResponseCode();
            System.out.println("Token Generation Response Code: " + tokenStatus);

            BufferedReader inToken;
            if (tokenStatus >= 200 && tokenStatus < 300) {
                inToken = new BufferedReader(new InputStreamReader(conToken.getInputStream(), StandardCharsets.UTF_8));
            } else {
                inToken = new BufferedReader(new InputStreamReader(conToken.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder tokenContent = new StringBuilder();
            while ((inputLine = inToken.readLine()) != null) {
                tokenContent.append(inputLine);
            }

            inToken.close();
            conToken.disconnect();

            System.out.println("Token Generation Response: " + tokenContent.toString());

            // Extract the token from the response
            String token = extractTokenFromResponse(tokenContent.toString());

            // Step 3: Access the /items/ endpoint using the obtained token
            String strQueryParam = "example_value";  // Replace with the actual value you want to pass
            URL itemsUrl = new URL("http://127.0.0.1:8000/items/?str=" + strQueryParam);
            HttpURLConnection conItems = (HttpURLConnection) itemsUrl.openConnection();

            conItems.setRequestMethod("POST");
            conItems.setRequestProperty("Authorization", "Bearer " + token);
            conItems.setRequestProperty("Accept", "application/json");

            int itemsStatus = conItems.getResponseCode();
            System.out.println("Items Endpoint Response Code: " + itemsStatus);

            BufferedReader inItems;
            if (itemsStatus >= 200 && itemsStatus < 300) {
                inItems = new BufferedReader(new InputStreamReader(conItems.getInputStream(), StandardCharsets.UTF_8));
            } else {
                inItems = new BufferedReader(new InputStreamReader(conItems.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder itemsContent = new StringBuilder();
            while ((inputLine = inItems.readLine()) != null) {
                itemsContent.append(inputLine);
            }

            inItems.close();
            conItems.disconnect();

            System.out.println("Items Endpoint Response: " + itemsContent.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String extractTokenFromResponse(String response) {
        // Assuming the token is in the response like {"access_token":"<TOKEN>","token_type":"bearer"}
        String[] parts = response.split(",");
        String tokenPart = parts[0];
        return tokenPart.split(":")[1].replaceAll("\"", "");
    }
}
