package at.randonneurs.util

import android.view.View
import android.widget.AdapterView

/** Kleiner Helper für Spinner.onItemSelectedListener mit Lambda. */
class SpinnerItemSelected(private val onSelected: (pos: Int) -> Unit) :
    AdapterView.OnItemSelectedListener {
    override fun onItemSelected(
        parent: AdapterView<*>, view: View?, position: Int, id: Long
    ) = onSelected(position)

    override fun onNothingSelected(parent: AdapterView<*>) {}
}
