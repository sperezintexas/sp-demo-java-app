package com.sealights.demoapp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.sealights.demoapp.data.Todo
import com.sealights.demoapp.service.TodoService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TodoControllerTest {
    @Mock
    private lateinit var todoService: TodoService

    @InjectMocks
    private lateinit var todoController: TodoController

    private lateinit var mockMvc: MockMvc
    private val objectMapper =
        ObjectMapper().apply {
            registerModule(JavaTimeModule())
        }

    @Test
    fun `should return all todos`() {
        // given
        val todos =
            listOf(
                Todo(id = 1L, title = "Test Todo 1", description = "Description 1", completed = false, createdAt = LocalDateTime.now()),
                Todo(id = 2L, title = "Test Todo 2", description = "Description 2", completed = true, createdAt = LocalDateTime.now()),
            )
        `when`(todoService.findAll()).thenReturn(todos)
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build()

        // when/then
        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("Test Todo 1"))
            .andExpect(jsonPath("$[0].description").value("Description 1"))
            .andExpect(jsonPath("$[0].completed").value(false))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].title").value("Test Todo 2"))
            .andExpect(jsonPath("$[1].description").value("Description 2"))
            .andExpect(jsonPath("$[1].completed").value(true))

        verify(todoService).findAll()
    }

    @Test
    fun `should return todo by id when exists`() {
        // given
        val todo = Todo(id = 1L, title = "Test Todo", description = "Test Description", completed = false, createdAt = LocalDateTime.now())
        `when`(todoService.findById(1L)).thenReturn(todo)
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build()

        // when/then
        mockMvc.perform(get("/api/todos/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Todo"))
            .andExpect(jsonPath("$.description").value("Test Description"))
            .andExpect(jsonPath("$.completed").value(false))

        verify(todoService).findById(1L)
    }

    @Test
    fun `should return 404 when todo not found`() {
        // given
        `when`(todoService.findById(1L)).thenReturn(null)
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build()

        // when/then
        mockMvc.perform(get("/api/todos/1"))
            .andExpect(status().isNotFound)

        verify(todoService).findById(1L)
    }

    @Test
    fun `should create a new todo`() {
        // given
        val newTodo = Todo(title = "New Todo", description = "New Description")
        val savedTodo =
            Todo(id = 1L, title = "New Todo", description = "New Description", completed = false, createdAt = LocalDateTime.now())

        `when`(todoService.save(newTodo)).thenReturn(savedTodo)
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build()

        // when/then
        mockMvc.perform(
            post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTodo)),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("New Todo"))
            .andExpect(jsonPath("$.description").value("New Description"))
            .andExpect(jsonPath("$.completed").value(false))

        verify(todoService).save(newTodo)
    }

    fun `should update todo when it exists`() {
        // given
        val todoToUpdate = Todo(title = "Updated Todo", description = "Updated Description", completed = true)
        val expectedTodoToUpdate = Todo(id = 1L, title = "Updated Todo", description = "Updated Description", completed = true)

        `when`(todoService.update(expectedTodoToUpdate)).thenReturn(true)
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build()

        // when/then
        mockMvc.perform(
            put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoToUpdate)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Updated Todo"))
            .andExpect(jsonPath("$.description").value("Updated Description"))
            .andExpect(jsonPath("$.completed").value(true))

        verify(todoService).update(expectedTodoToUpdate)
    }

    fun `should return 404 when updating non-existent todo`() {
        // given
        val todoToUpdate = Todo(title = "Updated Todo", description = "Updated Description", completed = true)
        val expectedTodoToUpdate = Todo(id = 1L, title = "Updated Todo", description = "Updated Description", completed = true)

        `when`(todoService.update(expectedTodoToUpdate)).thenReturn(false)
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build()

        // when/then
        mockMvc.perform(
            put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoToUpdate)),
        )
            .andExpect(status().isNotFound)

        verify(todoService).update(expectedTodoToUpdate)
    }

    @Test
    fun `should delete todo when it exists`() {
        // given
        `when`(todoService.deleteById(1L)).thenReturn(true)
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build()

        // when/then
        mockMvc.perform(delete("/api/todos/1"))
            .andExpect(status().isNoContent)

        verify(todoService).deleteById(1L)
    }

    @Test
    fun `should return 404 when deleting non-existent todo`() {
        // given
        `when`(todoService.deleteById(1L)).thenReturn(false)
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build()

        // when/then
        mockMvc.perform(delete("/api/todos/1"))
            .andExpect(status().isNotFound)

        verify(todoService).deleteById(1L)
    }
}
