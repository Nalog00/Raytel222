package uz.raytel.raytel.data.remote.auth

import com.google.gson.annotations.SerializedName

data class NewPaymentResponseData(
    val id: Int,
    val name: String,
    val image: String,
    val phone: String,
    val status: String,
    @SerializedName("created_at") val createdAt: String
)
