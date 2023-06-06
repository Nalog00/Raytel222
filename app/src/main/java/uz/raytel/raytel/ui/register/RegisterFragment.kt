package uz.raytel.raytel.ui.register

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges
import uz.raytel.raytel.R
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.auth.SendSmsData
import uz.raytel.raytel.databinding.FragmentRegisterBinding
import uz.raytel.raytel.utils.*
import uz.raytel.raytel.utils.MaskWatcher
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val binding by viewBinding(FragmentRegisterBinding::bind)
    private val viewModel: RegisterViewModel by viewModels<RegisterViewModelImpl>()
    private lateinit var navController: NavController

    @Inject
    lateinit var localStorage: LocalStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.sendSmsCodeFlow.onEach {
            binding.progressBar.hide()
            showSnackBar(
                binding.btnGetCode, "Kod jiberildi!"
            )
            navController.navigate(
                RegisterFragmentDirections.actionRegisterFragmentToConfirmFragment(
                    binding.etPhoneNumber.text.toString().filter { it.isDigit() })
            )
        }.launchIn(lifecycleScope)


        viewModel.messageFlow.onEach {
            binding.progressBar.hide()
            binding.btnGetCode.text = getString(R.string.text_resend)
            binding.btnGetCode.isEnabled = true
            showSnackBar(binding.btnGetCode, it)
        }.launchIn(lifecycleScope)
    }

    private fun initListeners() {
        binding.apply {
            etPhoneNumber.addTextChangedListener(MaskWatcher.phoneNumber())

            etPhoneNumber.addTextChangedListener {
                etPhoneNumber.error = null
                if (it.toString().isNotEmpty()) {
                    prefixNumber.show()
                } else {
                    prefixNumber.hide()
                }
            }

            btnGetCode.clicks().debounce(200).onEach {
                binding.progressBar.show()
                if (binding.etPhoneNumber.text.toString().isValidPhoneNumber) {
                    viewModel.sendSmsCode(
                        SendSmsData(
                            "998${binding.etPhoneNumber.text.toString().filter { it.isDigit() }}",
                            localStorage.deviceId
                        )
                    )
                    startSmsListener()
                } else {
                    binding.etPhoneNumber.error = getString(R.string.error_invalid_phone_number)
                    binding.progressBar.hide()
                }
                hideKeyboard()
            }.launchIn(lifecycleScope)
        }
    }

    private fun startSmsListener() {
        val client = SmsRetriever.getClient(requireContext())
        client.startSmsRetriever()
    }
}


