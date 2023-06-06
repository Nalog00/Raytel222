package uz.raytel.raytel.ui.main

import android.app.AlertDialog
import android.app.DownloadManager
import android.app.Service
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.ldralighieri.corbind.view.clicks
import uz.raytel.raytel.R
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.Main
import uz.raytel.raytel.data.remote.auth.SignInDeviceId
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.data.remote.realtime.OnlineCount
import uz.raytel.raytel.databinding.FragmentMainBinding
import uz.raytel.raytel.ui.MainActivity
import uz.raytel.raytel.ui.confirm.ConfirmViewModel
import uz.raytel.raytel.ui.confirm.ConfirmViewModelImpl
import uz.raytel.raytel.ui.register.RegisterViewModel
import uz.raytel.raytel.ui.register.RegisterViewModelImpl
import uz.raytel.raytel.utils.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)
    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()
    private val loginViewModel: RegisterViewModel by viewModels<RegisterViewModelImpl>()
    private val adapter by lazy { ProductPagingAdapter() }
    private val shopProductAdapter by lazy { ProductPagingAdapter() }
    var shopSelected = false

    private val sdf = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.ROOT)

    @Inject
    lateinit var localStorage: LocalStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initListener()
        initObservers()

        lifecycleScope.launch {
            if (localStorage.firstRun) {
                loginViewModel.signInDeviceId(SignInDeviceId(localStorage.deviceId))
            } else {
                viewModel.getRandomProducts()
            }
        }
    }

    private fun initListener() {
        val database = Firebase.database
        val myRef = database.getReference("online_count")

        myRef.setValue(OnlineCount(Random.nextInt(2000..3000))).addOnFailureListener {
            it.printStackTrace()
        }

        binding.apply {
            viewPager.adapter = adapter

            viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (shopSelected.not()) {
                        (requireActivity() as MainActivity).lastViewedProduct = viewPager.currentItem
                    }
                }
            })

            adapter.authNavigation {
                when (it) {
                    "exit" -> {
                        requireActivity().finish()
                    }
                    "login" -> {
                        findNavController().navigate(MainFragmentDirections.actionMainFragmentToRegisterFragment())
                    }
                }
            }

            shopProductAdapter.authNavigation {
                when (it) {
                    "exit" -> {
                        requireActivity().finish()
                    }
                    "login" -> {
                        findNavController().navigate(MainFragmentDirections.actionMainFragmentToRegisterFragment())
                    }
                }
            }

            adapter.setOnlineCount {
                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val value = snapshot.getValue<OnlineCount>()
                        it.text = getString(R.string.text_online, value?.onlineCount)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error.toException().printStackTrace()
                    }
                })
            }

            shopProductAdapter.setOnlineCount {
                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val value = snapshot.getValue<OnlineCount>()
                        it.text = getString(R.string.text_online, value?.onlineCount)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error.toException().printStackTrace()
                    }
                })
            }

            shopProductAdapter.setOnStoreClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_shopBottomSheetDialog)
            }

            adapter.setOnStoreClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_shopBottomSheetDialog)
            }

            shopProductAdapter.setOnSaveButtonClickListener { product ->
                val manager = requireActivity().getSystemService(DownloadManager::class.java)
                val uri = product.markedImage.toUri()
                val fileName = "${sdf.format(System.currentTimeMillis())}.jpg"
                val request = DownloadManager.Request(uri).setTitle(fileName)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                    .setMimeType("image/jpeg").setAllowedOverRoaming(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, fileName
                    )
                manager.enqueue(request)
                binding.bgScreenshotAnim.setScreenShotAnimation()
            }

            adapter.setOnSaveButtonClickListener { product ->
                val manager = requireActivity().getSystemService(DownloadManager::class.java)
                val uri = product.markedImage.toUri()
                val fileName = "${sdf.format(System.currentTimeMillis())}.jpg"
                val request = DownloadManager.Request(uri).setTitle(fileName)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                    .setMimeType("image/jpeg").setAllowedOverRoaming(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, fileName
                    )
                manager.enqueue(request)
                binding.bgScreenshotAnim.setScreenShotAnimation()
            }


            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (shopSelected) {
                            lifecycleScope.launchWhenCreated {
                                shopSelectedFlow.emit(false)
                            }
                        } else {
                            requireActivity().finish()
                        }
                    }
                })


        }
    }

    private fun initObservers() {
        binding.apply {
            viewModel.loadingFlow.onEach {
                progressBar.isVisible = it
            }.launchIn(lifecycleScope)

            viewModel.messageFlow.onEach {
                showSnackBar(requireView(), it)
            }.launchIn(lifecycleScope)

            viewModel.errorFlow.onEach {
                it.localizedMessage?.let { message -> showSnackBar(message) }
            }.launchIn(lifecycleScope)

            loginViewModel.loadingFlow.onEach {
                progressBar.isVisible = it
            }.launchIn(lifecycleScope)

            loginViewModel.messageFlow.onEach {
                showSnackBar(requireView(), it)
            }.launchIn(lifecycleScope)

            loginViewModel.errorFlow.onEach {
                it.localizedMessage?.let { message -> showSnackBar(message) }
            }.launchIn(lifecycleScope)

            loginViewModel.signInDeviceIdFlow.onEach {
                localStorage.token = it.token
                localStorage.firstRun = false
                viewModel.getRandomProducts()

                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.randomProductsFlow.collectLatest { photos ->
                        if (shopSelected) {
                            shopProductAdapter.submitData(photos)
                        } else {
                            adapter.submitData(photos)
                        }
                    }
                    binding.progressBar.hide()
                }
            }.launchIn(lifecycleScope)


            if (!localStorage.firstRun) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.randomProductsFlow.collectLatest { photos ->
                        if (shopSelected) {
                            shopProductAdapter.submitData(photos)
                        } else {
                            adapter.submitData(photos)
                        }
                    }
                }
                binding.progressBar.hide()
            }

            viewModel.productsFlow.onEach {
                shopSelected = true
            }.launchIn(lifecycleScope)


            shopSelectedFlow.onEach {
                shopSelected = it
                if (shopSelected) {
                    viewModel.getProducts(localStorage.selectedStoreId)
                    binding.viewPager.adapter = shopProductAdapter
                }
                if (!shopSelected) {
                    binding.viewPager.adapter = adapter
                    viewPager.currentItem = (requireActivity() as MainActivity).lastViewedProduct
                }
            }.launchIn(lifecycleScope)
        }
    }
}
