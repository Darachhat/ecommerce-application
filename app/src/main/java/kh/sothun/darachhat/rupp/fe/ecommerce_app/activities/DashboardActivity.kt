package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.BrandsAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.PopularAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.SliderAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityMainBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.SliderModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.viewModel.MainViewModel

class DashboardActivity : AppCompatActivity() {

    // use Kotlin indexing operator instead of explicit get()
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private lateinit var binding: ActivityMainBinding

    private val brandsAdapter = BrandsAdapter(mutableListOf())
    private val popularAdapter = PopularAdapter(mutableListOf())

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
        initPopulars()
    }

    private fun initPopulars() {
        binding.apply {
            recyclerViewPopular.layoutManager = LinearLayoutManager(this@DashboardActivity)
            recyclerViewPopular.adapter=popularAdapter
            progressBarPopular.visibility = View.VISIBLE
            viewModel.popular.observe(this@DashboardActivity){data->
                popularAdapter.updateData(data)
                progressBarPopular.visibility=View.GONE
            }

            viewModel.loadPopular()
        }
    }

    private fun initBrands() {
        binding.apply {
            recyclerViewBrands.layoutManager =
                LinearLayoutManager(this@DashboardActivity, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewBrands.adapter = brandsAdapter
            progressBarBrands.visibility = View.VISIBLE

            viewModel.brands.observe(this@DashboardActivity) { data ->
                brandsAdapter.updateData(data)
                progressBarBrands.visibility = View.GONE
            }
            viewModel.loadBrands()
        }
    }

    private fun initBanners() {
        binding.apply {
            progressBarBanner.visibility = View.VISIBLE
            viewModel.banners.observe(this@DashboardActivity) { data ->
                setupBanners(data) // renamed to follow Kotlin style
                progressBarBanner.visibility = View.GONE
            }
            viewModel.loadBanners()
        }
    }

    // renamed to 'setupBanners' (lowercase first letter)
    private fun setupBanners(images: List<SliderModel>) {
        val pager: ViewPager2 = binding.viewpagerSilder

        // set adapter (SliderAdapter must NOT mutate its list during swipes)
        pager.adapter = SliderAdapter(images, pager)

        // visual / performance tweaks
        pager.clipToPadding = false
        pager.clipChildren = false
        pager.offscreenPageLimit = 1
        pager.isNestedScrollingEnabled = false // important

        // access internal RecyclerView (child index 0) and lock its scrolling/overscroll
        (pager.getChildAt(0) as? RecyclerView)?.let { internalRv ->
            internalRv.isNestedScrollingEnabled = false
            internalRv.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            (internalRv.layoutManager as? LinearLayoutManager)?.isItemPrefetchEnabled = false
        }

        // prevent parent (NestedScrollView) from intercepting horizontal swipes while not idle
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                (pager.parent as? ViewGroup)?.requestDisallowInterceptTouchEvent(state != ViewPager2.SCROLL_STATE_IDLE)
            }
        })

        // extra: immediately disallow parent intercept on touch down (helps flaky devices)
        // ensure performClick() is called on ACTION_UP for accessibility/lint
        pager.getChildAt(0)?.setOnTouchListener { v, ev ->
            val parentView = pager.parent
            if (parentView is ViewGroup) {
                when (ev.action) {
                    MotionEvent.ACTION_DOWN -> parentView.requestDisallowInterceptTouchEvent(true)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        parentView.requestDisallowInterceptTouchEvent(false)
                        // call performClick to satisfy accessibility/lint (and potential click listeners)
                        v?.performClick()
                    }
                }
            }
            false // allow normal event propagation
        }

        // dots indicator
        if (images.size > 1) {
            binding.dotIndicator.visibility = View.VISIBLE
            binding.dotIndicator.attachTo(pager)
        } else {
            binding.dotIndicator.visibility = View.GONE
        }
    }
}
