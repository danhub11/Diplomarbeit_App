package at.randonneurs

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import at.randonneurs.data.SettingsStore
import at.randonneurs.network.SessionManager
import at.randonneurs.util.SpinnerItemSelected
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var store: SettingsStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        store = SettingsStore(this)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.title = getString(R.string.settings_title)

        // --- Sprache ---
        val spinnerLang = findViewById<Spinner>(R.id.spinnerLanguage)
        val labels = resources.getStringArray(R.array.settings_language_labels)
        spinnerLang.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            labels
        )
        val tags = resources.getStringArray(R.array.settings_language_tags) // "system" | "de" | "en"
        val savedTag = store.languageTag()
        val startIndex = tags.indexOf(savedTag).let { if (it >= 0) it else 0 }
        spinnerLang.setSelection(startIndex)

        spinnerLang.onItemSelectedListener = SpinnerItemSelected { pos ->
            val tag = tags[pos]
            if (tag != store.languageTag()) applyLanguageSafe(tag)
        }

        // --- Angemeldet bleiben ---
        val swKeepSignedIn = findViewById<SwitchMaterial>(R.id.switchKeepSignedIn)
        swKeepSignedIn.isChecked = store.keepSignedIn()
        swKeepSignedIn.setOnCheckedChangeListener { _, checked ->
            store.setKeepSignedIn(checked)
            Toast.makeText(
                this,
                if (checked) getString(R.string.settings_keep_signed_in_on) else getString(R.string.settings_keep_signed_in_off),
                Toast.LENGTH_SHORT
            ).show()
        }

        // --- Abmelden ---
        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            // Token & Sessiondaten löschen
            SessionManager(this).clear()

            Toast.makeText(this, getString(R.string.settings_logout_done), Toast.LENGTH_SHORT).show()

            // Komplett zum Anfang (Backstack leeren)
            val i = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(i)
        }
    }

    /** Sprache umstellen; auf älteren AppCompat-Versionen nicht crashen. */
    private fun applyLanguageSafe(tag: String) {
        store.setLanguageTag(tag)
        try {
            val locales = if (tag == "system") {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(tag)
            }
            AppCompatDelegate.setApplicationLocales(locales) // benötigt AppCompat >= 1.6
            Toast.makeText(this, getString(R.string.settings_lang_applied), Toast.LENGTH_SHORT).show()
            recreate()
        } catch (_: NoSuchMethodError) {
            Toast.makeText(
                this,
                "Sprachwechsel benötigt AppCompat 1.6+.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
