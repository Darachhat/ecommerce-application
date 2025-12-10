package kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import kh.sothun.darachhat.rupp.fe.ecommerce_app.activities.DetailActivity
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ViewholderPopularBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.ItemModel

class PopularAdapter(
    private val items: MutableList<ItemModel>
) : RecyclerView.Adapter<PopularAdapter.Viewholder>() {

    fun updateData(newData: List<ItemModel>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Viewholder {
        val binding = ViewholderPopularBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            titleTxt.text = item.title
            priceTxt.text = "$${item.price}"
            ratingTxt.text = item.rating.toString()

            Glide.with(holder.itemView.context)
                .load(item.picUrl.firstOrNull())
                .apply(RequestOptions().transform(CenterCrop()))
                .into(pic)

            root.setOnClickListener {
                val intent = Intent(holder.itemView.context,
                    DetailActivity::class.java)
                intent.putExtra("object", item)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class Viewholder(val binding: ViewholderPopularBinding) :
        RecyclerView.ViewHolder(binding.root)
}
