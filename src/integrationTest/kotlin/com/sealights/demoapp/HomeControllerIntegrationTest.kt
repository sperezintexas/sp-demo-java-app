package com.sealights.demoapp

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class HomeControllerIntegrationTest {
    private val httpClient =
        HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NEVER) // Don't follow redirects automatically
            .build()

    @Test
    fun `should redirect from root to index html`() {
        // Get the API endpoint from system property or environment variable, with a default for local testing
        var apiEndpoint = System.getProperty("API_ENDPOINT") ?: System.getenv("API_ENDPOINT") ?: "http://localhost:8080"
        // Remove trailing slash if present to avoid double slashes in URLs
        if (apiEndpoint.endsWith("/")) {
            apiEndpoint = apiEndpoint.dropLast(1)
        }
        println("[DEBUG_LOG] Using API endpoint: $apiEndpoint")

        // Send a GET request to the root URL
        val request =
            HttpRequest.newBuilder()
                .uri(URI.create(apiEndpoint))
                .GET()
                .build()

        println("[DEBUG_LOG] Sending GET request to: ${request.uri()}")

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        // Verify that the response is a redirect (302 Found or 303 See Other)
        println("[DEBUG_LOG] Response status code: ${response.statusCode()}")
        println("[DEBUG_LOG] Response headers: ${response.headers()}")

        // Check if the status code is a redirect (302 Found or 303 See Other)
        val isRedirect = response.statusCode() == 302 || response.statusCode() == 303
        assertEquals(true, isRedirect, "Expected a redirect status code (302 or 303)")

        // Check if the Location header points to index.html
        val locationHeader = response.headers().firstValue("Location").orElse("")
        println("[DEBUG_LOG] Location header: $locationHeader")

        // The Location header should end with index.html
        assertEquals(true, locationHeader.endsWith("/index.html"), "Expected redirect to index.html")
    }
}
