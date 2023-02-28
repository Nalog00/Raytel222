package uz.raytel.raytel.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import uz.raytel.raytel.data.remote.realtime.OnlineCount
import uz.raytel.raytel.databinding.ActivityMainBinding
import kotlin.random.Random
import kotlin.random.nextInt

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val database = Firebase.database
        val myRef = database.getReference("online_count")

        myRef.setValue(OnlineCount(Random.nextInt(2000..3000)))
            .addOnFailureListener {
                it.printStackTrace()
            }
    }
}
