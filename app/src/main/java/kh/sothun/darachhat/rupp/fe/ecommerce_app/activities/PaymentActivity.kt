package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kh.sothun.darachhat.rupp.fe.ecommerce_app.R
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityPaymentBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.helpers.ManagmentCart

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var managmentCart: ManagmentCart
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

            // Here you would normally process the payment and save order to Firebase
            // For now, just show success message and clear cart
            
            Toast.makeText(
                this@PaymentActivity,
                "Order placed successfully!\nDelivering to: $address, $city",
                Toast.LENGTH_LONG
            ).show()

            // Clear the cart
            managmentCart.clearCart()

            // Return to main activity
            finishAffinity()
        }
    }
}
