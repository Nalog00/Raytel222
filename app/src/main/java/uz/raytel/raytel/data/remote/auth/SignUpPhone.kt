package uz.raytel.raytel.data.remote.auth

import com.google.gson.annotations.SerializedName

data class SignUpPhone(
    val name: String,
    val phone: String,
    @SerializedName("device_id") val deviceId: String,
    val image: String,
    val password: String
)
