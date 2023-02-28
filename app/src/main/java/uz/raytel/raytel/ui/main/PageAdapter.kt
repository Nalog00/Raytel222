package uz.raytel.raytel.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import uz.raytel.raytel.R
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.databinding.ItemPageBinding
import uz.raytel.raytel.utils.click
import uz.raytel.raytel.utils.hide
import uz.raytel.raytel.utils.setImageWithGlide
import uz.raytel.raytel.utils.show

class PageAdapter(context: Context) : Adapter<PageAdapter.PageViewHolder>() {

    var localStorage: LocalStorage

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MyEntryPoint {
        fun getLocalStorage(): LocalStorage
    }

    init {
        val myEntryPoint = EntryPointAccessors.fromApplication(context, MyEntryPoint::class.java)
        localStorage = myEntryPoint.getLocalStorage()
    }

    var models = mutableListOf<Product>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class PageViewHolder(private val binding: ItemPageBinding) : ViewHolder(binding.root) {
        fun bind(model: Product) {
            binding.apply {
                onlineCount.invoke(tvOnline)

                ivImage.setImageWithGlide(ivImage.context, model.image)
                ivLogo.setImageWithGlide(ivLogo.context, model.store.image)

                tvShopName.text = model.store.name

                ivLogo.click {
                    storeClick.invoke(model.store.id)
                }

                tvShopName.click {
                    storeClick.invoke(model.store.id)
                }

                btnScreenshot.click {
                    onSaveButtonClick.invoke(model)
                }

                btnClose.click {
                    hideLockScreen()
                }

                val position = absoluteAdapterPosition + 1
                if (position >= 15 && position % 3 == 0 && !localStorage.signedIn) {
                    showLockScreen()
                } else {
                    hideLockScreen()
                }
            }
        }

        private fun showLockScreen() {
            binding.apply {
                // todo: blur bg

                tvNew.hide()
                ivLogo.hide()
                tvShopName.hide()
                btnScreenshot.hide()
                lockView.show()

                btnRegister.click {
                    navigate.invoke("register")
                }

                tvLogin.click {
                    navigate.invoke("login")
                }
            }
        }

        private fun hideLockScreen() {
            binding.apply {
                // todo: unblur bg

                lockView.hide()
                tvNew.show()
                ivLogo.show()
                tvShopName.show()
            }
        }
    }

    override fun getItemCount() = models.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        val binding = ItemPageBinding.bind(v)
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(models[position])
    }

    fun addItems(items: List<Product>) {
        val p = models.size
        models.addAll(items)
        notifyItemRangeInserted(p, items.size)
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

    private var thisItem: (item: Product) -> Unit = {}
    fun getCurrentItem(block: (item: Product) -> Unit) {
        thisItem = block
    }

    private var disableScroll: (Boolean) -> Unit = {}
    fun setDisableScrollListener(block: (Boolean) -> Unit) {
        disableScroll = block
    }

    private var onSaveButtonClick: (product: Product) -> Unit = {}
    fun setOnSaveButtonClickListener(block: (product: Product) -> Unit) {
        onSaveButtonClick = block
    }
}
