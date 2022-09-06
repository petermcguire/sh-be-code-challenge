package io.gzmo.dao

import AllRates
import DatabaseFactory.dbQuery
import Rate
import Rates
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToRate(row: ResultRow) = Rate(
        days = row[Rates.days],
        times = row[Rates.times],
        tz = row[Rates.tz],
        price = row[Rates.price],
    )

    override suspend fun allRates(): AllRates = dbQuery {
        AllRates(
            rates = Rates.selectAll().map(::resultRowToRate)
        )
    }

    override suspend fun updateRates() {

    }
}

val dao: DAOFacade = DAOFacadeImpl()