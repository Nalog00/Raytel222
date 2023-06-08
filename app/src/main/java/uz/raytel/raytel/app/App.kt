package uz.raytel.raytel.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import uz.raytel.raytel.utils.SmsVerificationReceiver

@HiltAndroidApp
class App: Application() {
    companion object {
        lateinit var INSTANCE: Application
         var otpReceiver: SmsVerificationReceiver.OTPReceiveListener? = null

        fun initOTPListener(receiver: SmsVerificationReceiver.OTPReceiveListener) {
            otpReceiver = receiver
        }
    }





    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
