package at.randonneurs.network.dto

/**
 * Antwort vom Backend nach erfolgreicher Registrierung.
 * Wir zeigen loginId im UI an, damit man sich gleich einloggen kann.
 */
data class RegisterResponse(
    val accountId: Int,
    val loginId: String,
    val message: String? = null
)
