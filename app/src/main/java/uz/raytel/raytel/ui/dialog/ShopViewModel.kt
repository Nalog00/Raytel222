package uz.raytel.raytel.ui.dialog

import kotlinx.coroutines.flow.Flow
import uz.raytel.raytel.data.remote.paging.PagingResponse
import uz.raytel.raytel.data.remote.store.Store

interface ShopViewModel {

    val storesFlow: Flow<PagingResponse<Store>>
    val loadingFlow: Flow<Boolean>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>

    suspend fun getStores(storeId: Int, page: Int, limit: Int)
}
