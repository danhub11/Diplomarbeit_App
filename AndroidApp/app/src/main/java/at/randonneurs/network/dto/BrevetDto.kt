package at.randonneurs.network.dto

/**
 * Entspricht BrevetBackend.Dtos.BrevetDto:
 *  public int Id {get;set;}
 *  public int Distance {get;set;}
 *  public DateTime Date {get;set;}
 *  public string? Town {get;set;}
 */
data class BrevetDto(
    val id: Int,
    val distance: Int,
    val date: String,   // ISO-String vom Server (z.B. "2025-03-15T00:00:00")
    val town: String?
)
