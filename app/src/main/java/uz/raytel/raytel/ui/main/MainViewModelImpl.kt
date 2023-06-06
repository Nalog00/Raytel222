package uz.raytel.raytel.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.GenericResponse
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.paging.PagingResponse
import uz.raytel.raytel.data.remote.paging.ProductsPagingSourse
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.domain.repository.MainRepository
import uz.raytel.raytel.domain.repository.Repository
import javax.inject.Inject

@HiltViewModel
class MainViewModelImpl @Inject constructor(
    private val mainRepository: MainRepository
) : MainViewModel, ViewModel() {


    private var newPagingSource: PagingSource<*, *>? = null


    override val getRandomProductsFlow = MutableStateFlow(-1)

    override val randomProductsFlow =
        getRandomProductsFlow.map(::newPager).flatMapLatest { pager -> pager.flow }
            .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())
            .cachedIn(viewModelScope)


    private fun newPager(id: Int): Pager<Int, Product> {
        return Pager(PagingConfig(3, enablePlaceholders = false)) {
            newPagingSource?.invalidate()
            mainRepository.getProducts(id).also { newPagingSource = it }
        }
    }


    override val productsFlow = MutableSharedFlow<PagingResponse<Product>>()
    override val loadingFlow = MutableSharedFlow<Boolean>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()


    override fun getRandomProducts() {
        getRandomProductsFlow.tryEmit(-1)
    }


    override suspend fun getProducts(storeId: Int) {
        getRandomProductsFlow.tryEmit(storeId)
    }
}
