package at.randonneurs.network.dto

/**
 * Muss zu den Backend-Dtos passen.
 * WICHTIG:
 * - dateOfBirth als ISO-String "YYYY-MM-DD"
 * - plz als String
 */
data class RandonneurDto(
    val firstname: String?,
    val lastname: String?,
    val dateOfBirth: String?,   // ISO-Format "yyyy-MM-dd"
    val address: String?,
    val plz: Int?,              // Zahl!
    val city: String?,
    val country: String?,
    val email: String?,
    val phoneNumber: String?
)
