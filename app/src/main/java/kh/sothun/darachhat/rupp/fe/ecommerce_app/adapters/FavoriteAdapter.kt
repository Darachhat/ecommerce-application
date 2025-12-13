package kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import kh.sothun.darachhat.rupp.fe.ecommerce_app.activities.DetailActivity
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ViewholderFavoriteBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.ItemModel

class FavoriteAdapter(
    private var items: MutableList<ItemModel>,
    private val onRemoveFavorite: (ItemModel, Int) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderFavoriteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        holder.binding.apply {
            titleTxt.text = item.title
            priceTxt.text = "$${item.price}"
            ratingTxt.text = item.rating.toString()

            val requestOptions = RequestOptions().transform(CenterCrop())
            Glide.with(holder.itemView.context)
                .load(item.picUrl.firstOrNull())
                .apply(requestOptions)
                .into(pic)

            // Click to view details
            root.setOnClickListener {
                val intent = Intent(holder.itemView.context, DetailActivity::class.java).apply {
                    putExtra("object", item)
                }
                holder.itemView.context.startActivity(intent)
            }

            // Remove from favorites
            removeFavoriteBtn.setOnClickListener {
                val position = holder.bindingAdapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    onRemoveFavorite(item, position)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: MutableList<ItemModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position in items.indices) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
    }
}
