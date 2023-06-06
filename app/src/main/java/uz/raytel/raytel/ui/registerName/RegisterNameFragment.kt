package uz.raytel.raytel.ui.registerName

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import uz.raytel.raytel.R
import uz.raytel.raytel.databinding.FragmentRegisterNameBinding

class RegisterNameFragment : Fragment(R.layout.fragment_register_name) {
    private val binding by viewBinding(FragmentRegisterNameBinding::bind)
    private val args: RegisterNameFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initListeners()
    }

    private fun initListeners() {
        binding.btnLogin.clicks().debounce(200).onEach {
            val name = binding.etName.text.toString()
            if (name.isNotEmpty()) {
                findNavController().navigate(
                    RegisterNameFragmentDirections.actionRegisterNameFragmentToPaymentConfirmFragment(
                        args.phone, name
                    )
                )
            } else {
                binding.etName.error = getString(R.string.error_required_field)
            }
        }.launchIn(lifecycleScope)


        binding.etName.addTextChangedListener {
            binding.etName.error = null
        }
    }
}