package com.sealights.demoapp

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoAppApplicationTests {
    @Test
    fun contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }

    @Test
    fun testApplicationCanBeInstantiated() {
        // Test that the application class can be instantiated
        assertDoesNotThrow {
            DemoAppApplication()
        }
    }

    @Test
    fun testMainMethodDoesNotThrowException() {
        // Test that the main method can be called without errors
        // Pass server.port=0 to use a random port and avoid conflicts
        assertDoesNotThrow {
            main(arrayOf("--server.port=0"))
        }
    }
}
