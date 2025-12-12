package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kh.sothun.darachhat.rupp.fe.ecommerce_app.R
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.CartAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityCartBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ViewholderCartBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.helpers.ChangeNumberItemsListener
import kh.sothun.darachhat.rupp.fe.ecommerce_app.helpers.ManagmentCart

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var managemendCart: ManagmentCart
    private var tax: Double=0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        managemendCart = ManagmentCart(this)

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
    }

    private fun calculatorCart(){
        val percentTax=0.02
        val delivery=10.0
        tax = Math.round((managemendCart.getTotalFee()*percentTax)*100)/100.0
        val total = Math.round((managemendCart.getTotalFee() + tax + delivery)*100)/100.0
        val itemTotal = Math.round(managemendCart.getTotalFee() * 100)/100.0

        binding.apply {
            totalFeeTxt.text = "$$itemTotal"
            taxTxt.text= "$$tax"
            deliveryTxt.text = "$$delivery"
            totalFeeTxt.text = "$$total"
        }
    }
}