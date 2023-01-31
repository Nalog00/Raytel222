package uz.raytel.raytel.ui.confirm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.raytel.raytel.R
import uz.raytel.raytel.databinding.FragmentConfirmPaymentBinding

class PaymentConfirmFragment : Fragment(R.layout.fragment_confirm_payment) {
    private val binding by viewBinding(FragmentConfirmPaymentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initObservers()
    }

    private fun initListeners() {

    }

    private fun initObservers() {

    }
}
