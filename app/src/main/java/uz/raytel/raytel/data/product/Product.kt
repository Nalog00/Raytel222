package uz.raytel.raytel.data.product

import com.google.gson.annotations.SerializedName
import uz.raytel.raytel.data.store.Store

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val image: String,
    @SerializedName("watermark_image") val markedImage: String,
    val store: Store
)
