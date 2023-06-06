package uz.raytel.raytel.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uz.raytel.raytel.data.remote.product.ErrorResponse
import java.io.Reader
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun getErrorMessage(json: Reader): String {
    val gson = Gson()
    val errorResponse: ErrorResponse? = gson.fromJson(json, ErrorResponse::class.java)
    return errorResponse?.error?.message.toString()
}


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}


fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


fun isNewProduct(dateString: String): Boolean {
    val date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val currentDate = LocalDateTime.now()
    val threshold = currentDate.minus(5, ChronoUnit.DAYS)
    return date.isAfter(threshold) || date.isEqual(threshold)
}

fun log(msg: String?){
    Log.d("TTTT",msg.toString())
}