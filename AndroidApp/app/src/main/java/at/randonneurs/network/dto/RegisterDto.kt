package at.randonneurs.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Muss den .NET RegisterDto matchen:
 *  - randonneur
 *  - passwordHash
 *  - repeatPassword
 * Property-Namen sind camelCase, wie es ASP.NET Core standardmäßig erwartet.
 */
data class RegisterDto(
    @SerializedName("PasswordHash")   val passwordHash: String,
    @SerializedName("RepeatPassword") val repeatPassword: String,
    @SerializedName("Randonneur")     val randonneur: RandonneurRegisterDto
)