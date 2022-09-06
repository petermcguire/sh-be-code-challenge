import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Rate(
    val days: String,
    val times: String,
    val tz: String,
    val price: Int,
)

@Serializable
data class AllRates(
    val rates: List<Rate>,
)

object Rates: IntIdTable(){
    val days = varchar("days", 128)
    val times = varchar("times", 128)
    val tz = varchar("tz", 128)
    val price = integer("price")
}