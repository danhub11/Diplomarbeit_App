package at.randonneurs.data

import android.content.Context

class SettingsStore(context: Context) {

    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private companion object {
        const val KEY_LANG = "langTag"                  // "system" | "de" | "en"
        const val KEY_KEEP_SIGNED_IN = "keepSignedIn"   // true = beim Start überspringen
    }

    // Sprache
    fun languageTag(): String = prefs.getString(KEY_LANG, "system") ?: "system"
    fun setLanguageTag(tag: String) { prefs.edit().putString(KEY_LANG, tag).apply() }

    // Angemeldet bleiben
    fun keepSignedIn(): Boolean = prefs.getBoolean(KEY_KEEP_SIGNED_IN, true) // Default: EIN
    fun setKeepSignedIn(enabled: Boolean) { prefs.edit().putBoolean(KEY_KEEP_SIGNED_IN, enabled).apply() }
}
