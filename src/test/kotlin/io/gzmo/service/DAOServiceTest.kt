package io.gzmo.service

import AllRates
import DatabaseFactory
import Price
import Rate
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.time.ZonedDateTime
import kotlin.test.Test

internal class DAOServiceTest {

    private val testDAOService: DAOService = DAOService()
    private val expected = AllRates(
        rates = listOf(
            Rate(
                days = "mon,tues,wed",
                times = "0900-1900",
                tz = "America/Chicago",
                price = 1200,
            ),
            Rate(
                days = "thurs,fri",
                times = "0100-2300",
                tz = "America/Chicago",
                price = 2400,
            ),
            Rate(
                days = "sat,sun",
                times = "0200-1000",
                tz = "America/Chicago",
                price = 1000,
            ),
            Rate(
                days = "sat,sun",
                times = "1000-2300",
                tz = "America/Chicago",
                price = 500,
            ),
        )
    )

    @BeforeEach
    fun before() {
        // connect and build db
        DatabaseFactory.connect()
        DatabaseFactory.build()
        runBlocking {
            testDAOService.updateRates(expected)
        }
    }

    @AfterEach
    fun after() {}

    @Test
    fun updateRatesSuccessTest() = runBlocking{

        val actual = testDAOService.allRates()
        assertThat(actual.rates).containsExactlyInAnyOrderElementsOf(expected.rates)

        Unit
    }

    @Test
    fun priceForRangeSuccessTest() = runBlocking{

        // start and end on a saturday between 10:00 and 12:43 central time
        val start = ZonedDateTime.parse("2022-09-10T10:00:00.000-05:00[America/Chicago]")
        val end = ZonedDateTime.parse("2022-09-10T12:43:00.000-05:00[America/Chicago]")

        val expectedPrice = Price(price = 500)

        val actual = testDAOService.priceForRange(start, end)
        assertThat(actual).isEqualTo(expectedPrice)

        Unit
    }

    @Test
    fun priceForRangeFailTest() = runBlocking{

        // start and end on a wednesday between 05:00 and 12:00 central time
        val start = ZonedDateTime.parse("2022-09-07T08:00:00.000-05:00[America/Chicago]")
        val end = ZonedDateTime.parse("2022-09-07T12:00:00.000-05:00[America/Chicago]")

        val actual = testDAOService.priceForRange(start, end)
        assertThat(actual).isEqualTo(null)

        Unit
    }
}