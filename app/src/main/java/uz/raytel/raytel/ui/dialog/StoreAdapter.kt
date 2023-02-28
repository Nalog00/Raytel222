package uz.raytel.raytel.ui.dialog

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import uz.raytel.raytel.R
import uz.raytel.raytel.data.remote.store.Store
import uz.raytel.raytel.databinding.ItemShopBinding
import uz.raytel.raytel.utils.click
import uz.raytel.raytel.utils.setImageWithGlide

class StoreAdapter : Adapter<StoreAdapter.StoreViewHolder>() {

    var models = mutableListOf<Store>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class StoreViewHolder(private val binding: ItemShopBinding) : ViewHolder(binding.root) {
        fun bind(model: Store) {
            binding.apply {
                ivLogo.setImageWithGlide(ivLogo.context, model.image)
                tvShopName.text = model.name

                root.click {
                    onItemClick.invoke(model)
                }
            }
        }
    }

    override fun getItemCount() = models.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_shop, parent, false)
        val binding = ItemShopBinding.bind(v)
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(models[position])
    }

    fun addItems(items: List<Store>) {
        val p = models.size
        models.addAll(items)
        notifyItemRangeInserted(p, items.size)
    }

    private var onItemClick: (store: Store) -> Unit = {}
    fun setOnItemClickListener(block: (store: Store) -> Unit) {
        onItemClick = block
    }
}
