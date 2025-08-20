package at.randonneurs.network.dto

/**
 * Mappt auf BrevetBackend.Dtos.ParticipantStatusUpdateDto
 *  public int ParticipantId { get; set; }
 *  public string Status { get; set; }
 */
data class ParticipantStatusUpdateDto(
    val participantId: Int,
    val status: String
)
