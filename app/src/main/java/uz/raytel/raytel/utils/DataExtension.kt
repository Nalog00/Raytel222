package uz.raytel.raytel.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uz.raytel.raytel.data.remote.product.ErrorResponse
import java.io.Reader

fun getErrorMessage(json: Reader): String {
    val gson = Gson()
    val type = object : TypeToken<ErrorResponse>() {}.type
    val errorResponse: ErrorResponse? =
        gson.fromJson(json, type)
    return errorResponse?.error?.message.toString()
}
