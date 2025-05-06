package com.sealights.demoapp.service

import com.sealights.demoapp.data.Message
import com.sealights.demoapp.repository.MessageRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service


@Service
class MessageService(private val messageRepository: MessageRepository) {
    fun findMessages(): List<Message> = messageRepository.findAll().toList()

    fun findMessageById(id: String): Message? = messageRepository.findByIdOrNull(id)

    fun save(message: Message): Message = messageRepository.save(message)
}