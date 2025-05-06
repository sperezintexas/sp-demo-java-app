package com.sealights.demoapp.data

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("MESSAGES")
data class Message(val text: String,
                   @Id val id: String? = null)