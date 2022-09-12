import io.gzmo.service.dao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    var allRates: AllRates
        private set

    init {
        // load seed file from resources
        val jsonString: String = this.javaClass.classLoader.getResource("seed.json").readText()
        // get AllRates
        allRates = Json.decodeFromString(jsonString)
    }

    fun init() {
        // connect
        connect()
        // build
        build()
        // seed
        seed()
    }

    private fun connect() {
        val host = System.getenv("POSTGRES_HOST")?: "localhost"
        val port = System.getenv("POSTGRES_PORT")?: "5432"
        val db = System.getenv("POSTGRES_DB")?: "spothero"
        // connect
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://$host:$port/$db"
        val user = System.getenv("POSTGRES_USER")?: "postgres"
        val password = System.getenv("POSTGRES_PASSWORD")?: "docker"
        Database.connect(jdbcURL, driverClassName, user, password)
        // a very hacky way to wait until the db is up....  :(
        var count = 0
        val max = 10
        while (true) {
            try {
                transaction {
                    SchemaUtils.drop(Rates)
                }
                break
            } catch (e: Exception) {
                Thread.sleep(1000)
                if (++count == max) {
                    throw e
                }
            }
        }
    }

    private fun build() {

        transaction {
            // drop table
            SchemaUtils.drop(Rates)
            // create table
            SchemaUtils.create(Rates)
        }
    }

    private fun seed() {
        // submit rates to db
        runBlocking { dao.updateRates(allRates) }
    }

    // utility function that will run each query in a coroutine so that they don't block
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}