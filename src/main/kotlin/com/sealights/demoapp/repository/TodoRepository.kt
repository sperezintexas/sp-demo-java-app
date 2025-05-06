package com.sealights.demoapp.repository

import com.sealights.demoapp.data.Todo
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : CrudRepository<Todo, Long>
