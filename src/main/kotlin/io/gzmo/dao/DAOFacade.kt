package io.gzmo.dao

import AllRates
import Price
import java.time.ZonedDateTime

interface DAOFacade {
    suspend fun allRates(): AllRates
    suspend fun updateRates(rates: AllRates)
    suspend fun priceForRange(start: ZonedDateTime, end: ZonedDateTime): Price
}