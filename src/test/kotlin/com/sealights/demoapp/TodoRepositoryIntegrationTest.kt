package com.sealights.demoapp

import com.sealights.demoapp.data.Todo
import com.sealights.demoapp.repository.TodoRepository
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TodoRepositoryIntegrationTest {
    @Autowired
    private lateinit var todoRepository: TodoRepository

    fun `should save and retrieve todo`() {
        // given
        val todo =
            Todo(
                id = null,
                title = "Test Todo",
                description = "Test Description",
                completed = false,
                createdAt = LocalDateTime.now(),
            )

        // when
        val savedTodo = todoRepository.save(todo)
        val retrievedTodo = todoRepository.findById(savedTodo.id!!).orElse(null)

        // then
        assertNotNull(retrievedTodo)
        assertEquals(savedTodo.id, retrievedTodo.id)
        assertEquals(todo.title, retrievedTodo.title)
        assertEquals(todo.description, retrievedTodo.description)
        assertEquals(todo.completed, retrievedTodo.completed)

        // cleanup
        todoRepository.deleteById(savedTodo.id!!)
    }
}
