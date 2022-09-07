package io.gzmo.dao

import AllRates

interface DAOFacade {
    suspend fun allRates(): AllRates
    suspend fun updateRates(rates: AllRates): AllRates
}