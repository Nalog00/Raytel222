package uz.raytel.raytel.data.remote.auth

import com.google.gson.annotations.SerializedName

data class SendSmsData(
    val phone: String,
    @SerializedName("device_id") val deviceId: String
)
