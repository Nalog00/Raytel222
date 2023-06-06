package uz.raytel.raytel.data.remote.product

import com.google.gson.annotations.SerializedName
import uz.raytel.raytel.data.remote.store.Store

data class Product(
    val id: Int,
    val number: Int,
    val alert: Boolean,
    val name: String,
    val description: String,
    val image: String,
    @SerializedName("watermark_image") val markedImage: String,
    @SerializedName("created_at") val createdAt: String,
    val store: Store
)
