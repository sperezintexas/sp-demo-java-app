package com.sealights.demoapp.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("messages")
data class Message(
    @Id val id: Long? = null,
    @Column("messageText") val messageText: String,
    val createdAt: java.time.LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        @JsonCreator
        @JvmStatic
        fun create(
            @JsonProperty("id") id: Long? = null,
            @JsonProperty("messageText") messageText: String? = null,
            @JsonProperty("text") text: String? = null,
            @JsonProperty("createdAt") createdAt: java.time.LocalDateTime? = null,
        ): Message {
            // Use either messageText or text, with messageText taking precedence
            val finalText = messageText ?: text ?: throw IllegalArgumentException("Either 'messageText' or 'text' property is required")
            return Message(id, finalText, createdAt ?: LocalDateTime.now())
        }
    }
}
