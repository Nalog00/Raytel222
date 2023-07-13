package uz.raytel.raytel.domain.repository.impl

import androidx.paging.PagingSource
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.paging.ProductsPagingSourse
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.data.remote.store.ShopPagingSourse
import uz.raytel.raytel.di.utils.UnauthorisedException
import uz.raytel.raytel.domain.repository.MainRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MainRepositoryImpl @Inject constructor(
    private val pagingSourceFactory: ProductsPagingSourse.Factory,
    private val shopPagingSourceFactory: ShopPagingSourse.Factory,
    private val localStorage: LocalStorage
) : MainRepository {

    override fun getProducts(id: Int): PagingSource<Int, Product> {
        return try {
            pagingSourceFactory.create(localStorage.signedIn, id)
        } catch (e: UnauthorisedException) {
            throw UnauthorisedException()
        } catch (e: Exception){
            throw e
        }
    }

    override fun getStores(storeId: Int): ShopPagingSourse {
        return shopPagingSourceFactory.create(storeId)
    }
}