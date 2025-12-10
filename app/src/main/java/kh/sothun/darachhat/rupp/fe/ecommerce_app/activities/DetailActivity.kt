package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kh.sothun.darachhat.rupp.fe.ecommerce_app.R
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.ColorAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.SizeAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityDetailBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ViewholderPopularBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.helpers.ManagmentCart
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.ItemModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemModel
    private lateinit var managmentCart: ManagmentCart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        item = intent.getSerializableExtra("object")!! as ItemModel

        setupViews()
        setupSizeList()
        setupColorList()

    }

    private fun setupColorList() {
        binding.apply {
            colorList.adapter = ColorAdapter(item.color)
            colorList.layoutManager = LinearLayoutManager(
                this@DetailActivity,
                LinearLayoutManager.VERTICAL, false
            )
        }
    }

    private fun setupSizeList() {
        val sizeList = item.size.map { it }
        binding.sizeList.apply {
            adapter = SizeAdapter(sizeList as ArrayList<String>)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupViews() = with(binding) {
        ttleTxt.text = item.title
        descriptionTxt.text = item.description
        priceTxt.text = "$${item.price}"
        numberItemTxt.text = item.numberInCart.toString()
        updateTotalPrice()

        Glide.with(this@DetailActivity)
            .load(item.picUrl.firstOrNull())
            .into(picMain)

        backBtn.setOnClickListener { finish() }

        plusBtn.setOnClickListener {
            item.numberInCart++
            numberItemTxt.text = item.numberInCart.toString()
            updateTotalPrice()
        }

        minusBtn.setOnClickListener {
            if(item.numberInCart > 1) {
                item.numberInCart--
                numberItemTxt.text = item.numberInCart.toString()
                updateTotalPrice()
            }
        }
    }

    private fun updateTotalPrice()= with(binding) {
        totalPriceTxt.text = "$${item.price * item.numberInCart}"
    }
}