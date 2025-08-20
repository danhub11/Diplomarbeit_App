package at.randonneurs.network

import at.randonneurs.network.dto.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ===== Auth =====
    @POST("Accounts/Login")
    suspend fun login(@Body dto: LoginDto): LoginResponse

    @POST("Accounts/Register")
    suspend fun register(@Body dto: RegisterDto): Response<ResponseBody>

    // ===== Account =====
    @GET("Accounts/profile")
    suspend fun profile(
        @Header("Authorization") bearer: String
    ): Response<RandonneurDto>

    // ===== Brevets (OHNE Auth-Header) =====
    @GET("Brevets")
    suspend fun getBrevetsByYear(
        @Query("year") year: Int,
        @Query("accountId") accountId: Int? = null,
        @Query("visibilitySettings") visibilitySettings: String? = null
    ): Response<List<BrevetDto>>
}
