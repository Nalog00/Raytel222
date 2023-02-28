package uz.raytel.raytel.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.raytel.raytel.R
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.auth.SignInPhone
import uz.raytel.raytel.databinding.FragmentLoginBinding
import uz.raytel.raytel.utils.clickWithDebounce
import uz.raytel.raytel.utils.isValidPhoneNumber
import uz.raytel.raytel.utils.showSnackBar
import uz.texnopos.elektrolife.core.MaskWatcher
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val binding by viewBinding(FragmentLoginBinding::bind)
    private val viewModel: LoginViewModel by viewModels<LoginViewModelImpl>()
    private lateinit var navController: NavController

    @Inject
    lateinit var localStorage: LocalStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.apply {
            etPhone.addTextChangedListener(MaskWatcher.phoneNumber())
            etPhone.addTextChangedListener {
                tilPhone.isErrorEnabled = false
            }

            btnLogin.clickWithDebounce(lifecycleScope) {
                val phone = etPhone.text.toString()
                val password = etPassword.text.toString()

                if (phone.isValidPhoneNumber && password.isNotEmpty()) {
                    lifecycleScope.launchWhenCreated {
                        viewModel.signInPhone(
                            SignInPhone(
                                "+998${phone.filter { it.isDigit() }}",
                                password,
                                localStorage.deviceId
                            )
                        )
                    }
                } else {
                    if (!phone.isValidPhoneNumber) {
                        tilPhone.error = getString(R.string.error_invalid_phone_number)
                    }
                    if (password.isEmpty()) {
                        tilPassword.error = getString(R.string.error_required_field)
                    }
                }
            }

            tvRegister.clickWithDebounce(lifecycleScope) {
                navController.navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun initObservers() {
        binding.apply {
            viewModel.loadingFlow.onEach {
                progressBar.isVisible = it
            }.launchIn(lifecycleScope)

            viewModel.errorFlow.onEach {
                it.localizedMessage?.let { msg -> showSnackBar(ivLogo, msg) }
                it.printStackTrace()
            }.launchIn(lifecycleScope)

            viewModel.messageFlow.onEach {
                showSnackBar(ivLogo, it)
            }.launchIn(lifecycleScope)

            viewModel.signInPhoneFlow.onEach {
                localStorage.token = it.token
                localStorage.signedIn = true
                findNavController().popBackStack(R.id.mainFragment, false)
            }.launchIn(lifecycleScope)
        }
    }
}
