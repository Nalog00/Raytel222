package uz.raytel.raytel.data.remote.confirm

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    val id: Int,
    val url: String,
    val path: String,
    @SerializedName("created_at") val createdAt: String
)
