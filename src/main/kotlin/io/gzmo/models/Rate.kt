import kotlinx.serialization.Serializable

@Serializable
data class Rate(
    val days: String,
    val times: String,
    val tz: String,
    val price: Int,
)