package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kh.sothun.darachhat.rupp.fe.ecommerce_app.R
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.MyOrderAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.OrderItemAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityMyOrdersBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.DialogOrderDetailsBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.Order
import java.text.SimpleDateFormat
import java.util.*

class MyOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyOrdersBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var orderAdapter: MyOrderAdapter
    private val orders = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupViews()
        loadOrders()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        orderAdapter = MyOrderAdapter { order ->
            showOrderDetailsDialog(order)
        }

        binding.ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyOrdersActivity)
            adapter = orderAdapter
        }

        // Empty state CTA
        binding.btnBrowse?.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    private fun loadOrders() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login to view orders", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        database.child("orders")
            .orderByChild("userId")
            .equalTo(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    orders.clear()
                    
                    for (orderSnapshot in snapshot.children) {
                        try {
                            val order = orderSnapshot.getValue(Order::class.java)
                            if (order != null) {
                                // Set the order ID from the key
                                val orderWithId = order.copy(id = orderSnapshot.key ?: "")
                                orders.add(orderWithId)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    // Sort by date (newest first)
                    orders.sortByDescending { it.orderDate }

                    binding.progressBar.visibility = View.GONE

                    if (orders.isEmpty()) {
                        binding.emptyView.visibility = View.VISIBLE
                        binding.ordersRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyView.visibility = View.GONE
                        binding.ordersRecyclerView.visibility = View.VISIBLE
                        orderAdapter.submitList(orders)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@MyOrdersActivity,
                        "Error loading orders: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showOrderDetailsDialog(order: Order) {
        val dialog = Dialog(this)
        val dialogBinding = DialogOrderDetailsBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        dialogBinding.apply {
            // Order Info
            txtDialogOrderId.text = "#${order.id.takeLast(8)}"
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            txtDialogOrderDate.text = dateFormat.format(Date(order.orderDate))
            txtDialogOrderStatus.text = order.status.uppercase()
            when (order.status.lowercase()) {
                "pending" -> {
                    txtDialogOrderStatus.setTextColor(ContextCompat.getColor(this@MyOrdersActivity, R.color.amber))
                    txtDialogOrderStatus.setBackgroundResource(R.drawable.status_pending_bg)
                }
                "processing" -> {
                    txtDialogOrderStatus.setTextColor(ContextCompat.getColor(this@MyOrdersActivity, R.color.blue))
                    txtDialogOrderStatus.setBackgroundResource(R.drawable.status_processing_bg)
                }
                "shipped" -> {
                    txtDialogOrderStatus.setTextColor(ContextCompat.getColor(this@MyOrdersActivity, R.color.purple))
                    txtDialogOrderStatus.setBackgroundResource(R.drawable.status_shipped_bg)
                }
                "delivered" -> {
                    txtDialogOrderStatus.setTextColor(ContextCompat.getColor(this@MyOrdersActivity, R.color.green))
                    txtDialogOrderStatus.setBackgroundResource(R.drawable.status_delivered_bg)
                }
                "cancelled" -> {
                    txtDialogOrderStatus.setTextColor(ContextCompat.getColor(this@MyOrdersActivity, R.color.red))
                    txtDialogOrderStatus.setBackgroundResource(R.drawable.status_cancelled_bg)
                }
                else -> {
                    txtDialogOrderStatus.setTextColor(ContextCompat.getColor(this@MyOrdersActivity, R.color.grey))
                    txtDialogOrderStatus.setBackgroundResource(R.drawable.status_unknown_bg)
                }
            }

            // Delivery Info
            txtDialogFullName.text = order.deliveryInfo.fullName
            txtDialogPhone.text = order.deliveryInfo.phone
            txtDialogAddress.text = "${order.deliveryInfo.address}\n${order.deliveryInfo.city}, ${order.deliveryInfo.postalCode}"
            txtDialogPaymentMethod.text = order.paymentMethod

            // Order Items
            val itemAdapter = OrderItemAdapter()
            recyclerOrderItems.layoutManager = LinearLayoutManager(this@MyOrdersActivity)
            recyclerOrderItems.adapter = itemAdapter
            itemAdapter.submitList(order.items)

            // Pricing
            txtDialogSubtotal.text = "$${String.format("%.2f", order.pricing.subtotal)}"
            txtDialogTax.text = "$${String.format("%.2f", order.pricing.tax)}"
            txtDialogDelivery.text = "$${String.format("%.2f", order.pricing.delivery)}"
            txtDialogTotal.text = "$${String.format("%.2f", order.pricing.total)}"

            btnClose.setOnClickListener { dialog.dismiss() }
        }

        dialog.show()
    }
}
