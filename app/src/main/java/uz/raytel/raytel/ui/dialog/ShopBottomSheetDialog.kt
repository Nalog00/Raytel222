package uz.raytel.raytel.ui.dialog

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.raytel.raytel.R
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.store.Store
import uz.raytel.raytel.databinding.BottomSheetShopBinding
import uz.raytel.raytel.utils.bottomShopListCloseFlow
import uz.raytel.raytel.utils.showSnackBar
import javax.inject.Inject

@AndroidEntryPoint
class ShopBottomSheetDialog : BottomSheetDialogFragment() {
    private val binding: BottomSheetShopBinding by viewBinding(BottomSheetShopBinding::bind)
    private val viewModel: ShopViewModel by viewModels<ShopViewModelImpl>()
    private var page = 1
    private var lastPage = 0
    private val storesList = mutableListOf<Store>()
    private var _adapter: StoreAdapter? = null
    private val adapter: StoreAdapter get() = _adapter!!

    @Inject
    lateinit var localStorage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_shop, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { layout ->
                val behaviour = BottomSheetBehavior.from(layout)
                setupFullHeight(layout)
                behaviour.peekHeight = Resources.getSystem().displayMetrics.heightPixels / 3
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initObservers()
    }

    private fun initListeners() {
        _adapter = StoreAdapter()

        binding.apply {
            lifecycleScope.launchWhenCreated {
                viewModel.getStores(localStorage.selectedStoreId, page, 15)
            }

            val layoutManager = rvShop.layoutManager as GridLayoutManager
            rvShop.adapter = adapter
            rvShop.addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (adapter.models.isNotEmpty() && page < lastPage &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1
                    ) {
                        page++
                        lifecycleScope.launchWhenCreated {
                            viewModel.getStores(localStorage.selectedStoreId, page, 50)
                        }
                    }
                }
            })

            adapter.setOnItemClickListener {
                localStorage.selectedStoreId = it.id
                lifecycleScope.launchWhenCreated {
                    bottomShopListCloseFlow.emit(it.id)
                }
                dismiss()
            }
        }
    }

    private fun initObservers() {
        viewModel.loadingFlow.onEach {
            binding.progressBar.isVisible = it
        }.launchIn(lifecycleScope)

        viewModel.messageFlow.onEach {
            showSnackBar(binding.rvShop, it)
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            it.localizedMessage?.let { message -> showSnackBar(binding.rvShop, message) }
        }.launchIn(lifecycleScope)

        viewModel.storesFlow.onEach {
            lastPage = it.pagination.totalPages
            if (it.pagination.currentPage == 1) {
                adapter.models.clear()
                storesList.clear()
            }
            storesList.addAll(it.data)
            adapter.addItems(it.data)
        }.launchIn(lifecycleScope)
    }

    override fun onDetach() {
        super.onDetach()
        _adapter = null
    }
}
