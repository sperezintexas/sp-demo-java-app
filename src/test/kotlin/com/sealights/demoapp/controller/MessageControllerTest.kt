package com.sealights.demoapp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.sealights.demoapp.data.Message
import com.sealights.demoapp.service.MessageService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class MessageControllerTest {
    @Mock
    private lateinit var messageService: MessageService

    @InjectMocks
    private lateinit var messageController: MessageController

    private lateinit var mockMvc: MockMvc
    private val objectMapper =
        ObjectMapper()
            .registerModule(KotlinModule.Builder().build())
            .registerModule(JavaTimeModule())

    @Test
    fun `should return all messages`() {
        // given
        val id1 = 1L
        val id2 = 2L
        val messages =
            listOf(
                Message(messageText = "Test Message 1", id = id1),
                Message(messageText = "Test Message 2", id = id2),
            )
        `when`(messageService.findMessages()).thenReturn(messages)
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build()

        // when/then
        mockMvc.perform(get("/api/messages"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(id1))
            .andExpect(jsonPath("$[0].messageText").value("Test Message 1"))
            .andExpect(jsonPath("$[0].createdAt").exists())
            .andExpect(jsonPath("$[1].id").value(id2))
            .andExpect(jsonPath("$[1].messageText").value("Test Message 2"))
            .andExpect(jsonPath("$[1].createdAt").exists())

        verify(messageService).findMessages()
    }

    @Test
    fun `should create a new message`() {
        // given
        val newMessage = Message(messageText = "New Message")
        val savedId = 3L
        val savedMessage = Message(messageText = "New Message", id = savedId)

        // Use the actual message object for stubbing
        `when`(messageService.save(newMessage)).thenReturn(savedMessage)
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build()

        // when/then
        mockMvc.perform(
            post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMessage)),
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/messages/$savedId"))
            .andExpect(jsonPath("$.id").value(savedId))
            .andExpect(jsonPath("$.messageText").value("New Message"))
            .andExpect(jsonPath("$.createdAt").exists())

        verify(messageService).save(newMessage)
    }

    @Test
    fun `should return message by id when exists`() {
        // given
        val messageId = 4L
        val message = Message(messageText = "Test Message", id = messageId)
        `when`(messageService.findMessageById(messageId)).thenReturn(message)
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build()

        // when/then
        mockMvc.perform(get("/api/messages/$messageId"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(messageId))
            .andExpect(jsonPath("$.messageText").value("Test Message"))
            .andExpect(jsonPath("$.createdAt").exists())

        verify(messageService).findMessageById(messageId)
    }

    @Test
    fun `should return 404 when message not found`() {
        // given
        val messageId = 5L
        `when`(messageService.findMessageById(messageId)).thenReturn(null)
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build()

        // when/then
        mockMvc.perform(get("/api/messages/$messageId"))
            .andExpect(status().isNotFound)

        verify(messageService).findMessageById(messageId)
    }
}
