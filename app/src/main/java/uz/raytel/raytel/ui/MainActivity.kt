package uz.raytel.raytel.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.realtime.OnlineCount
import uz.raytel.raytel.databinding.ActivityMainBinding
import uz.raytel.raytel.ui.confirm.ConfirmViewModel
import uz.raytel.raytel.ui.confirm.ConfirmViewModelImpl
import uz.raytel.raytel.utils.showSnackBar
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: ConfirmViewModel by viewModels<ConfirmViewModelImpl>()
    var lastViewedProduct = 0

    @Inject
    lateinit var localStorage: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.database.getReference("online_count")
            .setValue(OnlineCount(Random.nextInt(2000..3000))).addOnFailureListener {
                it.printStackTrace()
            }

        lifecycleScope.launchWhenCreated {
            viewModel.getDetails()
        }



        initObservers()
        initListeners()
    }

    private fun initListeners() {

    }

    private fun initObservers() {
        viewModel.loadingFlow.onEach {
            binding.progressBar.isVisible = it
        }.launchIn(lifecycleScope)


        viewModel.messageFlow.onEach {
            showSnackBar(binding.fragmentContainer, it)
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            it.localizedMessage?.let { message -> showSnackBar(binding.fragmentContainer, message) }
        }.launchIn(lifecycleScope)

        viewModel.confirmDetailsFlow.onEach {
            localStorage.infoTextMessage = it.data.infoText
            localStorage.lockScreenMessage = it.data.blockText
        }.launchIn(lifecycleScope)

    }


}