import io.gzmo.dao.dao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        // connect
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://localhost:5432/spothero"
        val user = "postgres"
        val password = "docker"
        Database.connect(jdbcURL, driverClassName, user, password)
        // build
        build()
        // seed
        seed("seed.json")
    }

    private fun build() {
        // build db if not already done so
        transaction {
            // drop table
            SchemaUtils.drop(Rates)
            // create table
            SchemaUtils.create(Rates)
        }
    }

    private fun seed(seedFile: String) {
        // load seed file from resources
        val jsonString: String = this.javaClass.classLoader.getResource(seedFile).readText()
        // get AllRates
        val rates = Json.decodeFromString<AllRates>(jsonString)
        // submit rates to db
        runBlocking { dao.updateRates(rates) }
    }

    // utility function that will run each query in a coroutine so that they don't block
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}