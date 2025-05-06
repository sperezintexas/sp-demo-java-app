package com.sealights.demoapp

import com.sealights.demoapp.data.Todo
import com.sealights.demoapp.repository.TodoRepository
import com.sealights.demoapp.service.TodoService
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
class TodoServiceTest {

    @Mock
    private lateinit var repository: TodoRepository

    @InjectMocks
    private lateinit var todoService: TodoService

    @Test
    fun `should find all todos`() {
        // given
        val todos = listOf(
            Todo(
                id = 1L,
                title = "Test Todo",
                description = "Test Description",
                completed = false,
                createdAt = LocalDateTime.now()
            )
        )
        `when`(repository.findAll()).thenReturn(todos)

        // when
        val result = todoService.findAll()

        // then
        assertEquals(todos, result)
        verify(repository).findAll()
    }

    @Test
    fun `should find todo by id`() {
        // given
        val todo = Todo(
            id = 1L,
            title = "Test Todo",
            description = "Test Description",
            completed = false,
            createdAt = LocalDateTime.now()
        )
        `when`(repository.findById(1L)).thenReturn(Optional.of(todo))

        // when
        val result = todoService.findById(1L)

        // then
        assertEquals(todo, result)
        verify(repository).findById(1L)
    }

    @Test
    fun `should return null when todo not found`() {
        // given
        `when`(repository.findById(1L)).thenReturn(Optional.empty())

        // when
        val result = todoService.findById(1L)

        // then
        assertNull(result)
        verify(repository).findById(1L)
    }

    @Test
    fun `should save new todo with generated id`() {
        // given
        val todo = Todo(
            id = null,
            title = "Test Todo",
            description = "Test Description",
            completed = false,
            createdAt = LocalDateTime.now()
        )
        val savedTodo = todo.copy(id = 1L)
        `when`(repository.save(any())).thenReturn(savedTodo)

        // when
        val result = todoService.save(todo)

        // then
        assertTrue(result.id != null)
        assertEquals(todo.title, result.title)
        verify(repository).save(any())
    }

    @Test
    fun `should save todo with existing id`() {
        // given
        val todo = Todo(
            id = 1L,
            title = "Test Todo",
            description = "Test Description",
            completed = false,
            createdAt = LocalDateTime.now()
        )
        `when`(repository.save(todo)).thenReturn(todo)

        // when
        val result = todoService.save(todo)

        // then
        assertEquals(todo.id, result.id)
        assertEquals(todo.title, result.title)
        verify(repository).save(todo)
    }

    @Test
    fun `should save todo with non-existent id`() {
        // given
        val todo = Todo(
            id = 999L,
            title = "Test Todo",
            description = "Test Description",
            completed = false,
            createdAt = LocalDateTime.now()
        )
        `when`(repository.save(todo)).thenReturn(todo)

        // when
        val result = todoService.save(todo)

        // then
        assertEquals(todo.id, result.id)
        assertEquals(todo.title, result.title)
        verify(repository).save(todo)
    }

    @Test
    fun `should update existing todo`() {
        // given
        val todo = Todo(
            id = 1L,
            title = "Updated Todo",
            description = "Updated Description",
            completed = true,
            createdAt = LocalDateTime.now()
        )
        `when`(repository.existsById(1L)).thenReturn(true)
        `when`(repository.save(todo)).thenReturn(todo)

        // when
        val result = todoService.update(todo)

        // then
        assertTrue(result)
        verify(repository).existsById(1L)
        verify(repository).save(todo)
    }

    @Test
    fun `should not update non-existent todo`() {
        // given
        val todo = Todo(
            id = 1L,
            title = "Updated Todo",
            description = "Updated Description",
            completed = true,
            createdAt = LocalDateTime.now()
        )
        `when`(repository.existsById(1L)).thenReturn(false)

        // when
        val result = todoService.update(todo)

        // then
        assertFalse(result)
        verify(repository).existsById(1L)
        verify(repository, never()).save(any())
    }

    @Test
    fun `should delete existing todo`() {
        // given
        `when`(repository.existsById(1L)).thenReturn(true)
        doNothing().`when`(repository).deleteById(1L)

        // when
        val result = todoService.deleteById(1L)

        // then
        assertTrue(result)
        verify(repository).existsById(1L)
        verify(repository).deleteById(1L)
    }

    @Test
    fun `should not delete non-existent todo`() {
        // given
        `when`(repository.existsById(1L)).thenReturn(false)

        // when
        val result = todoService.deleteById(1L)

        // then
        assertFalse(result)
        verify(repository).existsById(1L)
        verify(repository, never()).deleteById(any())
    }
}