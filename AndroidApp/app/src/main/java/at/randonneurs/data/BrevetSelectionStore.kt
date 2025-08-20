package at.randonneurs.data

import android.content.Context
import org.json.JSONObject

/**
 * Speichert Auswahl pro Brevet (id):
 *  - selected (Checkbox oben rechts)
 *  - withMedal (Checkbox unten)
 *  - desiredDate (tt.mm.jjjj)
 */
class BrevetSelectionStore(context: Context) {
    private val prefs = context.getSharedPreferences("brevet_selections", Context.MODE_PRIVATE)

    data class Selection(
        val selected: Boolean = false,
        val withMedal: Boolean = false,
        val desiredDate: String = ""
    )

    fun get(id: Int): Selection {
        val raw = prefs.getString(id.toString(), null) ?: return Selection()
        return try {
            val j = JSONObject(raw)
            Selection(
                selected = j.optBoolean("selected", false),
                withMedal = j.optBoolean("withMedal", false),
                desiredDate = j.optString("desiredDate", "")
            )
        } catch (_: Exception) {
            Selection()
        }
    }

    fun put(id: Int, sel: Selection) {
        val j = JSONObject()
            .put("selected", sel.selected)
            .put("withMedal", sel.withMedal)
            .put("desiredDate", sel.desiredDate)
        prefs.edit().putString(id.toString(), j.toString()).apply()
    }
}
