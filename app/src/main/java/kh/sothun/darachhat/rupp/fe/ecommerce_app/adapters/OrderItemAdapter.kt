package kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kh.sothun.darachhat.rupp.fe.ecommerce_app.R
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ItemOrderDetailBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.OrderItemDetail

class OrderItemAdapter : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    private var items = listOf<OrderItemDetail>()

    fun submitList(newItems: List<OrderItemDetail>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class OrderItemViewHolder(
        private val binding: ItemOrderDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderItemDetail) {
            binding.apply {
                txtItemTitle.text = item.title
                txtItemPrice.text = "$${String.format("%.2f", item.price * item.quantity)}"
                txtItemQuantity.text = "Qty: ${item.quantity}"
                
                if (item.size.isNotEmpty()) {
                    txtItemSize.text = "Size: ${item.size}"
                } else {
                    txtItemSize.text = ""
                }
                
                if (item.color.isNotEmpty()) {
                    txtItemColor.text = "Color: ${item.color}"
                } else {
                    txtItemColor.text = ""
                }

                // Load image
                if (item.thumbnail.isNotEmpty()) {
                    Glide.with(imgOrderItem.context)
                        .load(item.thumbnail)
                        .placeholder(R.drawable.grey_bg)
                        .error(R.drawable.grey_bg)
                        .into(imgOrderItem)
                } else {
                    imgOrderItem.setImageResource(R.drawable.grey_bg)
                }
            }
        }
    }
}
