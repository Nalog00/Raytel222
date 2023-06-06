package uz.raytel.raytel.data.remote.store

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.ApiService
import uz.raytel.raytel.data.remote.product.Product
import javax.inject.Inject

class ShopPagingSourse @AssistedInject constructor(
    private val api: ApiService, @Assisted("storeId") private val storeId: Int
) : PagingSource<Int, Store>() {
    override fun getRefreshKey(state: PagingState<Int, Store>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Store> {
        val page = params.key ?: 1
        val response = api.getStores(storeId, page, 30)

        return if (response.isSuccessful) {
            val shops = checkNotNull(response.body()).data
            val nextKey =
                if (page == checkNotNull(response.body()).pagination.totalPages) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(shops, prevKey, nextKey)
        } else {
            LoadResult.Error(HttpException(response))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("storeId") storeId: Int
        ): ShopPagingSourse
    }
}