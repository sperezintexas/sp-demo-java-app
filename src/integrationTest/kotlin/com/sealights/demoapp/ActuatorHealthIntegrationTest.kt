package com.sealights.demoapp

import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class ActuatorHealthIntegrationTest {
    private val httpClient =
        HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build()

    @Test
    fun `should return UP status from actuator health endpoint`() {
        // Get the API endpoint from system property or environment variable, with a default for local testing
        var apiEndpoint = System.getProperty("API_ENDPOINT") ?: System.getenv("API_ENDPOINT") ?: "http://localhost:8080"
        // Remove trailing slash if present to avoid double slashes in URLs
        if (apiEndpoint.endsWith("/")) {
            apiEndpoint = apiEndpoint.dropLast(1)
        }
        println("[DEBUG_LOG] Using API endpoint: $apiEndpoint")

        // Send a GET request to the actuator health endpoint
        val request =
            HttpRequest.newBuilder()
                .uri(URI.create("$apiEndpoint/actuator/health"))
                .GET()
                .build()

        println("[DEBUG_LOG] Sending GET request to: ${request.uri()}")

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        // Verify that the response is OK (200)
        println("[DEBUG_LOG] Response status code: ${response.statusCode()}")
        println("[DEBUG_LOG] Response body: ${response.body()}")

        assertEquals(200, response.statusCode(), "Expected 200 OK response from health endpoint")

        // Parse the response body as JSON
        val responseJson = JSONObject(response.body())

        // Verify that the status is "UP"
        val status = responseJson.getString("status")
        println("[DEBUG_LOG] Health status: $status")

        assertEquals("UP", status, "Expected health status to be UP")
    }
}
