package com.sealights.demoapp.controller

import com.fasterxml.jackson.databind.ObjectMapper
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
    private val objectMapper = ObjectMapper()

    @Test
    fun `should return all messages`() {
        // given
        val messages =
            listOf(
                Message(text = "Test Message 1", id = "1"),
                Message(text = "Test Message 2", id = "2"),
            )
        `when`(messageService.findMessages()).thenReturn(messages)
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build()

        // when/then
        mockMvc.perform(get("/api/messages"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].text").value("Test Message 1"))
            .andExpect(jsonPath("$[1].id").value("2"))
            .andExpect(jsonPath("$[1].text").value("Test Message 2"))

        verify(messageService).findMessages()
    }

    @Test
    fun `should create a new message`() {
        // given
        val newMessage = Message(text = "New Message")
        val savedMessage = Message(text = "New Message", id = "1")

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
            .andExpect(header().string("Location", "/1"))
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.text").value("New Message"))

        verify(messageService).save(newMessage)
    }

    @Test
    fun `should return message by id when exists`() {
        // given
        val message = Message(text = "Test Message", id = "1")
        `when`(messageService.findMessageById("1")).thenReturn(message)
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build()

        // when/then
        mockMvc.perform(get("/api/messages/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.text").value("Test Message"))

        verify(messageService).findMessageById("1")
    }

    @Test
    fun `should return 404 when message not found`() {
        // given
        `when`(messageService.findMessageById("1")).thenReturn(null)
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build()

        // when/then
        mockMvc.perform(get("/api/messages/1"))
            .andExpect(status().isNotFound)

        verify(messageService).findMessageById("1")
    }
}
