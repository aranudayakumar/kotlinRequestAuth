import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

fun main() {
    try {
        // Define the URL for the /users/ endpoint
        val url = URL("http://127.0.0.1:8000/users/")
        val con = url.openConnection() as HttpURLConnection

        // Set the request method to POST
        con.requestMethod = "POST"
        con.setRequestProperty("Content-Type", "application/json; utf-8")
        con.setRequestProperty("Accept", "application/json")
        con.doOutput = true

        // Create JSON request body to create a new user
        val jsonInputString = """{"username": "new_uwe4ser", "password": "new_password123"}"""

        // Send the JSON input string
        DataOutputStream(con.outputStream).use { out ->
            out.writeBytes(jsonInputString)
            out.flush()
        }

        // Get the response code
        val status = con.responseCode
        println("Response Code: $status")

        val response = if (status in 200..299) {
            // Read the response for a successful request
            BufferedReader(InputStreamReader(con.inputStream, StandardCharsets.UTF_8)).use { reader ->
                reader.readText()
            }
        } else {
            // Read the error stream for an unsuccessful request
            BufferedReader(InputStreamReader(con.errorStream, StandardCharsets.UTF_8)).use { reader ->
                reader.readText()
            }
        }

        // Print the response content
        println("Response: $response")

        // Close the connection
        con.disconnect()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
