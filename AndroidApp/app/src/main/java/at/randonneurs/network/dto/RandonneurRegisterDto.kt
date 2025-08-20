package at.randonneurs.network.dto

import com.google.gson.annotations.SerializedName

data class RandonneurRegisterDto(
    @SerializedName("Firstname")   val firstname: String,
    @SerializedName("Lastname")    val lastname: String,
    @SerializedName("DateOfBirth") val dateOfBirth: String, // "yyyy-MM-dd"
    @SerializedName("Address")     val address: String? = null,
    @SerializedName("PLZ")         val plz: Int,
    @SerializedName("City")        val city: String,
    @SerializedName("Country")     val country: String,
    @SerializedName("Email")       val email: String,
    @SerializedName("PhoneNumber") val phoneNumber: String? = null
)