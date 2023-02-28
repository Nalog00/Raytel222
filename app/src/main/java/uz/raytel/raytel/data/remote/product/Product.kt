package uz.raytel.raytel.data.remote.product

import com.google.gson.annotations.SerializedName
import uz.raytel.raytel.data.remote.store.Store

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val image: String,
    @SerializedName("watermark_image") val markedImage: String,
    val store: Store
)
