package uz.raytel.raytel.data.remote.auth

data class ConfirmSmsRequestData(
    val phone: String,
    val code: Int
)
