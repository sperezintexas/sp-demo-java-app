package com.sealights.demoapp.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class MessageTest {
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    @Test
    fun `should accept message from JSON without ID`() {
        // given
        val json = """{"messageText": "Test Message"}"""

        // when
        val message = objectMapper.readValue(json, Message::class.java)

        // then
        assert(message.messageText == "Test Message")
        assert(message.id == null)
        assertNotNull(message.createdAt)
    }

    @Test
    fun `should use provided ID when creating message from JSON with ID`() {
        // given
        val id = 123L
        val json = """{"messageText": "Test Message", "id": $id}"""

        // when
        val message = objectMapper.readValue(json, Message::class.java)

        // then
        assertNotNull(message.id)
        assert(message.id == id)
        assertNotNull(message.createdAt)
    }
}
