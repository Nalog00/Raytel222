package uz.raytel.raytel.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.raytel.raytel.data.remote.realtime.OnlineCount
import uz.raytel.raytel.databinding.ActivityMainBinding
import uz.raytel.raytel.utils.shopSelectedFlow
import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var fileName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        val database = Firebase.database
        val myRef = database.getReference("online_count")

        myRef.setValue(OnlineCount(Random.nextInt(2000..3000)))
            .addOnFailureListener {
                it.printStackTrace()
            }

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onComplete)
    }

    override fun onBackPressed() {
        var shopSelected = false
        shopSelectedFlow.onEach {
            shopSelected = it
        }.launchIn(lifecycleScope)

        if (shopSelected) {
            lifecycleScope.launchWhenCreated {
                shopSelectedFlow.emit(false)
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun openFile() {
        val intent = Intent(Intent.ACTION_VIEW)
        val file =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator + fileName)
        intent.setDataAndType(file.toUri(), "image/jpeg")
        startActivity(intent)
    }

    private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Snackbar.make(binding.fragmentContainer, "Tabıslı júklendi!", Snackbar.LENGTH_SHORT)
                .show()
        }
    }
}
