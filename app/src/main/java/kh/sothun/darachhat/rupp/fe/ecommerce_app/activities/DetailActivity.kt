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
import kh.sothun.darachhat.rupp.fe.ecommerce_app.helpers.TinyDB
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.ItemModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemModel
    private lateinit var managmentCart: ManagmentCart
    private lateinit var tinyDB: TinyDB
    private var isFavorite = false
    private lateinit var colorAdapter: ColorAdapter
    private lateinit var sizeAdapter: SizeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        tinyDB = TinyDB(this)
        item = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("object", ItemModel::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("object")!! as ItemModel
        }

        setupViews()
        setupSizeList()
        setupColorList()
        checkFavoriteStatus()
    }

    private fun setupColorList() {
        binding.apply {
            colorAdapter = ColorAdapter(item.color)
            colorList.adapter = colorAdapter
            colorList.layoutManager = LinearLayoutManager(
                this@DetailActivity,
                LinearLayoutManager.VERTICAL, false
            )
        }
    }

    private fun setupSizeList() {
        val sizeList = item.size.map { it }
        sizeAdapter = SizeAdapter(sizeList as ArrayList<String>)
        binding.sizeList.apply {
            adapter = sizeAdapter
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

        addToCartBtn.setOnClickListener {
            val selectedColor = colorAdapter.getSelectedColor()
            val selectedSize = sizeAdapter.getSelectedSize()
            
            when {
                selectedColor == null && selectedSize == null -> {
                    android.widget.Toast.makeText(
                        this@DetailActivity,
                        "Please select color and size",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                selectedColor == null -> {
                    android.widget.Toast.makeText(
                        this@DetailActivity,
                        "Please select a color",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                selectedSize == null -> {
                    android.widget.Toast.makeText(
                        this@DetailActivity,
                        "Please select a size",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    item.numberInCart = numberItemTxt.text.toString().toInt()
                    managmentCart.insertFood(item)
                    android.widget.Toast.makeText(
                        this@DetailActivity,
                        "Added to cart",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }

        addToFavoriteBtn.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun checkFavoriteStatus() {
        val favoriteList = getFavoriteList()
        isFavorite = favoriteList.any { it.title == item.title }
        updateFavoriteIcon()
    }

    private fun toggleFavorite() {
        val favoriteList = getFavoriteList()
        
        if (isFavorite) {
            // Remove from favorites
            favoriteList.removeAll { it.title == item.title }
            android.widget.Toast.makeText(this, "Removed from favorites", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            // Add to favorites
            favoriteList.add(item)
            android.widget.Toast.makeText(this, "Added to favorites", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        tinyDB.putListObject("FavoriteList", favoriteList)
        isFavorite = !isFavorite
        updateFavoriteIcon()
    }

    private fun updateFavoriteIcon() {
        binding.addToFavoriteBtn.setImageResource(
            if (isFavorite) R.drawable.btn_3 else R.drawable.fav_icon
        )
    }

    private fun getFavoriteList(): ArrayList<ItemModel> {
        return tinyDB.getListObject("FavoriteList") ?: arrayListOf()
    }

    private fun updateTotalPrice()= with(binding) {
        totalPriceTxt.text = "$${item.price * item.numberInCart}"
    }
}