package at.randonneurs.network.dto

data class LoginResponse(
    val token: String,
    val id: Int,
    val isAdmin: Boolean
)
