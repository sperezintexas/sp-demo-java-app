package com.sealights.demoapp.service

import com.sealights.demoapp.data.Todo
import com.sealights.demoapp.repository.TodoRepository
import org.springframework.stereotype.Service

@Service
class TodoService(private val todoRepository: TodoRepository) {

    fun findAll(): List<Todo> = todoRepository.findAll().toList()

    fun findById(id: Long): Todo? = todoRepository.findById(id).orElse(null)

    fun save(todo: Todo): Todo = todoRepository.save(todo)

    fun update(todo: Todo): Boolean {
        val id = todo.id ?: return false
        return if (todoRepository.existsById(id)) {
            todoRepository.save(todo)
            true
        } else false
    }

    fun deleteById(id: Long): Boolean {
        return if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id)
            true
        } else false
    }
}
