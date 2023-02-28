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
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import uz.raytel.raytel.R
import uz.raytel.raytel.databinding.FragmentRegisterBinding
import uz.raytel.raytel.utils.*
import uz.texnopos.elektrolife.core.MaskWatcher
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val binding by viewBinding(FragmentRegisterBinding::bind)
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        auth = Firebase.auth
        auth.setLanguageCode("uz")

        initListeners()
    }

    private fun initListeners() {
        binding.apply {
            etPhone.addTextChangedListener(MaskWatcher.phoneNumber())
            etPhone.addTextChangedListener {
                tilPhone.isErrorEnabled = false
            }
            etPinCode.addTextChangedListener {
                val code = it.toString()
                btnContinue.isEnabled = code.length == 6
            }

            tvSend.clickWithDebounce(lifecycleScope) {
                otpSend()
            }

            btnContinue.clickWithDebounce(lifecycleScope) {
                val code = etPinCode.text.toString().filter { it.isDigit() }
                if (verificationId.isEmpty()) {
                    showSnackBar(
                        btnContinue,
                        getString(R.string.error_verification_code_not_sent_yet)
                    )
                    return@clickWithDebounce
                }
                val credential = PhoneAuthProvider.getCredential(verificationId, code)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            navController.navigate(
                                RegisterFragmentDirections.actionRegisterFragmentToPaymentConfirmFragment(
                                    etPhone.text.toString().filter { it.isDigit() }
                                )
                            )
                        } else {
                            showSnackBar(
                                btnContinue,
                                getString(R.string.error_invalid_confirm_code)
                            )
                        }
                    }
            }

            tvSignIn.clickWithDebounce(lifecycleScope) {
                navController.navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun otpSend() {
        binding.progressBar.show()
        binding.tvSend.isEnabled = false

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressBar.hide()
                binding.tvSend.text = getString(R.string.text_resend)
                binding.tvSend.isEnabled = true
                e.localizedMessage?.let { showSnackBar(binding.tvSend, it) }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                binding.progressBar.hide()
                binding.tvSend.text = getString(R.string.text_resend)
                binding.tvSend.isEnabled = true
                showSnackBar(binding.tvSend, "Kod jiberildi!")
                this@RegisterFragment.verificationId = verificationId
            }
        }

        if (binding.etPhone.text.toString().isValidPhoneNumber) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+998${binding.etPhone.text.toString().filter { it.isDigit() }}")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } else {
            binding.tilPhone.error = getString(R.string.error_invalid_phone_number)
        }
    }
}
