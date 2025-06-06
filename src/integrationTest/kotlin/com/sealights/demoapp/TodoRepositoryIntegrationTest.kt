package com.sealights.demoapp

import org.json.JSONObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class TodoRepositoryIntegrationTest {
    private val httpClient =
        HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build()

    private var apiEndpoint = ""
    private var todoJson = ""
    private var createdTodoId: Long = 0

    @BeforeEach
    fun setup() {
        // Get the API endpoint from system property or environment variable, with a default for local testing
        apiEndpoint = System.getProperty("API_ENDPOINT") ?: System.getenv("API_ENDPOINT") ?: "http://localhost:8080"
        // Remove trailing slash if present to avoid double slashes in URLs
        if (apiEndpoint.endsWith("/")) {
            apiEndpoint = apiEndpoint.dropLast(1)
        }
        println("[DEBUG_LOG] Using API endpoint: $apiEndpoint")

        // Load the expected Todo JSON from resources
        todoJson =
            javaClass.getResourceAsStream("/expected-todo.json")?.bufferedReader()?.readText()
                ?: throw IllegalStateException("Failed to load expected-todo.json")
    }

    @AfterEach
    fun cleanup() {
        // Only attempt to delete if we have a valid ID
        if (createdTodoId > 0) {
            try {
                // Cleanup - DELETE the Todo
                val deleteRequest =
                    HttpRequest.newBuilder()
                        .uri(URI.create("$apiEndpoint/api/todos/$createdTodoId"))
                        .DELETE()
                        .build()

                println("[DEBUG_LOG] Cleanup: Sending DELETE request to: ${deleteRequest.uri()}")
                val deleteResponse = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString())
                println("[DEBUG_LOG] Cleanup: Delete response status: ${deleteResponse.statusCode()}")
            } catch (e: Exception) {
                println("[DEBUG_LOG] Error during cleanup: ${e.message}")
            }
        }
    }

    @Test
    fun should_create_todo() {
        // POST the Todo to the API
        val createRequest =
            HttpRequest.newBuilder()
                .uri(URI.create("$apiEndpoint/api/todos"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build()

        println("[DEBUG_LOG] Sending POST request to: ${createRequest.uri()}")
        println("[DEBUG_LOG] Request body: $todoJson")

        val createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString())

        // Verify the Todo was created successfully
        assertEquals(201, createResponse.statusCode())
        val createdTodoJson = JSONObject(createResponse.body())
        assertNotNull(createdTodoJson.getLong("id"))
        assertEquals("Integration Test Todo", createdTodoJson.getString("title"))

        // Store the created Todo ID for cleanup
        createdTodoId = createdTodoJson.getLong("id")
    }

    @Test
    fun should_read_todo() {
        // First create a todo to read
        should_create_todo()

        // GET the Todo by ID to verify it was persisted
        val getRequest =
            HttpRequest.newBuilder()
                .uri(URI.create("$apiEndpoint/api/todos/$createdTodoId"))
                .GET()
                .build()

        println("[DEBUG_LOG] Sending GET request to: ${getRequest.uri()}")

        val getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString())

        // Verify the Todo can be retrieved
        assertEquals(200, getResponse.statusCode())

        // Load the expected response JSON from resources
        val expectedResponseJson =
            javaClass.getResourceAsStream("/expected-todo-response.json")?.bufferedReader()?.readText()
                ?: throw IllegalStateException("Failed to load expected-todo-response.json")

        // Parse the expected response JSON and add the ID from the created Todo
        val expectedResponseObj = JSONObject(expectedResponseJson)
        expectedResponseObj.put("id", createdTodoId)

        // Parse the actual response JSON
        val actualResponseObj = JSONObject(getResponse.body())

        // Print both JSONs for debugging
        println("[DEBUG_LOG] Expected JSON: $expectedResponseObj")
        println("[DEBUG_LOG] Actual JSON: $actualResponseObj")

        // Remove createdAt from both objects for comparison
        expectedResponseObj.remove("createdAt")
        actualResponseObj.remove("createdAt")

        // Compare the response with the expected JSON, now that createdAt has been removed from both
        JSONAssert.assertEquals(expectedResponseObj.toString(), actualResponseObj.toString(), JSONCompareMode.STRICT)
    }

    @Test
    fun should_update_todo() {
        // First create a todo to update
        should_create_todo()

        // UPDATE the Todo to mark it as completed
        val updatedTodoJson = JSONObject(todoJson)
        updatedTodoJson.put("completed", true)
        updatedTodoJson.put("id", createdTodoId) // Include the ID for the update

        val updateRequest =
            HttpRequest.newBuilder()
                .uri(URI.create("$apiEndpoint/api/todos/$createdTodoId"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedTodoJson.toString()))
                .build()

        println("[DEBUG_LOG] Sending PUT request to: ${updateRequest.uri()}")
        println("[DEBUG_LOG] Request body: $updatedTodoJson")

        val updateResponse = httpClient.send(updateRequest, HttpResponse.BodyHandlers.ofString())

        // Verify the Todo was updated successfully
        assertEquals(200, updateResponse.statusCode())
        val updatedResponseJson = JSONObject(updateResponse.body())
        assertEquals(createdTodoId, updatedResponseJson.getLong("id"))
        assertEquals("Integration Test Todo", updatedResponseJson.getString("title"))
        assertEquals("Created during integration test", updatedResponseJson.getString("description"))
        assertTrue(updatedResponseJson.getBoolean("completed"))

        // GET the Todo again to verify the update was persisted
        val getUpdatedRequest =
            HttpRequest.newBuilder()
                .uri(URI.create("$apiEndpoint/api/todos/$createdTodoId"))
                .GET()
                .build()

        println("[DEBUG_LOG] Sending GET request for updated Todo to: ${getUpdatedRequest.uri()}")

        val getUpdatedResponse = httpClient.send(getUpdatedRequest, HttpResponse.BodyHandlers.ofString())

        // Verify the updated Todo can be retrieved
        assertEquals(200, getUpdatedResponse.statusCode())
        val updatedTodoFromGet = JSONObject(getUpdatedResponse.body())
        assertEquals(createdTodoId, updatedTodoFromGet.getLong("id"))
        assertEquals("Integration Test Todo", updatedTodoFromGet.getString("title"))
        assertEquals("Created during integration test", updatedTodoFromGet.getString("description"))
        assertTrue(updatedTodoFromGet.getBoolean("completed"))
    }

    @Test
    fun should_delete_todo() {
        // First create a todo to delete
        should_create_todo()

        // DELETE the Todo
        val deleteRequest =
            HttpRequest.newBuilder()
                .uri(URI.create("$apiEndpoint/api/todos/$createdTodoId"))
                .DELETE()
                .build()

        println("[DEBUG_LOG] Sending DELETE request to: ${deleteRequest.uri()}")

        val deleteResponse = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString())
        assertEquals(204, deleteResponse.statusCode())

        // Verify the Todo was deleted
        val verifyDeleteRequest =
            HttpRequest.newBuilder()
                .uri(URI.create("$apiEndpoint/api/todos/$createdTodoId"))
                .GET()
                .build()

        println("[DEBUG_LOG] Sending verification GET request to: ${verifyDeleteRequest.uri()}")

        val verifyDeleteResponse = httpClient.send(verifyDeleteRequest, HttpResponse.BodyHandlers.ofString())
        assertEquals(404, verifyDeleteResponse.statusCode())

        // Set createdTodoId to 0 to prevent cleanup from trying to delete again
        createdTodoId = 0
    }
}
