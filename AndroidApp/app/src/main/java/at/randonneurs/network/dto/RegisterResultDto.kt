package at.randonneurs.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Falls das Backend JSON liefert, entspricht es dieser Form.
 * ASP.NET serialisiert standardmäßig camelCase.
 */
data class RegisterResultDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)
