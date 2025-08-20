package at.randonneurs.network.dto

/**
 * Variante 1: Anmeldung per Brevet-Id
 */
data class BrevetRegisterById(
    val accountId: Int,
    val brevetId: Int,
    val withMedal: Boolean,
    val desiredDate: String? = null
)
