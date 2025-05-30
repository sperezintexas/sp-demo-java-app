package com.sealights.demoapp.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("messages")
data class Message
    @JsonCreator
    constructor(
        @JsonProperty("text") val text: String,
        @Id @JsonProperty("id") val id: Long? = null,
    )
