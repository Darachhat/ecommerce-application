package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kh.sothun.darachhat.rupp.fe.ecommerce_app.R
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.CartAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityCartBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ViewholderCartBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.helpers.ChangeNumberItemsListener
import kh.sothun.darachhat.rupp.fe.ecommerce_app.helpers.ManagmentCart

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var managemendCart: ManagmentCart
    private lateinit var auth: FirebaseAuth
    private var tax: Double=0.0
    private var delivery: Double=10.0
    private var subtotal: Double=0.0
    private var total: Double=0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        managemendCart = ManagmentCart(this)
        auth = FirebaseAuth.getInstance()

        initViews()
        calculatorCart()
        initCartList()
    }

    private fun initCartList() {
        binding.apply {
            viewCart.layoutManager = LinearLayoutManager(
                this@CartActivity,
                LinearLayoutManager.VERTICAL,false
            )
            viewCart.adapter = CartAdapter(managemendCart.getListCart(),
                this@CartActivity, object :
                ChangeNumberItemsListener{
                    override fun onChanged() {
                        calculatorCart()
                    }

                }
            )
            if(managemendCart.getListCart().isEmpty()){
                emptyTxt.visibility = View.VISIBLE
                viewCart.visibility = View.GONE
            } else{
                emptyTxt.visibility = View.GONE
                viewCart.visibility = View.VISIBLE
            }
        }
    }

    private fun initViews() {
        binding.cartBtn.setOnClickListener { finish() }
        
        binding.button.setOnClickListener {
            // Check if user is logged in
            if (auth.currentUser == null) {
                android.widget.Toast.makeText(
                    this,
                    "Please login to proceed with checkout",
                    android.widget.Toast.LENGTH_LONG
                ).show()
                // Navigate to login
                startActivity(Intent(this, LoginActivity::class.java))
                return@setOnClickListener
            }
            
            if (managemendCart.getListCart().isNotEmpty()) {
                val intent = Intent(this, PaymentActivity::class.java).apply {
                    putExtra("subtotal", subtotal)
                    putExtra("tax", tax)
                    putExtra("delivery", delivery)
                    putExtra("total", total)
                }
                startActivity(intent)
            }
        }
    }

    private fun calculatorCart(){
        val percentTax=0.02
        delivery=10.0
        tax = Math.round((managemendCart.getTotalFee()*percentTax)*100)/100.0
        total = Math.round((managemendCart.getTotalFee() + tax + delivery)*100)/100.0
        subtotal = Math.round(managemendCart.getTotalFee() * 100)/100.0

        binding.apply {
            totalFeeTxt.text = "$$subtotal"
            taxTxt.text= "$$tax"
            deliveryTxt.text = "$$delivery"
            totalTxt.text = "$$total"
        }
    }
}
