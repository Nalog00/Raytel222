package uz.raytel.raytel.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.paging.PagingResponse
import uz.raytel.raytel.data.remote.store.Store
import uz.raytel.raytel.domain.repository.Repository
import javax.inject.Inject

@HiltViewModel
class ShopViewModelImpl @Inject constructor(
    private val repository: Repository
) : ShopViewModel, ViewModel() {

    override val storesFlow = MutableSharedFlow<PagingResponse<Store>>()
    override val loadingFlow = MutableSharedFlow<Boolean>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()

    override suspend fun getStores(storeId: Int, page: Int, limit: Int) {
        repository.getStores(storeId, page, limit).onEach {
            when (it) {
                is ResultData.Loading -> {
                    loadingFlow.emit(true)
                }
                is ResultData.Success -> {
                    loadingFlow.emit(false)
                    storesFlow.emit(it.data)
                }
                is ResultData.Message -> {
                    loadingFlow.emit(false)
                    messageFlow.emit(it.message)
                }
                is ResultData.Error -> {
                    loadingFlow.emit(false)
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }
}
