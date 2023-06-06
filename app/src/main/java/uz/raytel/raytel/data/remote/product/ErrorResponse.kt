package uz.raytel.raytel.data.remote.product

data class ErrorResponse(
    val error: InnerErrorResponse, val error_code: Int
)

data class InnerErrorResponse(
    val message: String
)