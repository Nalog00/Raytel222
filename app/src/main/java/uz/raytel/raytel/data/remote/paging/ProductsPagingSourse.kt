package uz.raytel.raytel.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.ApiService
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.di.utils.UnauthorisedException
import uz.raytel.raytel.utils.getErrorMessage
import uz.raytel.raytel.utils.log
import javax.inject.Inject

class ProductsPagingSourse @AssistedInject constructor(
    private val api: ApiService,
    @Assisted("isSignedUp") private val isSignedUp: Boolean,
    @Assisted("id") private val id: Int
) : PagingSource<Int, Product>() {
    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: 1
        val response = if (id != -1) {
            api.getProducts(page, id, 3)
        } else {
            if (isSignedUp.not()) api.getRandomProducts(page) else api.getRandomProductsWithoutLimit(
                page
            )
        }

        return if (response.isSuccessful) {
            val products = checkNotNull(response.body()).data
            val nextKey =
                if (page == checkNotNull(response.body()).pagination.totalPages) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(products, prevKey, nextKey)
        } else {
            if (response.code() == 401) {
                LoadResult.Error(UnauthorisedException())
            } else {
                LoadResult.Error(HttpException(response))
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("isSignedUp") isSignedUp: Boolean, @Assisted("id") id: Int
        ): ProductsPagingSourse
    }
}