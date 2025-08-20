package at.randonneurs.data

import at.randonneurs.network.ApiClient
import at.randonneurs.network.ApiService
import at.randonneurs.network.SessionManager
import at.randonneurs.network.dto.LoginDto
import at.randonneurs.network.dto.LoginResponse
import at.randonneurs.network.dto.RegisterDto
import at.randonneurs.network.dto.RegisterResultDto
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

class AuthRepository(
    private val session: SessionManager,
    private val api: ApiService = ApiClient.api
) {
    // ---------- LOGIN ----------
    suspend fun login(loginId: String, password: String): LoginResponse {
        val res = try {
            api.login(LoginDto(loginId = loginId, password = password))
        } catch (e: HttpException) {
            val msg = e.response()?.errorBody()?.string()?.take(200)
            throw IllegalStateException("HTTP ${e.code()}: ${msg ?: e.message()}")
        }

        session.saveToken(res.token)
        session.saveLoginData(res.id, res.isAdmin)
        return res
    }

    // ---------- REGISTER ----------
    suspend fun register(dto: RegisterDto): RegisterResult {
        val response: Response<ResponseBody> = api.register(dto)

        // ---- Erfolgsfall (2xx) ----
        if (response.isSuccessful) {
            val raw = safeBodyString(response.body())
            // 1) Versuche JSON {success,message}
            val json = tryParseJson(raw)
            if (json != null) {
                if (json.success) return RegisterResult.OK
                // success==false trotz 2xx → als Fehler behandeln
                throw IllegalStateException(json.message ?: "Registrierung fehlgeschlagen.")
            }
            // 2) Plain‑Text Erfolg
            // typische Texte: "Account wurde erfolgreich erstellt!"
            if (raw.contains("erfolgreich", ignoreCase = true)) return RegisterResult.OK
            // 3) Falls hierher: unklare Erfolgsantwort → als generischen Erfolg werten
            return RegisterResult.OK
        }

        // ---- Fehlerfall (4xx/5xx) ----
        val rawErr = safeBodyString(response.errorBody())
        val jsonErr = tryParseJson(rawErr) // {success:false,message:"..."} falls vorhanden
        val msg = (jsonErr?.message ?: rawErr).ifBlank { "Registrierung fehlgeschlagen (HTTP ${response.code()})." }
        val lc = msg.lowercase()

        // Häufigster Fall: E-Mail existiert bereits
        if (lc.contains("email existiert schon") || lc.contains("existiert schon")) {
            // Optional: Als Erfolg durchwinken? Dann:
            // return RegisterResult.ALREADY_EXISTS
            return RegisterResult.ALREADY_EXISTS
        }

        // Passwörter passen nicht
        if (lc.contains("passwörter stimmen nicht überein")) {
            throw IllegalStateException("Passwörter stimmen nicht überein!")
        }

        // Alles andere: als Fehler hochreichen (zeigt exakte Server-Meldung)
        throw IllegalStateException(msg)
    }

    // ---------- Helpers ----------
    private fun safeBodyString(body: ResponseBody?): String =
        try { body?.string().orEmpty().trim() } catch (_: Exception) { "" }

    private fun tryParseJson(raw: String): RegisterResultDto? {
        if (raw.isBlank()) return null
        return try {
            Gson().fromJson(raw, RegisterResultDto::class.java)
        } catch (_: JsonSyntaxException) {
            null
        } catch (_: Exception) {
            null
        }
    }
}
