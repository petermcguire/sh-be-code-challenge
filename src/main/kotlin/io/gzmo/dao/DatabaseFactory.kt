import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://localhost:5432/postgres"
        val user = "postgres"
        val password = "postgres"
        val database = Database.connect(jdbcURL, driverClassName, user, password)
    }
}