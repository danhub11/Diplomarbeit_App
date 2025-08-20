package at.randonneurs.data

import android.content.Context
import at.randonneurs.network.ApiClient
import at.randonneurs.network.ApiService
import at.randonneurs.network.SessionManager
import at.randonneurs.network.dto.RandonneurDto

class AccountRepository(
    context: Context,
    private val api: ApiService = ApiClient.api
) {
    private val session = SessionManager(context)

    suspend fun profile(): RandonneurDto {
        val token = session.getToken() ?: throw IllegalStateException("Nicht eingeloggt (kein Token).")
        val res = api.profile("Bearer $token")

        if (res.isSuccessful) {
            return res.body() ?: throw IllegalStateException("Leere Server-Antwort.")
        } else {
            val msg = res.errorBody()?.string()?.ifBlank { null }
                ?: "HTTP ${res.code()} ${res.message()}"
            throw IllegalStateException(msg)
        }
    }
}
