package com.sealights.demoapp.data

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class MessageTest {
    private val objectMapper = ObjectMapper()

    @Test
    fun `should accept message from JSON without ID`() {
        // given
        val json = """{"text": "Test Message"}"""

        // when
        val message = objectMapper.readValue(json, Message::class.java)

        // then
        assert(message.text == "Test Message")
        assert(message.id == null)
    }

    @Test
    fun `should use provided ID when creating message from JSON with ID`() {
        // given
        val json = """{"text": "Test Message", "id": 123}"""

        // when
        val message = objectMapper.readValue(json, Message::class.java)

        // then
        assertNotNull(message.id)
        assert(message.id == 123L)
    }
}
