package io.gzmo.dao

import AllRates
import DatabaseFactory.dbQuery
import Rate
import Rates
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager

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

    override suspend fun updateRates(rates: AllRates): AllRates = dbQuery {
        // truncate rates table
        val conn = TransactionManager.current().connection
        val query = "TRUNCATE TABLE rates"
        val statement = conn.prepareStatement(query, false)
        val result = statement.executeUpdate()
        // add new rates
        val rates = rates.rates
        AllRates(
            rates = Rates.batchInsert(rates) {
                this[Rates.days] = it.days
                this[Rates.tz] = it.tz
                this[Rates.times] = it.times
                this[Rates.price] = it.price
            }.map(::resultRowToRate)
        )
    }
}

val dao: DAOFacade = DAOFacadeImpl()