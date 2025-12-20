package kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ItemMyOrderBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.Order
import java.text.SimpleDateFormat
import java.util.*

class MyOrderAdapter(
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<MyOrderAdapter.OrderViewHolder>() {

    private var orders = listOf<Order>()

    fun submitList(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemMyOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount() = orders.size

    inner class OrderViewHolder(
        private val binding: ItemMyOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                // Order ID - show last 8 characters
                txtOrderId.text = "Order #${order.id.takeLast(8)}"

                // Order date
                val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                txtOrderDate.text = dateFormat.format(Date(order.orderDate))

                // Total amount
                txtTotalAmount.text = "$${String.format("%.2f", order.pricing.total)}"

                // Items count
                val itemCount = order.items.sumOf { it.quantity }
                txtItemsCount.text = "$itemCount item${if (itemCount != 1) "s" else ""}"

                // Delivery address
                txtDeliveryAddress.text = "${order.deliveryInfo.address}, ${order.deliveryInfo.city}, ${order.deliveryInfo.postalCode}"

                // Payment method
                txtPaymentMethod.text = order.paymentMethod

                // Order status styled via resources
                txtOrderStatus.text = order.status.uppercase()
                styleStatusChip(binding.root.context, order.status.lowercase())

                // Click listener
                root.setOnClickListener { onOrderClick(order) }
                btnViewDetails.setOnClickListener { onOrderClick(order) }
            }
        }
    }

    private fun ItemMyOrderBinding.styleStatusChip(context: Context, status: String) {
        when (status) {
            "pending" -> {
                txtOrderStatus.setTextColor(ContextCompat.getColor(context, kh.sothun.darachhat.rupp.fe.ecommerce_app.R.color.amber))
                txtOrderStatus.setBackgroundResource(kh.sothun.darachhat.rupp.fe.ecommerce_app.R.drawable.status_pending_bg)
            }
            "processing" -> {
                txtOrderStatus.setTextColor(ContextCompat.getColor(context, kh.sothun.darachhat.rupp.fe.ecommerce_app.R.color.blue))
                txtOrderStatus.setBackgroundResource(kh.sothun.darachhat.rupp.fe.ecommerce_app.R.drawable.status_processing_bg)
            }
            "shipped" -> {
                txtOrderStatus.setTextColor(ContextCompat.getColor(context, kh.sothun.darachhat.rupp.fe.ecommerce_app.R.color.purple))
                txtOrderStatus.setBackgroundResource(kh.sothun.darachhat.rupp.fe.ecommerce_app.R.drawable.status_shipped_bg)
            }
            "delivered" -> {
                txtOrderStatus.setTextColor(ContextCompat.getColor(context, kh.sothun.darachhat.rupp.fe.ecommerce_app.R.color.green))
                txtOrderStatus.setBackgroundResource(kh.sothun.darachhat.rupp.fe.ecommerce_app.R.drawable.status_delivered_bg)
            }
            "cancelled" -> {
                txtOrderStatus.setTextColor(ContextCompat.getColor(context, kh.sothun.darachhat.rupp.fe.ecommerce_app.R.color.red))
                txtOrderStatus.setBackgroundResource(kh.sothun.darachhat.rupp.fe.ecommerce_app.R.drawable.status_cancelled_bg)
            }
            else -> {
                txtOrderStatus.setTextColor(ContextCompat.getColor(context, kh.sothun.darachhat.rupp.fe.ecommerce_app.R.color.grey))
                txtOrderStatus.setBackgroundResource(kh.sothun.darachhat.rupp.fe.ecommerce_app.R.drawable.status_unknown_bg)
            }
        }
    }
}
