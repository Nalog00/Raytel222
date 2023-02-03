package uz.raytel.raytel.data.auth

import com.google.gson.annotations.SerializedName

data class SignInDeviceId(
    @SerializedName("device_id") val deviceId: String
)
