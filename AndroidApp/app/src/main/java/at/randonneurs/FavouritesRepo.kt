package at.randonneurs

object FavoritesRepo {
    // Liste der Favoriten, die als Brevet-Objekte gespeichert werden
    private val favorites = mutableListOf<Brevet>()

    // Füge ein Brevet hinzu
    fun add(brevet: Brevet) {
        val key = "${brevet.city}-${brevet.date}-${brevet.distance}"
        if (!favorites.contains(brevet)) {
            favorites.add(brevet)
        }
    }

    // Entferne ein Brevet
    fun remove(brevet: Brevet) {
        favorites.remove(brevet)
    }

    // Überprüfen, ob ein Brevet in den Favoriten ist
    fun isFavourite(brevet: Brevet): Boolean {
        return favorites.contains(brevet)
    }

    // Alle Favoriten zurückgeben
    fun all(): List<Brevet> {
        return favorites
    }

    // Generiere einen eindeutigen Schlüssel für Favoriten
    fun key(city: String, date: String, distance: String): String {
        return "$city-$date-$distance"
    }
}
