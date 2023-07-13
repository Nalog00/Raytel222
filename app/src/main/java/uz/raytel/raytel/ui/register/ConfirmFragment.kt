package uz.raytel.raytel.ui.register

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.text.isDigitsOnly
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges
import uz.raytel.raytel.R
import uz.raytel.raytel.app.App
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.auth.ConfirmSmsRequestData
import uz.raytel.raytel.data.remote.auth.SendSmsData
import uz.raytel.raytel.databinding.FragmentConfirmBinding
import uz.raytel.raytel.ui.MainActivity
import uz.raytel.raytel.utils.*
import uz.raytel.raytel.utils.otp.OTPReceiveListener
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmFragment : Fragment(R.layout.fragment_confirm), TextWatcher,
    SmsVerificationReceiver.OTPReceiveListener {
    private val binding by viewBinding(FragmentConfirmBinding::bind)
    private val viewModel: RegisterViewModel by viewModels<RegisterViewModelImpl>()
    private val args: ConfirmFragmentArgs by navArgs()
    private lateinit var navController: NavController
    private var numTemp = ""


    @Inject
    lateinit var localStorage: LocalStorage


    private val editTextArray by lazy {
        arrayListOf(
            binding.etCodeOne,
            binding.etCodeTwo,
            binding.etCodeThree,
            binding.etCodeFour,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.initOTPListener(this)
        navController = findNavController()
        initTimer()
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.confirmSmsCode.onEach {
            localStorage.token = it.token
            viewModel.getMe()
        }.launchIn(lifecycleScope)

        viewModel.getMeFlow.onEach {
            if (it.name == null) {
                navController.navigate(
                    ConfirmFragmentDirections.actionConfirmFragmentToRegisterNameFragment(
                        args.phone
                    )
                )
            } else {
                localStorage.signedIn = true
                requireActivity().finish()
                requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        }.launchIn(lifecycleScope)

        viewModel.messageFlow.onEach {
            binding.progressBar.hide()
            showSnackBar(
                binding.btnCheck, it
            )
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            binding.progressBar.hide()
        }.launchIn(lifecycleScope)
    }

    private fun initTimer() {
        object : CountDownTimer(30000L, 1000L) {
            override fun onTick(p0: Long) {
                val seconds = (p0 * 0.001).toInt()
                if (view != null) {
                    binding.tvTimer.text = getString(R.string.after_30_seconds, seconds.toString())
                }
            }

            override fun onFinish() {
                if (view != null) {
                    binding.tvGetCodeAgain.text = getString(R.string.get_code_again)
                    binding.tvGetCodeAgain.isEnabled = true
                    binding.tvTimer.text = ""
                }
            }

        }.start()
    }

    private fun initListeners() {
        binding.apply {
            editTextArray.forEachIndexed { index, appCompatEditText ->
                appCompatEditText.addTextChangedListener(this@ConfirmFragment)

                appCompatEditText.setOnKeyListener { _, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                        if (index != 0) {
                            if (editTextArray.filter {
                                    it.text.toString().isNotEmpty()
                                }.size == 4) {
                                appCompatEditText.setText("")
                            } else {
                                editTextArray[index - 1].requestFocus()
                                editTextArray[index - 1].setSelection(editTextArray[index - 1].length())
                            }
                        }
                    }
                    false
                }
            }

            editTextArray.first().requestFocus()



            tvGetCodeAgain.clicks().debounce(200).onEach {
                binding.progressBar.show()
                viewModel.sendSmsCode(SendSmsData("998${args.phone}", localStorage.deviceId))
            }.launchIn(lifecycleScope)

            btnCheck.clicks().debounce(200).onEach {
                binding.progressBar.show()
                var code = ""
                binding.lCode.forEach { view ->
                    code += (view as EditText).text.toString().filter { str -> str.isDigit() }
                }
                viewModel.confirmSmsCode(ConfirmSmsRequestData("998${args.phone}", code.toInt()))
            }.launchIn(lifecycleScope)

        }

    }


    override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        numTemp = s.toString()
    }

    override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (editTextArray.filter { it.text.toString().isNotEmpty() }.size == 4) {
            lifecycleScope.launch {

                binding.progressBar.show()
                delay(400)
                binding.btnCheck.callOnClick()
            }

        }
    }

    override fun afterTextChanged(s: Editable) {
        (0 until editTextArray.size).forEach { i ->
            if (s === editTextArray[i].editableText) {
                if (s.isBlank()) {
                    return
                }
                if (s.length >= 2) {
                    val newTemp = s.toString().substring(s.length - 1, s.length)
                    if (newTemp != numTemp) {
                        editTextArray[i].setText(newTemp)
                    } else {
                        editTextArray[i].setText(s.toString().substring(0, s.length - 1))
                    }
                } else if (i != editTextArray.size - 1) {
                    editTextArray[i + 1].requestFocus()
                    editTextArray[i + 1].setSelection(editTextArray[i + 1].length())
                    return
                }
            }
        }


    }

    override fun onOTPReceived(otp: String) {
        if (otp.length == 4 && otp.isDigitsOnly()) {
            binding.etCodeOne.setText(otp[0].toString())
            binding.etCodeTwo.setText(otp[1].toString())
            binding.etCodeThree.setText(otp[2].toString())
            binding.etCodeFour.setText(otp[3].toString())

            binding.progressBar.show()
        }
    }

    override fun onOTPTimeOut() {
        Log.d("TTTT", "Time out ")
    }
}


