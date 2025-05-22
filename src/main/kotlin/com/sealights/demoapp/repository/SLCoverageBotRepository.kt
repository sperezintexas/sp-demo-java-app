package com.sealights.demoapp.repository

import com.sealights.demoapp.data.SLCoverageBot
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SLCoverageBotRepository : CrudRepository<SLCoverageBot, String>
