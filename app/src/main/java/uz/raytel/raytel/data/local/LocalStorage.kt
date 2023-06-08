package uz.raytel.raytel.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.Secure
import dagger.hilt.android.qualifiers.ApplicationContext
import uz.raytel.raytel.app.App
import uz.raytel.raytel.utils.BooleanPreference
import uz.raytel.raytel.utils.IntPreference
import uz.raytel.raytel.utils.StringPreference
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("HardwareIds")
@Singleton
class LocalStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val pref: SharedPreferences =
            App.INSTANCE.getSharedPreferences("LocalStorage", Context.MODE_PRIVATE)
    }

    var firstRun by BooleanPreference(pref, defValue = true)

    var token by StringPreference(pref)

    val deviceId by StringPreference(
        pref,
        Secure.getString(context.contentResolver, Secure.ANDROID_ID)
    )

    var signedIn by BooleanPreference(pref, defValue = false)

    var selectedStoreId by IntPreference(pref, defValue = 1)

    var lockScreenMessage by StringPreference(pref)
    var infoTextMessage by StringPreference(pref)
}
