package io.gzmo.dao

import AllRates
import DatabaseFactory.dbQuery
import NewRates
import Rate
import Rates
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DAOFacadeImpl : DAOFacade {
    private fun ratesToAllRates(): AllRates {
        class _Rate(
            val days: List<String>,
            val times: String,
            val tz: String,
            val price: Int,
        )
        val rates: MutableList<_Rate> = mutableListOf()
        Rates.slice(Rates.price).selectAll().withDistinct().forEach{
            val price = it[Rates.price]
            Rates.select{Rates.price eq price}.forEach{

            }
        }
        return AllRates(rates=rates.map { it[Rate.] = _Rate })
    }

    private fun allRatesToListOfNewRates(allRates: AllRates): List<NewRates> {
        val rates = allRates.rates
        val listOfNewRates: MutableList<NewRates> = mutableListOf()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HHmm")


        for (rate in rates) {
            // get days list
            val days: List<String> = rate.days.split(",")
            val times: List<String> = rate.times.split("-")
            for (day in days) {
                // make Rates and add to list
                listOfNewRates.add(
                    NewRates(
                        day = day,
                        start = LocalTime.parse(times[0], formatter),
                        end = LocalTime.parse(times[1], formatter),
                        price = rate.price,
                    )
                )
            }
        }
        return listOfNewRates
    }

    private fun truncateRates(): Int {
        val conn = TransactionManager.current().connection
        val query = "TRUNCATE TABLE rates"
        val statement = conn.prepareStatement(query, false)
        return statement.executeUpdate()
    }

    override suspend fun allRates(): AllRates = dbQuery {
        ratesToAllRates()
    }

    override suspend fun updateRates(rates: AllRates) {
        dbQuery {
            // truncate rates table
            truncateRates()
            // convert rates
            var listOfNewRates = allRatesToListOfNewRates(rates)
            // insert it all
            Rates.batchInsert(listOfNewRates) {
                this[Rates.day] = it.day
                this[Rates.start] = it.start
                this[Rates.end] = it.end
                this[Rates.price] = it.price
            }
        }
    }
}

val dao: DAOFacade = DAOFacadeImpl()