package uz.raytel.raytel.ui.dialog

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.raytel.raytel.data.remote.paging.PagingResponse
import uz.raytel.raytel.data.remote.store.Store

interface ShopViewModel {


    val getStoresFlow: Flow<Int>
    val storesFlow: Flow<PagingData<Store>>
    val loadingFlow: Flow<Boolean>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>

    suspend fun getStores(storeId: Int)
}
