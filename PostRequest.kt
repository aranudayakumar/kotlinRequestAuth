import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

fun main() {
    try {
        // Step 1: Create a new user by sending a POST request to the /users/ endpoint
        val userUrl = URL("http://127.0.0.1:8000/users/")
        val conUser = userUrl.openConnection() as HttpURLConnection

        conUser.requestMethod = "POST"
        conUser.setRequestProperty("Content-Type", "application/json; utf-8")
        conUser.setRequestProperty("Accept", "application/json")
        conUser.doOutput = true

        val jsonUserInputString = """{"username": "new_us399wddwer", "password": "new_password123"}"""

        DataOutputStream(conUser.outputStream).use { out ->
            out.writeBytes(jsonUserInputString)
            out.flush()
        }

        val userStatus = conUser.responseCode
        println("User Creation Response Code: $userStatus")

        val userResponse = if (userStatus in 200..299) {
            BufferedReader(InputStreamReader(conUser.inputStream, StandardCharsets.UTF_8)).use { reader ->
                reader.readText()
            }
        } else {
            BufferedReader(InputStreamReader(conUser.errorStream, StandardCharsets.UTF_8)).use { reader ->
                reader.readText()
            }
        }

        println("User Creation Response: $userResponse")
        conUser.disconnect()

        // Step 2: Obtain a token by sending a POST request to the /api/token endpoint
        val tokenUrl = URL("http://127.0.0.1:8000/api/token")
        val conToken = tokenUrl.openConnection() as HttpURLConnection

        conToken.requestMethod = "POST"
        conToken.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; utf-8")
        conToken.setRequestProperty("Accept", "application/json")
        conToken.doOutput = true

        val formInputString = "username=new_us399wddwer&password=new_password123"

        DataOutputStream(conToken.outputStream).use { out ->
            out.writeBytes(formInputString)
            out.flush()
        }

        val tokenStatus = conToken.responseCode
        println("Token Generation Response Code: $tokenStatus")

        val tokenResponse = if (tokenStatus in 200..299) {
            BufferedReader(InputStreamReader(conToken.inputStream, StandardCharsets.UTF_8)).use { reader ->
                reader.readText()
            }
        } else {
            BufferedReader(InputStreamReader(conToken.errorStream, StandardCharsets.UTF_8)).use { reader ->
                reader.readText()
            }
        }

        println("Token Generation Response: $tokenResponse")
        conToken.disconnect()

        // Extract the token from the response
        val token = extractTokenFromResponse(tokenResponse)

        // Step 3: Access the /items/ endpoint using the obtained token
        val strQueryParam = "example_value"  // Replace with the actual value you want to pass
        val itemsUrl = URL("http://127.0.0.1:8000/items/?str=$strQueryParam")
        val conItems = itemsUrl.openConnection() as HttpURLConnection

        conItems.requestMethod = "POST"
        conItems.setRequestProperty("Authorization", "Bearer $token")
        conItems.setRequestProperty("Accept", "application/json")

        val itemsStatus = conItems.responseCode
        println("Items Endpoint Response Code: $itemsStatus")

        val itemsResponse = if (itemsStatus in 200..299) {
            BufferedReader(InputStreamReader(conItems.inputStream, StandardCharsets.UTF_8)).use { reader ->
                reader.readText()
            }
        } else {
            BufferedReader(InputStreamReader(conItems.errorStream, StandardCharsets.UTF_8)).use { reader ->
                reader.readText()
            }
        }

        println("Items Endpoint Response: $itemsResponse")
        conItems.disconnect()

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun extractTokenFromResponse(response: String): String {
    // Assuming the token is in the response like {"access_token":"<TOKEN>","token_type":"bearer"}
    val tokenPart = response.split(",")[0]
    return tokenPart.split(":")[1].replace("\"", "")
}
