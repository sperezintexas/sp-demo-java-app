package com.sealights.demoapp.data

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table("sl_coverage_bots")
data class SLCoverageBot(
    val name: String,
    val description: String,
    val status: String = "active",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Id val id: String? = UUID.randomUUID().toString(),
)
