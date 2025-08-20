package at.randonneurs.network.dto

/**
 * JSON-Body für POST /Brevets/Register
 * Passe Feldnamen bei Bedarf an dein Backend an.
 */
data class BrevetRegisterRequest(
    val accountId: Int,            // falls Backend aus Token liest, wird es ignoriert
    val brevetId: Int,
    val withMedal: Boolean,
    val desiredDate: String? = null // ISO "yyyy-MM-dd" oder null
)
