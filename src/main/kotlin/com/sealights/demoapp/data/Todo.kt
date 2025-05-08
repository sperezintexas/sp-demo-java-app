
package com.sealights.demoapp.data

import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.annotation.Id
import java.time.LocalDateTime

@Table("todos")
data class Todo(
    @Id val id: Long? = null,
    val title: String,
    val description: String,
    val completed: Boolean = false,
    val createdAt: java.time.LocalDateTime = LocalDateTime.now()
)
