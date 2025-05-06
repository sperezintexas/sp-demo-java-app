package com.sealights.demoapp

import com.sealights.demoapp.service.TodoService
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfig {
    @Bean
    fun todoService(): TodoService = mock(TodoService::class.java)
}
