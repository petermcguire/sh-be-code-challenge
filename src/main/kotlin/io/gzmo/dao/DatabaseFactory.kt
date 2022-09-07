import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://localhost:5432/spothero"
        val user = "postgres"
        val password = "docker"
        val database = Database.connect(jdbcURL, driverClassName, user, password)

        // test out transaction
        transaction(database) {
            // create table
            SchemaUtils.create(Rates)
        }
    }
    // utility function that will run each query in a coroutine so that they don't block
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}