package com.sealights.demoapp.controller

import com.sealights.demoapp.data.Todo
import com.sealights.demoapp.service.TodoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/todos")
class TodoController(private val todoService: TodoService) {

    @GetMapping
    fun getAllTodos(): ResponseEntity<List<Todo>> = ResponseEntity.ok(todoService.findAll())

    @GetMapping("/{id}")
    fun getTodoById(@PathVariable id: Long): ResponseEntity<Todo> {
        return todoService.findById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTodo(@RequestBody todo: Todo): Todo {
        // Ensure createdAt is set if not provided in the request
       // val todoWithCreatedAt = todo
        return todoService.save(todo)
    }

    @PutMapping("/{id}")
    fun updateTodo(
        @PathVariable id: Long,
        @RequestBody todo: Todo
    ): ResponseEntity<Todo> {
        val updatedTodo = todo.copy(id = id)
        return if (todoService.update(updatedTodo)) {
            ResponseEntity.ok(updatedTodo)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable id: Long): ResponseEntity<Unit> {
        return if (todoService.deleteById(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}