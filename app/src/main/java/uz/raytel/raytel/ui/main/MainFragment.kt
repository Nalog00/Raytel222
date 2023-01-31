package uz.raytel.raytel.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.raytel.raytel.R
import uz.raytel.raytel.databinding.FragmentMainBinding

class MainFragment: Fragment(R.layout.fragment_main) {
    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initObservers()
    }

    private fun initListener() {

    }

    private fun initObservers() {

    }
}
