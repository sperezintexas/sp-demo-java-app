package com.sealights.demoapp.controller

import com.sealights.demoapp.data.SLCoverageBot
import com.sealights.demoapp.service.SLCoverageBotService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/sl-coverage-bots")
class SLCoverageBotController(private val slCoverageBotService: SLCoverageBotService) {

    @GetMapping
    fun getAllBots(): ResponseEntity<List<SLCoverageBot>> = ResponseEntity.ok(slCoverageBotService.findAll())

    @GetMapping("/{id}")
    fun getBotById(@PathVariable id: String): ResponseEntity<SLCoverageBot> {
        return slCoverageBotService.findById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createBot(@RequestBody slCoverageBot: SLCoverageBot): ResponseEntity<SLCoverageBot> {
        val savedBot = slCoverageBotService.save(slCoverageBot)
        return ResponseEntity.created(URI("/${savedBot.id}")).body(savedBot)
    }

    @PutMapping("/{id}")
    fun updateBot(
        @PathVariable id: String,
        @RequestBody slCoverageBot: SLCoverageBot
    ): ResponseEntity<SLCoverageBot> {
        val updatedBot = slCoverageBot.copy(id = id)
        return if (slCoverageBotService.update(updatedBot)) {
            ResponseEntity.ok(updatedBot)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteBot(@PathVariable id: String): ResponseEntity<Unit> {
        return if (slCoverageBotService.deleteById(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}