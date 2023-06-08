package uz.raytel.raytel.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import uz.raytel.raytel.app.App

class SmsVerificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    var otp: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    if (App.otpReceiver != null) {
                        otp = otp.replace("<#> Raytel code: ", "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        App.otpReceiver!!.onOTPReceived(otp)
                    }
                }
                CommonStatusCodes.TIMEOUT ->
                    App.otpReceiver!!.onOTPTimeOut()
            }

            Log.d("TTTTT","On receive")
        }
    }

    interface OTPReceiveListener {

        fun onOTPReceived(otp: String)

        fun onOTPTimeOut()
    }
}