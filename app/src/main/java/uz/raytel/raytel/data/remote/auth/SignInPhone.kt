package uz.raytel.raytel.data.remote.auth

import com.google.gson.annotations.SerializedName

data class SignInPhone(
    val phone: String,
    val password: String,
    @SerializedName("device_id") val deviceId: String
)
