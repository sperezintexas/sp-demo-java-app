package com.sealights.demoapp

import com.fasterxml.jackson.databind.ObjectMapper
import com.sealights.demoapp.controller.SLCoverageBotController
import com.sealights.demoapp.data.SLCoverageBot
import com.sealights.demoapp.service.SLCoverageBotService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
class SLCoverageBotControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var slCoverageBotService: SLCoverageBotService

    @Test
    fun `should get all bots`() {
        // given
        val bots = listOf(
            SLCoverageBot(
                id = "1",
                name = "Test Bot",
                description = "Test Description",
                status = "active",
                createdAt = LocalDateTime.now()
            )
        )
        `when`(slCoverageBotService.findAll()).thenReturn(bots)

        // when/then
        mockMvc.perform(get("/api/sl-coverage-bots"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].name").value("Test Bot"))
            .andExpect(jsonPath("$[0].description").value("Test Description"))
            .andExpect(jsonPath("$[0].status").value("active"))
    }

    @Test
    fun `should get bot by id`() {
        // given
        val bot = SLCoverageBot(
            id = "1",
            name = "Test Bot",
            description = "Test Description",
            status = "active",
            createdAt = LocalDateTime.now()
        )
        `when`(slCoverageBotService.findById("1")).thenReturn(bot)

        // when/then
        mockMvc.perform(get("/api/sl-coverage-bots/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("Test Bot"))
            .andExpect(jsonPath("$.description").value("Test Description"))
            .andExpect(jsonPath("$.status").value("active"))
    }

    @Test
    fun `should return 404 when bot not found`() {
        // given
        `when`(slCoverageBotService.findById("1")).thenReturn(null)

        // when/then
        mockMvc.perform(get("/api/sl-coverage-bots/1"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should create bot`() {
        // given
        val bot = SLCoverageBot(
            id = null,
            name = "Test Bot",
            description = "Test Description",
            status = "active",
            createdAt = LocalDateTime.now()
        )
        val savedBot = bot.copy(id = "1")
        `when`(slCoverageBotService.save(any())).thenReturn(savedBot)

        // when/then
        mockMvc.perform(
            post("/api/sl-coverage-bots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bot))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("Test Bot"))
            .andExpect(jsonPath("$.description").value("Test Description"))
            .andExpect(jsonPath("$.status").value("active"))
    }

    @Test
    fun `should update bot`() {
        // given
        val bot = SLCoverageBot(
            id = "1",
            name = "Updated Bot",
            description = "Updated Description",
            status = "inactive",
            createdAt = LocalDateTime.now()
        )
        `when`(slCoverageBotService.update(any())).thenReturn(true)

        // when/then
        mockMvc.perform(
            put("/api/sl-coverage-bots/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bot))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("Updated Bot"))
            .andExpect(jsonPath("$.description").value("Updated Description"))
            .andExpect(jsonPath("$.status").value("inactive"))
    }

    @Test
    fun `should return 404 when updating non-existent bot`() {
        // given
        val bot = SLCoverageBot(
            id = "1",
            name = "Updated Bot",
            description = "Updated Description",
            status = "inactive",
            createdAt = LocalDateTime.now()
        )
        `when`(slCoverageBotService.update(any())).thenReturn(false)

        // when/then
        mockMvc.perform(
            put("/api/sl-coverage-bots/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bot))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should delete bot`() {
        // given
        `when`(slCoverageBotService.deleteById("1")).thenReturn(true)

        // when/then
        mockMvc.perform(delete("/api/sl-coverage-bots/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should return 404 when deleting non-existent bot`() {
        // given
        `when`(slCoverageBotService.deleteById("1")).thenReturn(false)

        // when/then
        mockMvc.perform(delete("/api/sl-coverage-bots/1"))
            .andExpect(status().isNotFound)
    }
}
