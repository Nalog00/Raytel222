package uz.raytel.raytel.ui.confirm

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.robertlevonyan.components.picker.ItemModel
import com.robertlevonyan.components.picker.ItemType
import com.robertlevonyan.components.picker.PickerDialog
import com.robertlevonyan.components.picker.pickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import uz.raytel.raytel.R
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.auth.SignUpPhone
import uz.raytel.raytel.databinding.FragmentConfirmPaymentBinding
import uz.raytel.raytel.ui.register.RegisterViewModel
import uz.raytel.raytel.ui.register.RegisterViewModelImpl
import uz.raytel.raytel.utils.*
import java.io.File
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
            etUsername.addTextChangedListener {
                tilUsername.isErrorEnabled = false
            }
            etPassword.addTextChangedListener {
                tilPassword.isErrorEnabled = false
            }

            btnUpload.clickWithDebounce(lifecycleScope) {
                pickerDialog {
                    setTitle("Súwretti saylań")
                    setListType(PickerDialog.ListType.TYPE_LIST)
                    setItems(setOf(ItemModel(ItemType.ITEM_GALLERY)))
                }.setPickerCloseListener { _, uris ->
                    val fileUri = uris.first()
                    val file = FileUtils.getFile(requireContext(), fileUri)!!
                    val image = file.asRequestBody("image/*".toMediaTypeOrNull())

                    imagePart =
                        MultipartBody.Part.createFormData("file[0]", file.name, image)
                    imageSelected = true
                    binding.tvFileStatus.text = getString(R.string.text_photo_selected)
                    binding.tvFileStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_green
                        )
                    )
                }.show()
            }

            btnFinish.clickWithDebounce(lifecycleScope) {
                val name = etUsername.text.toString()
                val password = etPassword.text.toString()

                if (name.isNotEmpty() && password.isNotEmpty() && imageSelected) {
                    lifecycleScope.launchWhenCreated {
                        confirmViewModel.uploadImage(imagePart)
                    }
                } else {
                    if (name.isEmpty()) {
                        tilUsername.error = getString(R.string.error_required_field)
                    }
                    if (password.isEmpty()) {
                        tilPassword.error = getString(R.string.error_required_field)
                    }
                    if (!imageSelected) {
                        showSnackBar(btnFinish, getString(R.string.error_image_not_selected))
                    }
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
                val phone = "+998${args.phone}"
                val name = etUsername.text.toString()
                val password = etPassword.text.toString()
                val image = it.data.first().url
                registerViewModel.signUp(
                    SignUpPhone(name, phone, localStorage.deviceId, image, password)
                )
            }.launchIn(lifecycleScope)

            confirmViewModel.confirmDetailsFlow.onEach {
                val data = it.data
                tvDescription.text = data.description
                tvTitle.text = data.title
                tvPrice.text = getString(R.string.text_cost, data.price.toSumFormat)
                tvBankDetails.text =
                    getString(R.string.text_bank_details, data.cardNumber, data.cardHolder)
                tvEndText.text = data.endText
                tvPhone.text = data.phone.toPhoneNumber
                tvBankDetails.clickWithDebounce(lifecycleScope) {
                    val manager =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText(
                        "CardNumber",
                        data.cardNumber.filter { c -> c.isDigit() }
                    )
                    manager.setPrimaryClip(clipData)
                }
            }.launchIn(lifecycleScope)

            registerViewModel.signUpFlow.onEach {
                localStorage.token = it.token
                localStorage.signedIn = true
                findNavController().popBackStack(R.id.mainFragment, false)
            }.launchIn(lifecycleScope)
        }
    }

    private val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK && data != null) {
                val fileUri = data.data!!
                val file = File(fileUri.path!!)
                val image = file.asRequestBody("image/*".toMediaTypeOrNull())

                imagePart =
                    MultipartBody.Part.createFormData("file[0]", file.name, image)
                imageSelected = true
                binding.tvFileStatus.text = getString(R.string.text_photo_selected)
                binding.tvFileStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_green
                    )
                )
            }
        }
}
