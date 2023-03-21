package uz.raytel.raytel.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.raytel.raytel.data.remote.realtime.OnlineCount
import uz.raytel.raytel.databinding.ActivityMainBinding
import uz.raytel.raytel.utils.shopSelectedFlow
import kotlin.random.Random
import kotlin.random.nextInt

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

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
}
