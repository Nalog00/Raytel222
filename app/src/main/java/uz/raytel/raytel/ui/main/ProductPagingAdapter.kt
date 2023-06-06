package uz.raytel.raytel.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import uz.raytel.raytel.R
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.databinding.ItemPageBinding
import uz.raytel.raytel.utils.*

class ProductPagingAdapter() :
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
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(25))).into(ivImage)
                    } else {
                        ivImage.setImageWithGlide(binding.root.context, data.image)
                    }

                    if (isNewProduct(data.createdAt)) {
                        binding.ivNew.show()
                    }

                    tvShopName.text = data.store.name
                    tvShopName.isSelected = true
                    tvOnline.isSelected = true


                    tvExit.click {
                        navigate.invoke("exit")
                    }

                    btnLogin.click {
                        navigate.invoke("login")
                    }

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

                ivNew.hide()
                ivLogo.hide()
                tvShopName.hide()
                btnScreenshot.hide()
                lockView.show()


            }
        }

        private fun hideLockScreen() {
            binding.apply {
                lockView.hide()
                ivNew.show()
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

    private var navigate: (String) -> Unit = {}
    fun authNavigation(block: (direction: String) -> Unit) {
        navigate = block
    }

    private var onSaveButtonClick: (product: Product) -> Unit = {}
    fun setOnSaveButtonClickListener(block: (product: Product) -> Unit) {
        onSaveButtonClick = block
    }
}