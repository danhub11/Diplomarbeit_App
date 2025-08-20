package at.randonneurs

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import at.randonneurs.data.BrevetSelectionStore
import at.randonneurs.databinding.ActivityFavouritesBinding
import at.randonneurs.network.ApiClient
import at.randonneurs.network.dto.BrevetDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouritesBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val allItems = mutableListOf<BrevetDto>()
    private val favItems = mutableListOf<BrevetDto>()
    private lateinit var adapter: BrevetFavoritesAdapter
    private lateinit var store: BrevetSelectionStore

    private val defaultYear = 2025

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topAppBar.setNavigationOnClickListener { finish() }
        supportActionBar?.title = getString(R.string.label_my_brevets)

        store = BrevetSelectionStore(this)

        binding.recyclerViewFavs.layoutManager = LinearLayoutManager(this)
        adapter = BrevetFavoritesAdapter(favItems, store)
        binding.recyclerViewFavs.adapter = adapter
        binding.recyclerViewFavs.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        loadAndShowFavourites(defaultYear)
    }

    private fun loadAndShowFavourites(year: Int) {
        scope.launch {
            try {
                val res = withContext(Dispatchers.IO) { ApiClient.api.getBrevetsByYear(year) }
                if (res.isSuccessful) {
                    val list = res.body().orEmpty()
                    allItems.clear()
                    allItems.addAll(list)

                    favItems.clear()
                    favItems.addAll(allItems.filter { store.get(it.id).selected })

                    adapter.notifyDataSetChanged()

                    if (favItems.isEmpty()) {
                        Toast.makeText(
                            this@FavouritesActivity,
                            getString(R.string.toast_no_brevets, year),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@FavouritesActivity,
                        getString(R.string.toast_load_failed_http, res.code()),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@FavouritesActivity,
                    getString(R.string.toast_load_failed_msg, e.message ?: "error"),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
