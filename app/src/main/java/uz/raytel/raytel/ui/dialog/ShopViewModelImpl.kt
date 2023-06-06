package uz.raytel.raytel.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.paging.PagingResponse
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.data.remote.store.Store
import uz.raytel.raytel.domain.repository.MainRepository
import uz.raytel.raytel.domain.repository.Repository
import javax.inject.Inject

@HiltViewModel
class ShopViewModelImpl @Inject constructor(
    private val repository: Repository, private val mainRepository: MainRepository
) : ShopViewModel, ViewModel() {
    override val getStoresFlow = MutableStateFlow(-1)

    override val storesFlow = getStoresFlow.map(::newPager).flatMapLatest { pager -> pager.flow }
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty()).cachedIn(viewModelScope)


    override val loadingFlow = MutableSharedFlow<Boolean>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()
    override suspend fun getStores(storeId: Int) {
        getStoresFlow.tryEmit(storeId)
    }


    private var newPagingSource: PagingSource<*, *>? = null
    private fun newPager(id: Int): Pager<Int, Store> {
        return Pager(PagingConfig(3, enablePlaceholders = false)) {
            newPagingSource?.invalidate()
            mainRepository.getStores(id).also { newPagingSource = it }
        }
    }
}
