package at.randonneurs

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.randonneurs.data.AuthRepository
import at.randonneurs.data.SettingsStore
import at.randonneurs.databinding.ActivityMainBinding
import at.randonneurs.network.ApiClient
import at.randonneurs.network.SessionManager
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Repository korrekt mit SessionManager initialisieren
    private val auth by lazy { AuthRepository(SessionManager(this)) }

    private fun String.isGuid() =
        Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$").matches(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // >>> Auto-Skip: Wenn Token existiert und "Angemeldet bleiben" aktiv, direkt ins Dashboard
        val session = SessionManager(this)
        val settings = SettingsStore(this)
        if (settings.keepSignedIn() && !session.token().isNullOrBlank()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        binding.btnLogin.setOnClickListener {
            val loginId = binding.etId.text?.toString()?.trim().orEmpty()   // GUID!
            val password = binding.etPassword.text?.toString().orEmpty()
            if (!loginId.isGuid() || password.isEmpty()) {
                Toast.makeText(this, "Bitte gültige Login-ID (GUID) und Passwort eingeben.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            scope.launch {
                try {
                    val repo = AuthRepository(SessionManager(this@MainActivity), ApiClient.api)
                    repo.login(loginId, password)
                    startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Login fehlgeschlagen: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
