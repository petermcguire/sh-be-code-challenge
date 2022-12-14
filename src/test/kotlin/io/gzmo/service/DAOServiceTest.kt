package io.gzmo.service

import AllRates
import DatabaseFactory
import Price
import Rate
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import java.time.ZonedDateTime
import kotlin.test.Test

internal class DAOServiceTest {

    private val testDAOService: DAOService = DAOService()

    @BeforeEach
    fun before() {
        // assure a fresh db
        DatabaseFactory.init()
    }

    @Test
    fun `allRates should return correct values`() = runBlocking{

        val expected = DatabaseFactory.allRates
        val actual = testDAOService.allRates()
        assertThat(actual.rates).containsExactlyInAnyOrderElementsOf(expected.rates)

        Unit
    }

    @Test
    fun `updateRates should update rates correctly`() = runBlocking{

        val expected = AllRates(
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
            )
        )
        testDAOService.updateRates(expected)
        val actual = testDAOService.allRates()
        assertThat(actual.rates).containsExactlyInAnyOrderElementsOf(expected.rates)

        Unit
    }

    @Test
    fun `priceForRange should return proper price for given valid range`() = runBlocking{

        // start and end on a saturday between 10:00 and 12:43 central time
        val start = ZonedDateTime.parse("2022-09-10T10:00:00.000-05:00[America/Chicago]")
        val end = ZonedDateTime.parse("2022-09-10T12:43:00.000-05:00[America/Chicago]")

        val expectedPrice = Price(price = 2000)

        val actual = testDAOService.priceForRange(start, end)
        assertThat(actual).isEqualTo(expectedPrice)

        Unit
    }

    @Test
    fun `priceForRange should convert times to central time`() = runBlocking{

        // start and end on September 8th (Thursday) between 22:00 and 23:00 Calcutta time
        val start = ZonedDateTime.parse("2022-09-08T22:00:00.000+05:30[Asia/Calcutta]")
        val end = ZonedDateTime.parse("2022-09-08T23:00:00.000+05:30[Asia/Calcutta]")

        // which is between 11:30 and 12:30 Thursday central time, so expecting 1500
        val expectedPrice = Price(price = 1500)

        val actual = testDAOService.priceForRange(start, end)
        assertThat(actual).isEqualTo(expectedPrice)

        Unit
    }

    @Test
    fun `priceForRange should return null for range spanning more than one day`() = runBlocking{

        val start = ZonedDateTime.parse("2022-09-07T14:00:00.000-05:00[America/Chicago]")
        val end = ZonedDateTime.parse("2022-09-09T19:00:00.000-05:00[America/Chicago]")

        val actual = testDAOService.priceForRange(start, end)
        assertThat(actual).isEqualTo(null)

        Unit
    }

    @Test
    fun `priceForRange should return null for range spanning more than one rate`() = runBlocking{

        // wednesday from 03:00 to 12:00 spans the two wednesday rates
        val start = ZonedDateTime.parse("2022-09-07T03:00:00.000-05:00[America/Chicago]")
        val end = ZonedDateTime.parse("2022-09-07T12:00:00.000-05:00[America/Chicago]")

        val actual = testDAOService.priceForRange(start, end)
        assertThat(actual).isEqualTo(null)

        Unit
    }

    @Test
    fun `priceForRange should return null for given invalid range`() = runBlocking{

        // start and end on a wednesday between 14:00 and 19:00 central time
        val start = ZonedDateTime.parse("2022-09-07T14:00:00.000-05:00[America/Chicago]")
        val end = ZonedDateTime.parse("2022-09-07T19:00:00.000-05:00[America/Chicago]")

        val actual = testDAOService.priceForRange(start, end)
        assertThat(actual).isEqualTo(null)

        Unit
    }

    @Test
    fun `priceForRange should return null for an end before a start`() = runBlocking{

        val start = ZonedDateTime.parse("2022-09-07T14:00:00.000-05:00[America/Chicago]")
        val end = ZonedDateTime.parse("2022-09-07T12:00:00.000-05:00[America/Chicago]")

        val actual = testDAOService.priceForRange(start, end)
        assertThat(actual).isEqualTo(null)

        Unit
    }
}