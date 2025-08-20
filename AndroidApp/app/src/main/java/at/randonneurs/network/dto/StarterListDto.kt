package at.randonneurs.network.dto

data class StarterListDto(
    val id: Int,
    val lastname: String,
    val firstname: String,
    val location: String?,
    val country: String?,
    val withMedal: Boolean,
    val status: String,     // z. B. "PENDING", "DNS", "DNF", "FINISHED"
    val paid: Boolean,
    val brevetId: Int
)
