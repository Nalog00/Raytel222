package uz.raytel.raytel.utils.otp

interface OTPReceiveListener {
    fun onOTPReceived(otp: String)

    fun onOTPTimeOut()
}