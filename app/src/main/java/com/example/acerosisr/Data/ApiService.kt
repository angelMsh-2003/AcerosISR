package com.example.acerosisr.Data

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * WARNING: This JsonConverter is extremadamente simplificado y frágil.
 * Úsalo solo para pruebas. En producción deberías usar kotlinx.serialization o Gson/Moshi.
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

    // Helper para separar entradas sin romper comas dentro de strings/objetos
    internal fun splitJsonEntries(jsonContent: String): List<String> {
        val entries = mutableListOf<String>()
        var inQuote = false
        var braceCount = 0
        var bracketCount = 0
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

    private fun makeRequest(
        endpoint: String,
        method: String,
        body: String? = null
    ): Result<String> {
        return try {
            val url = URL("${baseUrl.trimEnd('/')}/$endpoint")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = method
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.setRequestProperty("Accept", "application/json")
            connection.connectTimeout = 10000 // 10 segundos
            connection.readTimeout = 10000    // 10 segundos

            if (body != null && (method == "POST" || method == "PUT")) {
                connection.doOutput = true
                val os: OutputStream = connection.outputStream
                val input = body.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
                os.flush()
                os.close()
            }

            val responseCode = connection.responseCode
            val responseMessage = connection.responseMessage

            if (responseCode in 200..299) {
                BufferedReader(InputStreamReader(connection.inputStream, Charsets.UTF_8)).use {
                    val response = it.readText()
                    Result.success(response)
                }
            } else {
                val errorStream = connection.errorStream
                val errorResponse = if (errorStream != null) {
                    BufferedReader(InputStreamReader(errorStream, Charsets.UTF_8)).use { reader ->
                        reader.readText()
                    }
                } else {
                    "Error: $responseCode - $responseMessage"
                }
                Result.failure(
                    RuntimeException("HTTP Error: $responseCode - $responseMessage, Body: $errorResponse")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Métodos públicos para usar desde el repositorio
    suspend fun get(endpoint: String): Result<String> =
        makeRequest(endpoint, "GET")

    suspend fun post(endpoint: String, body: String): Result<String> =
        makeRequest(endpoint, "POST", body)

    suspend fun put(endpoint: String, body: String): Result<String> =
        makeRequest(endpoint, "PUT", body)

    suspend fun delete(endpoint: String): Result<String> =
        makeRequest(endpoint, "DELETE")
}
