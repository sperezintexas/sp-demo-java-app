package com.sealights.demoapp

import com.sealights.demoapp.data.SLCoverageBot
import com.sealights.demoapp.repository.SLCoverageBotRepository
import com.sealights.demoapp.service.SLCoverageBotService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class SLCoverageBotServiceTest {

    @Mock
    private lateinit var repository: SLCoverageBotRepository

    @InjectMocks
    private lateinit var slCoverageBotService: SLCoverageBotService

    @Test
    fun `should find all bots`() {
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
        `when`(repository.findAll()).thenReturn(bots)

        // when
        val result = slCoverageBotService.findAll()

        // then
        assertEquals(bots, result)
        verify(repository).findAll()
    }

    @Test
    fun `should find bot by id`() {
        // given
        val bot = SLCoverageBot(
            id = "1",
            name = "Test Bot",
            description = "Test Description",
            status = "active",
            createdAt = LocalDateTime.now()
        )
        `when`(repository.findById("1")).thenReturn(Optional.of(bot))

        // when
        val result = slCoverageBotService.findById("1")

        // then
        assertEquals(bot, result)
        verify(repository).findById("1")
    }

    @Test
    fun `should return null when bot not found`() {
        // given
        `when`(repository.findById("1")).thenReturn(Optional.empty())

        // when
        val result = slCoverageBotService.findById("1")

        // then
        assertNull(result)
        verify(repository).findById("1")
    }

    @Test
    fun `should save new bot with generated id`() {
        // given
        val bot = SLCoverageBot(
            id = null,
            name = "Test Bot",
            description = "Test Description",
            status = "active",
            createdAt = LocalDateTime.now()
        )
        val savedBot = bot.copy(id = "1")
        `when`(repository.save(any())).thenReturn(savedBot)

        // when
        val result = slCoverageBotService.save(bot)

        // then
        assertTrue(result.id != null)
        assertEquals(bot.name, result.name)
        verify(repository).save(any())
    }

    @Test
    fun `should update existing bot`() {
        // given
        val bot = SLCoverageBot(
            id = "1",
            name = "Updated Bot",
            description = "Updated Description",
            status = "inactive",
            createdAt = LocalDateTime.now()
        )
        `when`(repository.existsById("1")).thenReturn(true)
        `when`(repository.save(bot)).thenReturn(bot)

        // when
        val result = slCoverageBotService.update(bot)

        // then
        assertTrue(result)
        verify(repository).existsById("1")
        verify(repository).save(bot)
    }

    @Test
    fun `should not update non-existent bot`() {
        // given
        val bot = SLCoverageBot(
            id = "1",
            name = "Updated Bot",
            description = "Updated Description",
            status = "inactive",
            createdAt = LocalDateTime.now()
        )
        `when`(repository.existsById("1")).thenReturn(false)

        // when
        val result = slCoverageBotService.update(bot)

        // then
        assertFalse(result)
        verify(repository).existsById("1")
        verify(repository, never()).save(any())
    }

    @Test
    fun `should delete existing bot`() {
        // given
        `when`(repository.existsById("1")).thenReturn(true)
        doNothing().`when`(repository).deleteById("1")

        // when
        val result = slCoverageBotService.deleteById("1")

        // then
        assertTrue(result)
        verify(repository).existsById("1")
        verify(repository).deleteById("1")
    }

    @Test
    fun `should not delete non-existent bot`() {
        // given
        `when`(repository.existsById("1")).thenReturn(false)

        // when
        val result = slCoverageBotService.deleteById("1")

        // then
        assertFalse(result)
        verify(repository).existsById("1")
        verify(repository, never()).deleteById(any())
    }
}