package com.sealights.demoapp.service

import com.sealights.demoapp.data.SLCoverageBot
import com.sealights.demoapp.repository.SLCoverageBotRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class SLCoverageBotService(private val slCoverageBotRepository: SLCoverageBotRepository) {
    fun findAll(): List<SLCoverageBot> = slCoverageBotRepository.findAll().toList()

    fun findById(id: String): SLCoverageBot? = slCoverageBotRepository.findByIdOrNull(id)

    fun save(slCoverageBot: SLCoverageBot): SLCoverageBot = slCoverageBotRepository.save(slCoverageBot)

    fun update(slCoverageBot: SLCoverageBot): Boolean {
        val id = slCoverageBot.id ?: return false
        return if (slCoverageBotRepository.existsById(id)) {
            slCoverageBotRepository.save(slCoverageBot)
            true
        } else false
    }

    fun deleteById(id: String): Boolean {
        return if (slCoverageBotRepository.existsById(id)) {
            slCoverageBotRepository.deleteById(id)
            true
        } else false
    }
}