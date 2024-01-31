package uz.raytel.raytel.ui.main

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.databinding.ItemPageBinding
import uz.raytel.raytel.utils.*

class ProductPagingAdapter(private val localStorage: LocalStorage) :
    PagingDataAdapter<Product, ProductPagingAdapter.ProductViewHolder>(MyDiffUtil) {

    inner class ProductViewHolder(private val binding: ItemPageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Product?) {
            data?.let {
                binding.apply {
                    onlineCount.invoke(tvOnline)


                    ivLogo.setImageWithGlide(ivLogo.context, data.store.image)


                    if (data.alert) {
                        Glide.with(binding.root.context).asBitmap().load(data.image)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(25)))
                            .into(ivImage)
                        binding.pbLoading.hide()
                    } else {
                        Glide.with(binding.root.context).load(data.image)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    if (binding.pbLoading.isVisible){
                                        binding.pbLoading.hide()
                                    }
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    if (binding.pbLoading.isVisible){
                                        binding.pbLoading.hide()
                                    }
                                    return false
                                }

                            }).into(ivImage)

                    }

                    if (isNewProduct(data.createdAt)) {
                        binding.ivNew.show()
                    } else {
                        binding.ivNew.hide()
                    }

                    tvShopName.text = data.store.name
                    tvShopName.isSelected = true
                    tvOnline.isSelected = true


                    btnLogin.click {
                        navigate.invoke()
                    }
                    tvTrialExpired.text = localStorage.lockScreenMessage

                    ivLogo.click {
                        storeClick.invoke(data.store.id)
                    }

                    tvShopName.click {
                        storeClick.invoke(data.store.id)
                    }

                    btnScreenshot.click {
                        onSaveButtonClick.invoke(data)
                    }

                    btnClose.click {
                        hideLockScreen()
                    }

                    if (data.alert) {
                        showLockScreen()
                    } else {
                        hideLockScreen()
                    }
                }
            }
        }

        private fun showLockScreen() {
            binding.apply {
                ivLogo.hide()
                tvShopName.hide()
                btnScreenshot.hide()
                lockView.show()


            }
        }

        private fun hideLockScreen() {
            binding.apply {
                lockView.hide()
                ivLogo.show()
                tvShopName.show()
                btnScreenshot.show()
            }
        }
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ItemPageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    object MyDiffUtil : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id && oldItem.createdAt == newItem.createdAt && oldItem.alert == newItem.alert && oldItem.number == newItem.number
        }
    }


    private var onlineCount: (tvCount: TextView) -> Unit = {}
    fun setOnlineCount(block: (TextView) -> Unit) {
        onlineCount = block
    }

    private var storeClick: (id: Int) -> Unit = {}
    fun setOnStoreClickListener(block: (id: Int) -> Unit) {
        storeClick = block
    }

    private var navigate: () -> Unit = {}
    fun authNavigation(block: () -> Unit) {
        navigate = block
    }

    private var onSaveButtonClick: (product: Product) -> Unit = {}
    fun setOnSaveButtonClickListener(block: (product: Product) -> Unit) {
        onSaveButtonClick = block
    }
}