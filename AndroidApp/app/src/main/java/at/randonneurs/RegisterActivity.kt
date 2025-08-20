package at.randonneurs

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.randonneurs.data.AuthRepository
import at.randonneurs.data.RegisterResult
import at.randonneurs.databinding.ActivityRegisterBinding
import at.randonneurs.network.SessionManager
import at.randonneurs.network.dto.RegisterDto
import at.randonneurs.network.dto.RandonneurRegisterDto
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val auth by lazy { AuthRepository(SessionManager(this)) }

    private var isSubmitting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreate.setOnClickListener {
            if (isSubmitting) return@setOnClickListener
            isSubmitting = true
            binding.btnCreate.isEnabled = false
            binding.btnCreate.text = getString(R.string.registering)

            val first = binding.etFirstname.text?.toString()?.trim().orEmpty()
            val last  = binding.etLastname.text?.toString()?.trim().orEmpty()
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val pass  = binding.etPassword.text?.toString()?.trim().orEmpty()
            val city  = binding.etCity.text?.toString()?.trim().orEmpty()
            val country = binding.etCountry.text?.toString()?.trim().orEmpty()
            val address = binding.etAddress.text?.toString()?.trim().orEmpty()
            val phone = binding.etPhone.text?.toString()?.trim().orEmpty()
            val plz = binding.etPlz.text?.toString()?.trim()?.toIntOrNull()

            // Datum auf "yyyy-MM-dd" normalisieren (Eingabe "2007-01-01" oder "2007.01.01")
            val dobInput = binding.etBirth.text?.toString()?.trim().orEmpty().replace('.', '-')
            val dob = try {
                if (dobInput.isNotBlank())
                    LocalDate.parse(dobInput).format(DateTimeFormatter.ISO_LOCAL_DATE)
                else ""
            } catch (_: Exception) { "" }

            // Pflichtfelder prüfen
            if (first.isBlank() || last.isBlank() || email.isBlank() || pass.isBlank() ||
                plz == null || city.isBlank() || country.isBlank()
            ) {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_LONG).show()
                resetButton()
                return@setOnClickListener
            }

            val dto = RegisterDto(
                passwordHash   = pass,
                repeatPassword = pass,
                randonneur = RandonneurRegisterDto(
                    firstname   = first,
                    lastname    = last,
                    dateOfBirth = dob, // darf leer sein, Backend setzt ggf. nichts
                    address     = address.ifBlank { null },
                    plz         = plz,
                    city        = city,
                    country     = country,
                    email       = email,
                    phoneNumber = phone.ifBlank { null }
                )
            )

            scope.launch {
                try {
                    when (auth.register(dto)) {
                        RegisterResult.OK -> {
                            Toast.makeText(
                                this@RegisterActivity,
                                getString(R.string.register_success),
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                        RegisterResult.ALREADY_EXISTS -> {
                            // Bei Doppelklick/E-Mail schon da: als Erfolg behandeln
                            Toast.makeText(
                                this@RegisterActivity,
                                getString(R.string.register_exists_treated_ok),
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registrierung fehlgeschlagen: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    resetButton()
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun resetButton() {
        isSubmitting = false
        binding.btnCreate.isEnabled = true
        binding.btnCreate.text = getString(R.string.create_account)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
