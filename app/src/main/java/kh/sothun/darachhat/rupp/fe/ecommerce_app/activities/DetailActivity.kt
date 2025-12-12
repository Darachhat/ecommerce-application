package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import kh.sothun.darachhat.rupp.fe.ecommerce_app.R
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityDetailBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.helpers.ManagmentCart
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.ItemsModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(context = this)
        item = intent.getSerializableExtra(name = "object")!! as ItemsModel

        setupViews()
    }

    private fun setupViews() = with(receiver = binding) {
        titleTxt.text = item.title
        descriptionTxt.text = item.description
        priceTxt.text = "$${item.price}"
        numberItemTxt.text = item.numberInCart.toString()
        updateTotalPrice()

        Glide.with(this@DetailActivity)
            .load( model = item.picUrl.getfirstOrNull)
            .into( view = picMain)

        backBtn.setOnClickListener { finish() }

        plusBtn.setOnClickListener{
            item.numberInCart++
            numberItemTxt.text = item.numberInCart.toString()
            updateTotalPrice()
        }
        minusBtn.setOnClickListener{
            if (item.numberInCart > 1) {
                item.numberInCart--
                numberItemTxt.text = item.numberInCart.toString()
                updateTotalPrice()
            }
        }

    private fun updateTotalPrice() = with(receiver = binding) {
        totalPriceTxt.text = "$${item.price * item.numberInCart}"
    }
}}