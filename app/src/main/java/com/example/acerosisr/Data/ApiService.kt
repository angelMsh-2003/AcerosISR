package com.example.acerosisr.Data

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.delay // Added for simulating network latency

/**
 * WARNING: This JsonConverter is extremely simplified and fragile. 
 * In a real application, you should use a robust JSON serialization/deserialization library
 * such as kotlinx.serialization (recommended for Kotlin) or Gson.
 * It does not properly handle nested objects, arrays, escaped characters within strings, or complex data types.
 */
object JsonConverter {
    fun toJson(map: Map<String, Any?>): String {
        return map.entries.joinToString(prefix = "{", postfix = "}") { (k, v) ->
            val value = when (v) {
                is String -> "\"" + v.replace("\"", "\\\"") + "\""
                null -> "null"
                else -> v.toString()
            }
            "\"$k\":$value"
        }
    }

    fun fromJson(jsonString: String): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        // Basic attempt to parse a flat JSON object. Very fragile.
        if (jsonString.startsWith("{") && jsonString.endsWith("}")) {
            val cleanedJson = jsonString.substring(1, jsonString.length - 1)
            val entries = splitJsonEntries(cleanedJson)

            entries.forEach { entry ->
                val parts = entry.split(":", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].trim().removePrefix("\"").removeSuffix("\"")
                    var value: Any? = parts[1].trim()
                    if (value == "null") {
                        value = null
                    } else if (value.toString().startsWith("\"") && value.toString().endsWith("\"")) {
                        value = value.toString().removePrefix("\"").removeSuffix("\"")
                    } else if (value.toString().toLongOrNull() != null) {
                        value = value.toString().toLong()
                    } else if (value.toString().toDoubleOrNull() != null) {
                        value = value.toString().toDouble()
                    } else if (value.toString().toBooleanStrictOrNull() != null) {
                        value = value.toString().toBooleanStrictOrNull()
                    }
                    map[key] = value
                }
            }
        }
        return map
    }

    // Helper function to try and split entries, avoiding commas inside quoted strings
    internal fun splitJsonEntries(jsonContent: String): List<String> { // Changed to internal
        val entries = mutableListOf<String>()
        var inQuote = false
        var braceCount = 0 // To handle nested JSON objects (though this simple parser won't fully parse them)
        var bracketCount = 0 // To handle arrays
        var lastSplit = 0

        jsonContent.forEachIndexed { index, char ->
            when (char) {
                '\"' -> inQuote = !inQuote
                '{' -> if (!inQuote) braceCount++
                '}' -> if (!inQuote) braceCount--
                '[' -> if (!inQuote) bracketCount++
                ']' -> if (!inQuote) bracketCount--
                ',' -> {
                    if (!inQuote && braceCount == 0 && bracketCount == 0) {
                        entries.add(jsonContent.substring(lastSplit, index))
                        lastSplit = index + 1
                    }
                }
            }
        }
        entries.add(jsonContent.substring(lastSplit, jsonContent.length))
        return entries.map { it.trim() }.filter { it.isNotEmpty() }
    }
}

class ApiService(private val baseUrl: String) {

    // Mock implementation for development without a real backend
    private suspend fun mockRequest(endpoint: String, method: String, body: String?): Result<String> {
        delay(500) // Simulate network latency

        return when (endpoint) {
            "api/empleados/login" -> {
                val requestMap = body?.let { JsonConverter.fromJson(it) } ?: emptyMap()
                val numEmpleado = requestMap["numEmpleado"] as? Long
                val passwordPlaintext = requestMap["passwordPlaintext"] as? String
                
                if (numEmpleado == 1234567L && passwordPlaintext == "Admin321") {
                    Result.success("Login exitoso")
                } else if (numEmpleado == 1000000L && passwordPlaintext == "Pass123") {
                    Result.success("Login exitoso")
                } else {
                    Result.failure(RuntimeException("Credenciales inválidas (Mock)"))
                }
            }
            "api/empleados/count" -> Result.success("{\"count\":5}") // Mock 5 employees
            "api/empleados/1000000" -> Result.success("{\"NumEmpleado\":1000000,\"NombreEmpleado\":\"Usuario Mock\",\"Cargo\":\"trabajador\",\"Correo\":\"mock@example.com\",\"Password_hash\":\"hashedpass\",\"Salt\":\"saltvalue\",\"Estado\":1}")
            "api/actual-user" -> Result.success("{\"ID\":1,\"UserId\":1000000,\"NombreEmpleado\":\"Usuario Mock\",\"Cargo\":\"trabajador\"}") // Mock logged in user
            "api/empleados" -> { // For registerNewUser
                if (method == "POST") {
                     // Simulate successful registration
                    Result.success("Registro exitoso (Mock)")
                } else {
                    Result.failure(RuntimeException("Método no permitido (Mock)"))
                }
            }
            else -> Result.failure(RuntimeException("Endpoint no reconocido o no mockeado: $endpoint"))
        }
    }

    private fun makeRequest(endpoint: String, method: String, body: String? = null): Result<String> {
        // This is the real HTTP implementation. You can swap between real and mock here.
        // For now, let's keep both, and decide in MainActivity which one to use.
        return try {
            val url = URL("$baseUrl/$endpoint")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = method
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.setRequestProperty("Accept", "application/json")
            connection.connectTimeout = 10000 // 10 seconds
            connection.readTimeout = 10000 // 10 seconds

            if (body != null && (method == "POST" || method == "PUT")) {
                connection.doOutput = true
                val os: OutputStream = connection.outputStream
                val input = body.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            val responseMessage = connection.responseMessage

            if (responseCode in 200..299) {
                BufferedReader(InputStreamReader(connection.inputStream, Charsets.UTF_8)).use {
                    val response = it.readText()
                    Result.success(response)
                }
            } else {val errorStream = connection.errorStream
                val errorResponse = if (errorStream != null) {
                    BufferedReader(InputStreamReader(errorStream, Charsets.UTF_8)).use { reader ->
                        reader.readText()
                    }
                } else {
                    "Error: $responseCode - $responseMessage"
                }
                Result.failure(RuntimeException("HTTP Error: $responseCode - $responseMessage, Body: $errorResponse"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Overriding get/post/put/delete to use mockRequest if baseUrl is for mock
    suspend fun get(endpoint: String): Result<String> {
        return if (baseUrl == "mock-api-url") mockRequest(endpoint, "GET", null) else makeRequest(endpoint, "GET")
    }

    suspend fun post(endpoint: String, body: String): Result<String> {
        return if (baseUrl == "mock-api-url") mockRequest(endpoint, "POST", body) else makeRequest(endpoint, "POST", body)
    }

    suspend fun put(endpoint: String, body: String): Result<String> {
        return if (baseUrl == "mock-api-url") mockRequest(endpoint, "PUT", body) else makeRequest(endpoint, "PUT", body)
    }

    suspend fun delete(endpoint: String): Result<String> {
        return if (baseUrl == "mock-api-url") mockRequest(endpoint, "DELETE", null) else makeRequest(endpoint, "DELETE", null)
    }
}
