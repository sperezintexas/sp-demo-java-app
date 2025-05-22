package com.sealights.demoapp.data

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("messages")
data class Message(
    val text: String,
    @Id val id: String? = UUID.randomUUID().toString(),
)
