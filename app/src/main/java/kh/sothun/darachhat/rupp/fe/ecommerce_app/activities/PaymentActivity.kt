package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kh.sothun.darachhat.rupp.fe.ecommerce_app.R
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityPaymentBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.helpers.ManagmentCart

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var managmentCart: ManagmentCart
    private lateinit var auth: FirebaseAuth
    private var subtotal: Double = 0.0
    private var tax: Double = 0.0
    private var delivery: Double = 0.0
    private var total: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        auth = FirebaseAuth.getInstance()
        
        // Check if user is logged in, if not redirect to login
        if (auth.currentUser == null) {
            Toast.makeText(
                this,
                "Please login to complete your order",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        // Get order summary from intent
        subtotal = intent.getDoubleExtra("subtotal", 0.0)
        tax = intent.getDoubleExtra("tax", 0.0)
        delivery = intent.getDoubleExtra("delivery", 0.0)
        total = intent.getDoubleExtra("total", 0.0)

        setupViews()
        displayOrderSummary()
    }

    private fun setupViews() {
        binding.apply {
            backBtn.setOnClickListener { finish() }

            // Payment method selection
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioCreditCard -> {
                        cardDetailsLayout.visibility = android.view.View.VISIBLE
                    }
                    else -> {
                        cardDetailsLayout.visibility = android.view.View.GONE
                    }
                }
            }

            // Place order button
            placeOrderBtn.setOnClickListener {
                if (validateInputs()) {
                    processPayment()
                }
            }
        }
    }

    private fun displayOrderSummary() {
        binding.apply {
            summarySubtotalTxt.text = "$${String.format("%.2f", subtotal)}"
            summaryTaxTxt.text = "$${String.format("%.2f", tax)}"
            summaryDeliveryTxt.text = "$${String.format("%.2f", delivery)}"
            summaryTotalTxt.text = "$${String.format("%.2f", total)}"
        }
    }

    private fun validateInputs(): Boolean {
        binding.apply {
            // Validate full name
            val fullName = edtFullName.text.toString().trim()
            if (fullName.isEmpty()) {
                edtFullName.error = "Full name is required"
                edtFullName.requestFocus()
                return false
            }

            // Validate phone
            val phone = edtPhone.text.toString().trim()
            if (phone.isEmpty()) {
                edtPhone.error = "Phone number is required"
                edtPhone.requestFocus()
                return false
            }

            // Validate address
            val address = edtAddress.text.toString().trim()
            if (address.isEmpty()) {
                edtAddress.error = "Address is required"
                edtAddress.requestFocus()
                return false
            }

            // Validate city
            val city = edtCity.text.toString().trim()
            if (city.isEmpty()) {
                edtCity.error = "City is required"
                edtCity.requestFocus()
                return false
            }

            // Validate postal code
            val postalCode = edtPostalCode.text.toString().trim()
            if (postalCode.isEmpty()) {
                edtPostalCode.error = "Postal code is required"
                edtPostalCode.requestFocus()
                return false
            }

            // Validate credit card if selected
            if (radioCreditCard.isChecked) {
                val cardNumber = edtCardNumber.text.toString().trim()
                if (cardNumber.isEmpty()) {
                    edtCardNumber.error = "Card number is required"
                    edtCardNumber.requestFocus()
                    return false
                }

                val cardExpiry = edtCardExpiry.text.toString().trim()
                if (cardExpiry.isEmpty()) {
                    edtCardExpiry.error = "Expiry date is required"
                    edtCardExpiry.requestFocus()
                    return false
                }

                val cardCvv = edtCardCvv.text.toString().trim()
                if (cardCvv.isEmpty()) {
                    edtCardCvv.error = "CVV is required"
                    edtCardCvv.requestFocus()
                    return false
                }
            }

            return true
        }
    }

    private fun processPayment() {
        binding.apply {
            // Get delivery information
            val fullName = edtFullName.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val address = edtAddress.text.toString().trim()
            val city = edtCity.text.toString().trim()
            val postalCode = edtPostalCode.text.toString().trim()

            // Get payment method
            val paymentMethod = when (radioGroup.checkedRadioButtonId) {
                R.id.radioCreditCard -> "Credit Card"
                R.id.radioCashOnDelivery -> "Cash on Delivery"
                R.id.radioPaypal -> "PayPal"
                else -> "Unknown"
            }

            // Save order to Firebase Database
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val orderId = FirebaseDatabase.getInstance().getReference("orders").push().key ?: return@apply
                
                // Prepare cart items for order
                val cartItems = managmentCart.getListCart()
                val orderItems = cartItems.map { item ->
                    hashMapOf(
                        "productId" to "",  // Product ID not stored in cart
                        "title" to item.title,
                        "price" to item.price,
                        "quantity" to item.numberInCart,
                        "size" to if (item.size.isNotEmpty()) item.size[0] else "",
                        "color" to if (item.color.isNotEmpty()) item.color[0] else "",
                        "thumbnail" to (item.picUrl.firstOrNull() ?: "")
                    )
                }
                
                // Prepare order data matching AdminApp structure
                val orderData = hashMapOf(
                    "userId" to currentUser.uid,
                    "userEmail" to (currentUser.email ?: ""),
                    "orderDate" to System.currentTimeMillis(),
                    "status" to "pending",
                    "deliveryInfo" to hashMapOf(
                        "fullName" to fullName,
                        "phone" to phone,
                        "address" to address,
                        "city" to city,
                        "postalCode" to postalCode
                    ),
                    "paymentMethod" to paymentMethod,
                    "items" to orderItems,
                    "pricing" to hashMapOf(
                        "subtotal" to subtotal,
                        "tax" to tax,
                        "delivery" to delivery,
                        "total" to total
                    )
                )
                
                // Save to Firebase
                FirebaseDatabase.getInstance().getReference("orders")
                    .child(orderId)
                    .setValue(orderData)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this@PaymentActivity,
                            "Order placed successfully!\nDelivering to: $address, $city",
                            Toast.LENGTH_LONG
                        ).show()
                        
                        // Clear the cart
                        managmentCart.clearCart()
                        
                        // Navigate to dashboard with cleared back stack
                        val intent = Intent(this@PaymentActivity, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this@PaymentActivity,
                            "Failed to place order: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } else {
                Toast.makeText(
                    this@PaymentActivity,
                    "Please login to place an order",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
