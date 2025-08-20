package at.randonneurs

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import at.randonneurs.data.BrevetSelectionStore
import at.randonneurs.databinding.ActivityDashboardBinding
import at.randonneurs.network.ApiClient
import at.randonneurs.network.dto.BrevetDto
import at.randonneurs.util.SpinnerItemSelected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val allItems = mutableListOf<BrevetDto>()   // komplette Jahresliste vom Backend
    private val shownItems = mutableListOf<BrevetDto>() // nach Status gefiltert
    private lateinit var adapter: BrevetAdapter
    private lateinit var store: BrevetSelectionStore

    private lateinit var years: List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar & Titel
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setTitle(R.string.brevets25)

        store = BrevetSelectionStore(this)

        // RecyclerView
        binding.recyclerViewBrevets.layoutManager = LinearLayoutManager(this)
        adapter = BrevetAdapter(shownItems, store)
        binding.recyclerViewBrevets.adapter = adapter
        binding.recyclerViewBrevets.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        // Status-Filter
        val statusOptions = resources.getStringArray(R.array.filter_status_values)
        binding.spinnerStatus.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, statusOptions
        )

        // Jahr-Filter (2020..2030, Default 2025)
        years = (2020..2030).toList()
        val yearLabels = years.map { it.toString() }
        binding.spinnerYear.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, yearLabels
        )
        val defaultYear = 2025
        val defaultIndex = years.indexOf(defaultYear).takeIf { it >= 0 }
            ?: years.indexOf(LocalDate.now().year)
        if (defaultIndex >= 0) binding.spinnerYear.setSelection(defaultIndex)

        // Initial laden
        loadBrevetsForYear(years[defaultIndex])

        // Listener
        binding.spinnerYear.onItemSelectedListener = SpinnerItemSelected { pos ->
            val y = years[pos]
            if (y == 2025) supportActionBar?.setTitle(R.string.brevets25)
            else supportActionBar?.title = "Brevets $y"
            loadBrevetsForYear(y)
        }
        binding.spinnerStatus.onItemSelectedListener = SpinnerItemSelected {
            applyStatusFilter()
        }
    }

    // ===== Overflow-Menü (3 Punkte) =====
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard_actions, menu)
        binding.topAppBar.overflowIcon =
            AppCompatResources.getDrawable(this, R.drawable.ic_more_vert_24)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_my_brevets -> { startActivity(Intent(this, FavouritesActivity::class.java)); true }
            R.id.menu_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ===== Daten laden & filtern =====
    private fun loadBrevetsForYear(year: Int) {
        scope.launch {
            try {
                val res = withContext(Dispatchers.IO) { ApiClient.api.getBrevetsByYear(year) }
                if (res.isSuccessful) {
                    val list = res.body().orEmpty()
                    allItems.clear(); allItems.addAll(list)
                    applyStatusFilter()
                    if (list.isEmpty()) {
                        Toast.makeText(this@DashboardActivity, "Keine Brevets gefunden ($year).", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DashboardActivity, "Laden fehlgeschlagen: HTTP ${res.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DashboardActivity, "Laden fehlgeschlagen: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun applyStatusFilter() {
        val selected = binding.spinnerStatus.selectedItem?.toString()?.lowercase() ?: "alle"
        val today = LocalDate.now()

        fun isOpen(dto: BrevetDto): Boolean {
            val d = try {
                OffsetDateTime.parse(dto.date).withOffsetSameInstant(ZoneOffset.UTC).toLocalDate()
            } catch (_: Exception) {
                try { LocalDate.parse(dto.date.take(10)) } catch (_: Exception) { today }
            }
            return !d.isBefore(today)
        }

        shownItems.clear()
        when (selected) {
            getString(R.string.filter_status_open).lowercase()  -> shownItems.addAll(allItems.filter { isOpen(it) })
            getString(R.string.filter_status_ended).lowercase() -> shownItems.addAll(allItems.filter { !isOpen(it) })
            else -> shownItems.addAll(allItems)
        }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
