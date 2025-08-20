package at.randonneurs

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.randonneurs.data.BrevetSelectionStore
import at.randonneurs.network.dto.BrevetDto
import com.google.android.material.checkbox.MaterialCheckBox
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class BrevetAdapter(
    private val data: List<BrevetDto>,
    private val store: BrevetSelectionStore
) : RecyclerView.Adapter<BrevetAdapter.VH>() {

    private val outFormatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.GERMAN)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_brevet, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val b = data[position]

        // UI-Basis
        holder.city.text = b.town ?: "(ohne Ort)"
        val localDate = parseToLocalDate(b.date)
        holder.date.text = localDate.format(outFormatter)
        holder.distance.text = "${b.distance} km"

        // Status
        val isOpen = !localDate.isBefore(LocalDate.now())
        if (isOpen) {
            holder.badge.text = "Open"
            holder.badge.setBackgroundResource(R.drawable.status_pill_open)
        } else {
            holder.badge.text = "Ended"
            holder.badge.setBackgroundResource(R.drawable.status_pill_ended)
        }

        // Auswahl-Status aus Store laden
        val sel = store.get(b.id)
        holder.cbSelect.setOnCheckedChangeListener(null)
        holder.cbMedal.setOnCheckedChangeListener(null)
        holder.cbSelect.isChecked = sel.selected
        holder.cbMedal.isChecked = sel.withMedal

        // TextWatcher sauber handhaben
        holder.etDesiredDate.removeTextChangedListener(holder.tw)
        holder.etDesiredDate.setText(sel.desiredDate)
        holder.tw = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                store.put(b.id, sel.copy(desiredDate = (s?.toString() ?: "")))
            }
        }
        holder.etDesiredDate.addTextChangedListener(holder.tw)

        // Listener -> lokal speichern
        holder.cbSelect.setOnCheckedChangeListener { _, checked ->
            store.put(
                b.id,
                sel.copy(
                    selected = checked,
                    desiredDate = holder.etDesiredDate.text?.toString() ?: "",
                    withMedal = holder.cbMedal.isChecked
                )
            )
        }
        holder.cbMedal.setOnCheckedChangeListener { _, checked ->
            store.put(
                b.id,
                sel.copy(
                    withMedal = checked,
                    desiredDate = holder.etDesiredDate.text?.toString() ?: "",
                    selected = holder.cbSelect.isChecked
                )
            )
        }
    }

    override fun getItemCount(): Int = data.size

    private fun parseToLocalDate(iso: String): LocalDate {
        return try {
            OffsetDateTime.parse(iso).withOffsetSameInstant(ZoneOffset.UTC).toLocalDate()
        } catch (_: Exception) {
            try { LocalDate.parse(iso.take(10)) } catch (_: Exception) { LocalDate.now() }
        }
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val city: TextView = v.findViewById(R.id.textCity)
        val date: TextView = v.findViewById(R.id.textDate)
        val distance: TextView = v.findViewById(R.id.textDistance)
        val badge: TextView = v.findViewById(R.id.badgeStatus)
        val cbSelect: MaterialCheckBox = v.findViewById(R.id.cbSelect)
        val cbMedal: MaterialCheckBox = v.findViewById(R.id.cbMedal)
        val etDesiredDate: EditText = v.findViewById(R.id.etDesiredDate)
        var tw: TextWatcher? = null
    }
}
