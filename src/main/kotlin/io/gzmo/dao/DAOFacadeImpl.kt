package io.gzmo.dao

import AllRates
import DatabaseFactory.dbQuery
import NewRates
import Price
import Rate
import Rates
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DAOFacadeImpl : DAOFacade {
    private val dayTranslator: Map<String, String> = mapOf(
        "MONDAY" to "mon",
        "TUESDAY" to "tues",
        "WEDNESDAY" to "wed",
        "THURSDAY" to "thurs",
        "FRIDAY" to "fri",
        "SATURDAY" to "sat",
        "SUNDAY" to "sun",
    )
    private fun ratesToAllRates(): AllRates {
        class _Rate(
            val days: MutableList<String>,
            val times: String,
            val tz: String,
            val price: Int,
        )
        val rates: MutableList<_Rate> = mutableListOf()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HHmm")

        Rates.slice(listOf(Rates.start, Rates.finish, Rates.price)).selectAll().withDistinct().forEach { distinctTimes ->
            var rate = _Rate(
                days = mutableListOf(),
                times = distinctTimes[Rates.start].format(formatter) + "-" + distinctTimes[Rates.finish].format(formatter),
                tz = "America/Chicago",
                price = distinctTimes[Rates.price],
            )
            Rates.select {
                (Rates.start eq distinctTimes[Rates.start]) and
                (Rates.finish eq distinctTimes[Rates.finish]) and
                (Rates.price eq distinctTimes[Rates.price])}.forEach { result ->
                    rate.days.add(result[Rates.day])
            }
            rates.add(rate)
        }

        return AllRates(
            rates = rates.map{
                Rate(
                    days = it.days.joinToString(","),
                    times = it.times,
                    tz = it.tz,
                    price = it.price,
                )
            }
        )

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
                        finish = LocalTime.parse(times[1], formatter),
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
                this[Rates.finish] = it.finish
                this[Rates.price] = it.price
            }
        }
    }

    override suspend fun priceForRange(start: ZonedDateTime, end: ZonedDateTime): Price {
        val zonedStart = start.withZoneSameInstant(ZoneId.of("America/Chicago"))
        val zonedEnd = end.withZoneSameInstant(ZoneId.of("America/Chicago"))
        val day: String = dayTranslator[zonedStart.dayOfWeek.toString()]!!
        var price = 0
        dbQuery {
            Rates.select{
                (Rates.day eq day) and
                (Rates.start lessEq zonedStart.toLocalTime())  and
                (Rates.finish greaterEq zonedEnd.toLocalTime())
            }.forEach {
                println(it)
                price = it[Rates.price]
            }
        }
        return  Price(price = price)
    }
}

val dao: DAOFacade = DAOFacadeImpl()