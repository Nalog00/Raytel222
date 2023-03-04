package uz.raytel.raytel.ui.main

import android.app.DownloadManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.raytel.raytel.R
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.auth.SignInDeviceId
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.data.remote.realtime.OnlineCount
import uz.raytel.raytel.databinding.FragmentMainBinding
import uz.raytel.raytel.ui.login.LoginViewModel
import uz.raytel.raytel.ui.login.LoginViewModelImpl
import uz.raytel.raytel.utils.bottomShopListCloseFlow
import uz.raytel.raytel.utils.showSnackBar
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)
    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()
    private val loginViewModel: LoginViewModel by viewModels<LoginViewModelImpl>()
    private lateinit var navController: NavController
    private var _adapter: PageAdapter? = null
    private val adapter: PageAdapter get() = _adapter!!
    private var page = 1
    private var lastPage = 0
    private var products = mutableListOf<Product>()
    private val sdf = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.ROOT)

    @Inject
    lateinit var localStorage: LocalStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()



        lifecycleScope.launchWhenCreated {
            if (localStorage.signedIn) {
                viewModel.getProducts(page, localStorage.selectedStoreId, 50)
            } else {
                loginViewModel.signInDeviceId(SignInDeviceId(localStorage.deviceId))
            }
        }

        initListener()
        initObservers()
    }

    private fun initListener() {
        _adapter = PageAdapter(requireContext())

        val database = Firebase.database
        val myRef = database.getReference("online_count")

        myRef.setValue(OnlineCount(Random.nextInt(2000..3000)))
            .addOnFailureListener {
                it.printStackTrace()
            }

        binding.apply {
            viewPager.adapter = adapter

            adapter.authNavigation {
                val action = when (it) {
                    "register" -> R.id.action_mainFragment_to_registerFragment
                    else -> R.id.action_mainFragment_to_loginFragment
                }
                navController.navigate(action)
            }

            adapter.setOnlineCount {
                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val value = snapshot.getValue<OnlineCount>()
                        it.text = value?.onlineCount.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error.toException().printStackTrace()
                    }
                })
            }

            adapter.setOnStoreClickListener {
                navController.navigate(R.id.action_mainFragment_to_shopBottomSheetDialog)
            }

            adapter.getCurrentItem {
                lifecycleScope.launch {
                    if (products.size > 1) {
                        val beforeLastProduct = products[products.lastIndex - 1]
                        if (beforeLastProduct == it && page < lastPage) {
                            viewModel.getProducts(++page, localStorage.selectedStoreId, 50)
                        }
                    }
                }
            }

            adapter.setOnSaveButtonClickListener { product ->
                val manager = requireActivity().getSystemService(DownloadManager::class.java)
                val uri = product.markedImage.toUri()
                val fileName = "${sdf.format(System.currentTimeMillis())}.jpg"
                val request = DownloadManager.Request(uri)
                    .setTitle(fileName)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                    .setMimeType("image/jpeg")
                    .setAllowedOverRoaming(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

                manager.enqueue(request)
            }
        }
    }

    private fun initObservers() {
        binding.apply {
            viewModel.loadingFlow.onEach {
                progressBar.isVisible = it
            }.launchIn(lifecycleScope)

            viewModel.messageFlow.onEach {
                showSnackBar(progressBar, it)
            }.launchIn(lifecycleScope)

            viewModel.errorFlow.onEach {
                it.localizedMessage?.let { message -> showSnackBar(progressBar, message) }
            }.launchIn(lifecycleScope)

            loginViewModel.loadingFlow.onEach {
                progressBar.isVisible = it
            }.launchIn(lifecycleScope)

            loginViewModel.messageFlow.onEach {
                showSnackBar(progressBar, it)
            }.launchIn(lifecycleScope)

            loginViewModel.errorFlow.onEach {
                it.localizedMessage?.let { message -> showSnackBar(progressBar, message) }
            }.launchIn(lifecycleScope)

            loginViewModel.signInDeviceIdFlow.onEach {
                localStorage.token = it.token
                viewModel.getRandomProducts()
            }.launchIn(lifecycleScope)

            viewModel.randomProductsFlow.onEach {
                adapter.models = it.data.toMutableList()
            }.launchIn(lifecycleScope)

            viewModel.productsFlow.onEach {
                lastPage = it.pagination.totalPages
                if (it.pagination.currentPage == 1) {
                    adapter.models.clear()
                    products.clear()
                    products = it.data.toMutableList()
                    adapter.models = it.data.toMutableList()
                } else {
                    products.addAll(it.data)
                    adapter.addItems(it.data)
                }
            }.launchIn(lifecycleScope)

            bottomShopListCloseFlow.onEach {
                page = 1
                viewModel.getProducts(page, localStorage.selectedStoreId, 50)
            }.launchIn(lifecycleScope)
        }
    }
}
