package io.gzmo

import AllRates
import DatabaseFactory
import Price
import Rate
import io.ktor.client.call.body
import kotlin.test.Test
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import io.ktor.http.HttpStatusCode
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import kotlinx.serialization.json.Json

class ApplicationTest {

    @Test
    fun `get rates should return OK and rates`() = testApplication{

        val client = createClient {
            install(ContentNegotiation) {
                json(Json)
            }
        }
        val expected = DatabaseFactory.allRates
        val actual = client.get("/rates")

        assertThat(actual.status).isEqualTo(HttpStatusCode.OK)
        val actualBody = actual.body<AllRates>()
        assertThat(actualBody.rates).containsExactlyInAnyOrderElementsOf(expected.rates)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun `put rates should update rates correctly`() = testApplication{

        val client = createClient {
            install(ContentNegotiation) {
                json(Json)
            }
        }
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

        client.put("/rates") {
            setBody(expected)
            contentType(ContentType.Application.Json)
        }
        val actual = client.get("/rates")
        val actualBody = actual.body<AllRates>()
        assertThat(actualBody.rates).containsExactlyInAnyOrderElementsOf(expected.rates)
    }

    @Test
    fun `get price should return OK and proper price for given valid range`() = testApplication {

        val client = createClient {
            install(ContentNegotiation) {
                json(Json)
            }
        }
        val actual = client.get("/price") {
            url {
                parameters.append("start", "2022-09-10T10:00:00-05:00")
                parameters.append("end", "2022-09-10T12:43:00-05:00")
            }
        }
        val expectedPrice = Price(price = 2000)

        assertThat(actual.status).isEqualTo(HttpStatusCode.OK)
        val actualPrice = actual.body<Price>()
        assertThat(actualPrice).isEqualTo(expectedPrice)
    }

    @Test
    fun `get price should return BadRequest and unavailable for given invalid range`() = testApplication {

        val client = createClient {
            install(ContentNegotiation) {
                json(Json)
            }
        }
        val actual = client.get("/price") {
            url {
                parameters.append("start", "2022-09-07T14:00:00-05:00")
                parameters.append("end", "2022-09-07T19:00:00-05:00")
            }
        }

        assertThat(actual.status).isEqualTo(HttpStatusCode.BadRequest)
        val actualPrice = actual.body<String>()
        assertThat(actualPrice).isEqualTo("unavailable")
    }

    @Test
    fun `get price should return BadRequest and unavailable for given an end before start`() = testApplication {

        val client = createClient {
            install(ContentNegotiation) {
                json(Json)
            }
        }
        val actual = client.get("/price") {
            url {
                parameters.append("start", "2022-09-07T14:00:00-05:00")
                parameters.append("end", "2022-09-07T12:00:00-05:00")
            }
        }

        assertThat(actual.status).isEqualTo(HttpStatusCode.BadRequest)
        val actualPrice = actual.body<String>()
        assertThat(actualPrice).isEqualTo("unavailable")
    }

    @Test
    fun `get price should return BadRequest and unavailable for malformed range`() = testApplication {

        val client = createClient {
            install(ContentNegotiation) {
                json(Json)
            }
        }
        val actual = client.get("/price") {
            url {
                parameters.append("start", "202-09-07T14:00:00-05:00")
                parameters.append("end", "2022-09-07T19:00:00-05:00")
            }
        }

        assertThat(actual.status).isEqualTo(HttpStatusCode.BadRequest)
        val actualPrice = actual.body<String>()
        assertThat(actualPrice).isEqualTo("unavailable")
    }

    @Test
    fun `get price should return OK and should convert times to central time`() = testApplication {

        val client = createClient {
            install(ContentNegotiation) {
                json(Json)
            }
        }
        val actual = client.get("/price") {
            url {
                // start and end on September 8th (Thursday) between 22:00 and 23:00 Calcutta time
                parameters.append("start", "2022-09-08T22:00:00+05:30")
                parameters.append("end", "2022-09-08T23:00:00+05:30")
            }
        }
        // which is between 11:30 and 12:30 Thursday central time, so expecting 1500
        val expectedPrice = Price(price = 1500)

        assertThat(actual.status).isEqualTo(HttpStatusCode.OK)
        val actualPrice = actual.body<Price>()
        assertThat(actualPrice).isEqualTo(expectedPrice)
    }
}