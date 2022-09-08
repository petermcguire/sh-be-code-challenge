import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.time
import java.time.LocalTime

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

data class NewRates(
    val day: String,
    val start: LocalTime,
    val finish: LocalTime,
    val price: Int,
)

object Rates: IntIdTable(){
    val day = varchar("day", 128)
    val start = time("start")
    val finish = time("finish")
    val price = integer("price")
}
