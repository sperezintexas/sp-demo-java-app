package com.sealights.demoapp.repository

import com.sealights.demoapp.data.Message
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : CrudRepository<Message, String>
