package at.randonneurs.network

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    private companion object {
        const val KEY_TOKEN = "token"
        const val KEY_ACCOUNT_ID = "accountId"
        const val KEY_IS_ADMIN = "isAdmin"
    }

    // --- JWT ---
    fun saveToken(token: String?) {
        prefs.edit().apply {
            if (token.isNullOrBlank()) remove(KEY_TOKEN) else putString(KEY_TOKEN, token)
        }.apply()
    }
    fun token(): String? = prefs.getString(KEY_TOKEN, null)

    // --- User-Daten aus LoginResponse ---
    fun saveLoginData(accountId: Int, isAdmin: Boolean) {
        prefs.edit()
            .putInt(KEY_ACCOUNT_ID, accountId)
            .putBoolean(KEY_IS_ADMIN, isAdmin)
            .apply()
    }
    fun accountId(): Int = prefs.getInt(KEY_ACCOUNT_ID, -1)
    fun isAdmin(): Boolean = prefs.getBoolean(KEY_IS_ADMIN, false)

    fun clear() = prefs.edit().clear().apply()

    fun getToken(): String? = prefs.getString("token", null)

}
