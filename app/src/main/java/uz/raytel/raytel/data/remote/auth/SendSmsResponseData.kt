package uz.raytel.raytel.data.remote.auth

import com.google.gson.annotations.SerializedName

data class SendSmsResponseData(
    @SerializedName("sms_send")
    val smsSent: Boolean,
    val phone: String,
    @SerializedName("new_user")
    val newUser: Boolean
)
