package com.sealights.demoapp.controller

import com.sealights.demoapp.data.Message
import com.sealights.demoapp.service.MessageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/messages")
class MessageController(private val messageService: MessageService) {
    @GetMapping
    fun getAllMessages() = ResponseEntity.ok(messageService.findMessages())

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun post(
        @RequestBody message: Message,
    ): ResponseEntity<Message> {
        val savedMessage = messageService.save(message)
        return ResponseEntity.created(URI("/api/messages/${savedMessage.id}")).body(savedMessage)
    }

    @GetMapping("/{id}")
    fun getMessage(
        @PathVariable id: Long,
    ): ResponseEntity<Message> = messageService.findMessageById(id).toResponseEntity()

    private fun Message?.toResponseEntity(): ResponseEntity<Message> =
        // If the message is null (not found), set response code to 404
        this?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
}
