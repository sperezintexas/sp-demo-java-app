package com.sealights.demoapp

import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Integration test for the Message API.
 *
 * This test assumes that the application is already running on localhost:8080.
 * Start the application before running this test with:
 * ./gradlew bootRun
 */
class MessageRepositoryIntegrationTest {
    companion object {
        private const val API_ENDPOINT = "http://localhost:8080"
        private val httpClient = HttpClient.newBuilder().build()
    }

    @Test
    fun `should create and retrieve message from deployed API`() {
        // Load the expected Message JSON from resources
        val expectedMessageJson =
            javaClass.getResourceAsStream("/expected-message.json")?.bufferedReader()?.readText()
                ?: throw IllegalStateException("Failed to load expected-message.json")

        println("[DEBUG_LOG] Using API endpoint: $API_ENDPOINT")
        println("[DEBUG_LOG] Request body: $expectedMessageJson")

        // POST the Message to the API
        val createRequest =
            HttpRequest.newBuilder()
                .uri(URI.create("$API_ENDPOINT/api/messages"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(expectedMessageJson))
                .build()

        val createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString())

        // Verify the Message was created successfully
        assertEquals(201, createResponse.statusCode())
        val createdMessageJson = JSONObject(createResponse.body())
        assertNotNull(createdMessageJson.getLong("id"))
        assertEquals("Integration Test Message", createdMessageJson.getString("text"))

        // Store the created Message ID for later use
        val createdMessageId = createdMessageJson.getLong("id")

        // GET the Message by ID to verify it was persisted
        val getRequest =
            HttpRequest.newBuilder()
                .uri(URI.create("$API_ENDPOINT/api/messages/$createdMessageId"))
                .GET()
                .build()

        val getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString())

        // Verify the Message can be retrieved
        assertEquals(200, getResponse.statusCode())

        // Load the expected response JSON from resources
        val expectedResponseJson =
            javaClass.getResourceAsStream("/expected-message-response.json")?.bufferedReader()?.readText()
                ?: throw IllegalStateException("Failed to load expected-message-response.json")

        // Parse the expected response JSON and add the ID from the created Message
        val expectedResponseObj = JSONObject(expectedResponseJson)
        expectedResponseObj.put("id", createdMessageId)

        // Parse the actual response JSON
        val actualResponseObj = JSONObject(getResponse.body())

        // Print both JSONs for debugging
        println("[DEBUG_LOG] Expected JSON: $expectedResponseObj")
        println("[DEBUG_LOG] Actual JSON: $actualResponseObj")

        // Compare the response with the expected JSON
        JSONAssert.assertEquals(expectedResponseObj.toString(), actualResponseObj.toString(), JSONCompareMode.STRICT)

        // GET all messages to verify the created message is included
        val getAllRequest =
            HttpRequest.newBuilder()
                .uri(URI.create("$API_ENDPOINT/api/messages"))
                .GET()
                .build()

        val getAllResponse = httpClient.send(getAllRequest, HttpResponse.BodyHandlers.ofString())

        // Verify the GET all request was successful
        assertEquals(200, getAllResponse.statusCode())

        // Parse the response as a JSON array
        val responseArray = JSONObject("{\"messages\":" + getAllResponse.body() + "}").getJSONArray("messages")

        // Verify the created message is in the list
        var foundMessage = false
        for (i in 0 until responseArray.length()) {
            val message = responseArray.getJSONObject(i)
            if (message.getLong("id") == createdMessageId) {
                assertEquals("Integration Test Message", message.getString("text"))
                foundMessage = true
                break
            }
        }

        // Assert that the created message was found in the list
        assertEquals(true, foundMessage, "Created message was not found in the list of all messages")
    }
}
