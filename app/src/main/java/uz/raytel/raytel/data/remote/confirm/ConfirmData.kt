package uz.raytel.raytel.data.remote.confirm

import com.google.gson.annotations.SerializedName

data class ConfirmData(
    val description: String,
    val title: String,
    val price: Int,
    @SerializedName("card_number") val cardNumber: String,
    @SerializedName("card_holder") val cardHolder: String,
    @SerializedName("end_text") val endText: String,
    val phone: String,
    @SerializedName("block_text") val blockText: String,
    @SerializedName("unblock_text") val unblockText: String
)
