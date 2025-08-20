package at.randonneurs

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.textview.MaterialTextView

class BrevetDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_brevet_detail)

        // Daten aus dem Intent abrufen
        val city = intent.getStringExtra("city").orEmpty()
        val date = intent.getStringExtra("date").orEmpty()
        val distance = intent.getStringExtra("distance").orEmpty()
        val status = intent.getStringExtra("status").orEmpty() // "AKTIV" | "INAKTIV"
        val isActive = status == "AKTIV"

        // Views referenzieren
        val tvCity = findViewById<MaterialTextView>(R.id.tvCity)
        val tvDate = findViewById<MaterialTextView>(R.id.tvDate)
        val tvDistance = findViewById<MaterialTextView>(R.id.tvDistance)
        val chipStatus = findViewById<Chip>(R.id.chipStatus)
        val ivHeart = findViewById<ImageView>(R.id.ivHeart)

        // Inhalte setzen
        tvCity.text = city
        tvDate.text = date
        tvDistance.text = distance

        if (isActive) {
            chipStatus.text = getString(R.string.status_active)
            chipStatus.setTextColor(getColor(R.color.color_status_open_text))
            chipStatus.chipBackgroundColor = getColorStateList(R.color.color_status_open_bg)
            ivHeart.isEnabled = true
            ivHeart.alpha = 1f
        } else {
            chipStatus.text = getString(R.string.status_inactive)
            chipStatus.setTextColor(getColor(R.color.color_status_ended_text))
            chipStatus.chipBackgroundColor = getColorStateList(R.color.color_status_ended_bg)
            ivHeart.isEnabled = false
            ivHeart.alpha = 0.5f
        }

        // Favoriten-Toggle (nur lokal, kein Netz-Call)
        val brevet = Brevet(city, date, distance, status)
        ivHeart.isSelected = FavoritesRepo.isFavourite(brevet)

        ivHeart.setOnClickListener {
            if (!isActive) return@setOnClickListener
            ivHeart.isSelected = !ivHeart.isSelected
            if (ivHeart.isSelected) {
                FavoritesRepo.add(brevet)
                Toast.makeText(this, getString(R.string.check_in), Toast.LENGTH_SHORT).show()
            } else {
                FavoritesRepo.remove(brevet)
                Toast.makeText(this, getString(R.string.check_out), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
