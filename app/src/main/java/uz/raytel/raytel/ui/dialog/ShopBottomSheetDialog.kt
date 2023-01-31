package uz.raytel.raytel.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.raytel.raytel.R
import uz.raytel.raytel.databinding.BottomSheetShopBinding

class ShopBottomSheetDialog : BottomSheetDialogFragment() {
    private val binding: BottomSheetShopBinding by viewBinding(BottomSheetShopBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_shop, container, false)
    }

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
