package uz.raytel.raytel.ui.confirm

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.robertlevonyan.components.picker.ItemModel
import com.robertlevonyan.components.picker.ItemType
import com.robertlevonyan.components.picker.PickerDialog
import com.robertlevonyan.components.picker.pickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import uz.raytel.raytel.R
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.auth.NewPaymentRequestData
import uz.raytel.raytel.databinding.FragmentConfirmPaymentBinding
import uz.raytel.raytel.ui.MainActivity
import uz.raytel.raytel.ui.register.RegisterViewModel
import uz.raytel.raytel.ui.register.RegisterViewModelImpl
import uz.raytel.raytel.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class PaymentConfirmFragment : Fragment(R.layout.fragment_confirm_payment) {
    private val binding by viewBinding(FragmentConfirmPaymentBinding::bind)
    private val args: PaymentConfirmFragmentArgs by navArgs()
    private val registerViewModel: RegisterViewModel by viewModels<RegisterViewModelImpl>()
    private val confirmViewModel: ConfirmViewModel by viewModels<ConfirmViewModelImpl>()
    private var imagePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", "")
    private var imageSelected = false

    @Inject
    lateinit var localStorage: LocalStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initObservers()

    }

    private fun initListeners() {
        lifecycleScope.launchWhenCreated {
            confirmViewModel.getDetails()
        }

        binding.apply {
            btnUpload.clickWithDebounce(lifecycleScope) {
                pickerDialog {
                    setTitle("Súwretti saylań")
                    setListType(PickerDialog.ListType.TYPE_GRID)
                    setItems(setOf(ItemModel(ItemType.ITEM_GALLERY)))
                }.setPickerCloseListener { _, uris ->
                    if (uris.isNotEmpty()) {
                        lifecycleScope.launch {
                            val fileUri = uris.first()
                            val file = FileUtils.getFile(requireContext(), fileUri)!!
                            val image = file.asRequestBody("image/*".toMediaTypeOrNull())
                            imagePart =
                                MultipartBody.Part.createFormData("file[0]", file.name, image)
                            imageSelected = true
                        }
                        binding.tvFileStatus.text = getString(R.string.text_photo_selected)
                        binding.tvFileStatus.setTextColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.color_green
                            )
                        )
                    }
                }.show()
            }

            btnFinish.clickWithDebounce(lifecycleScope) {
                if (imageSelected) {
                    lifecycleScope.launch {
                        confirmViewModel.uploadImage(imagePart)
                    }
                } else {
                    showSnackBar(btnFinish, getString(R.string.error_image_not_selected))
                }
            }
        }
    }

    private fun initObservers() {
        binding.apply {
            confirmViewModel.loadingFlow.onEach {
                progressBar.isVisible = it
            }.launchIn(lifecycleScope)

            confirmViewModel.errorFlow.onEach {
                it.localizedMessage?.let { msg -> showSnackBar(btnFinish, msg) }
                it.printStackTrace()
            }.launchIn(lifecycleScope)

            confirmViewModel.messageFlow.onEach {
                showSnackBar(btnFinish, it)
            }.launchIn(lifecycleScope)

            registerViewModel.loadingFlow.onEach {
                progressBar.isVisible = it
            }.launchIn(lifecycleScope)

            registerViewModel.errorFlow.onEach {
                it.localizedMessage?.let { msg -> showSnackBar(btnFinish, msg) }
                it.printStackTrace()
            }.launchIn(lifecycleScope)

            registerViewModel.messageFlow.onEach {
                showSnackBar(btnFinish, it)
            }.launchIn(lifecycleScope)

            confirmViewModel.uploadImageFlow.onEach {
                val image = it.data.first().url
                registerViewModel.newPayment(
                    NewPaymentRequestData(args.username, image)
                )
            }.launchIn(lifecycleScope)




            confirmViewModel.confirmDetailsFlow.onEach {
                val data = it.data
                tvDescription.text = getString(R.string.text_pay_money, data.price.toSumFormat)
                tvCardNumber.text = data.cardNumber
                tvCardName.text = data.cardHolder
                icCopy.clickWithDebounce(lifecycleScope) {
                    val manager =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText(
                        "CardNumber",
                        data.cardNumber.filter { c -> c.isDigit() })
                    manager.setPrimaryClip(clipData)
                }
            }.launchIn(lifecycleScope)

            registerViewModel.newPaymentSuccessFlow.onEach {
                localStorage.signedIn = true
                requireActivity().finish()
                requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
            }.launchIn(lifecycleScope)

        }
    }
}

