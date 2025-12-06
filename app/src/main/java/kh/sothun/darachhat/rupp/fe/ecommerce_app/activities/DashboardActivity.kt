package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import kh.sothun.darachhat.rupp.fe.ecommerce_app.R
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.BrandsAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.SliderAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityMainBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.BrandModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.SliderModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.viewModel.MainViewModel

class DashboardActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private lateinit var binding: ActivityMainBinding

    private val brandsAdapter = BrandsAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        initBrands()
        initBanners()
    }

    private fun initBrands() {
        binding.apply {
            recyclerViewBrands.layoutManager =
                LinearLayoutManager(this@DashboardActivity,
                    LinearLayoutManager.HORIZONTAL, false)
            recyclerViewBrands.adapter = brandsAdapter
            progressBarBrands.visibility = View.VISIBLE

            viewModel.brands.observe(this@DashboardActivity){data->
               brandsAdapter.updateData(data)
                progressBarBrands.visibility = View.GONE
            }
            viewModel.loadBrands()
        }
    }

    private fun initBanners(){
        binding.apply {
            progressBarBanner.visibility = View.VISIBLE
            viewModel.banners.observe(this@DashboardActivity){data->
                SetupBanners(data)
                progressBarBanner.visibility = View.GONE
            }
            viewModel.loadBanners()
        }
    }

    private fun SetupBanners(image: List<SliderModel>){
        binding.viewpagerSilder.apply {
            adapter = SliderAdapter(image, this)
            clipToPadding=false
            clipChildren=false
            offscreenPageLimit=3
            (getChildAt(3) as? RecyclerView)?.overScrollMode=
                RecyclerView.OVER_SCROLL_NEVER
            setPageTransformer(CompositePageTransformer().apply {
                addTransformer(MarginPageTransformer(40))
            })
        }
        binding.dotIndicator.apply {
            visibility = if(image.size>1) View.VISIBLE else View.GONE
            if(image.size>1) attachTo(binding.viewpagerSilder)
        }
    }
}