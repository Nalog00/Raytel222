package uz.raytel.raytel.data.remote.auth

import com.google.gson.annotations.SerializedName

data class GetInfoAboutMeData(
    val type: String,
    val status: String,
    val minutes: Int,
    @SerializedName("active_minutes")
    val activeMinutes: String,
    val phone: String,
    val name: String?,
    @SerializedName("actived_at")
    val activatedAt: String

)
