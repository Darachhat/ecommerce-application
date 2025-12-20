package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
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
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.CategoryModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.SliderModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.viewModel.MainViewModel

class DashboardActivity : AppCompatActivity() {

    // use Kotlin indexing operator instead of explicit get()
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private val brandsAdapter = BrandsAdapter(mutableListOf()) { brand ->
        selectedBrandId = brand.id
        applyFilters()
    }
    private val popularAdapter = PopularAdapter(mutableListOf())
    private var allPopularItems: MutableList<kh.sothun.darachhat.rupp.fe.ecommerce_app.model.ItemModel> = mutableListOf()
    private var selectedBrandId: String? = null
    private var selectedCategoryId: String? = null
    private var isSearching: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        checkAuthenticationState()
        initUI()
    }

    private fun initUI() {
        initBrands()
        initBanners()
        initPopulars()
        initCategoriesFilter()
        initBottomNavigation()
        initSearchAndOtherElements()
    }

    private fun checkAuthenticationState() {
        val currentUser = auth.currentUser
        binding.apply {
            if (currentUser != null) {
                // User is logged in
                welcomeSection.visibility = View.VISIBLE
                loginSection.visibility = View.GONE
                textView3.text = currentUser.displayName ?: "User"
            } else {
                // User is not logged in
                welcomeSection.visibility = View.GONE
                loginSection.visibility = View.VISIBLE
                
                // Handle login button click
                loginBtn.setOnClickListener {
                    startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                }
            }
        }
    }

    private fun initSearchAndOtherElements() {
        binding.apply {
            // Search functionality
            editTextText.setOnEditorActionListener { v, actionId, event ->
                val query = v.text.toString()
                if (query.isNotEmpty()) {
                    isSearching = true
                    binding.progressBarPopular.visibility = View.VISIBLE
                    viewModel.searchProducts(query)
                }
                false
            }

            editTextText.addTextChangedListener { text ->
                val q = text?.toString()?.trim() ?: ""
                if (q.isEmpty()) {
                    isSearching = false
                    applyFilters()
                }
            }


            // "See all" text click
            textView5.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, ProductsActivity::class.java))
            }
        }
    }

    private fun initBottomNavigation() {
        binding.apply {
            // Main/Home button - refresh or scroll to top
            mainBtn.setOnClickListener {
                binding.recyclerViewPopular.smoothScrollToPosition(0)
                binding.recyclerViewBrands.smoothScrollToPosition(0)
            }

            // Cart button
            cartBtn.setOnClickListener {
                // Check if user is logged in
                if (auth.currentUser == null) {
                    android.widget.Toast.makeText(
                        this@DashboardActivity,
                        "Please login to view your cart",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                    return@setOnClickListener
                }
                startActivity(Intent(this@DashboardActivity, CartActivity::class.java))
            }

            // Favorite button
            favoriteBtn.setOnClickListener {
                // Check if user is logged in
                if (auth.currentUser == null) {
                    android.widget.Toast.makeText(
                        this@DashboardActivity,
                        "Please login to view your favorites",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                    return@setOnClickListener
                }
                startActivity(Intent(this@DashboardActivity, FavoriteActivity::class.java))
            }

            // Orders button

            // Profile button
            profileBtn.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, ProfileActivity::class.java))
            }
        }
    }

    private fun initPopulars() {
        binding.apply {
            recyclerViewPopular.layoutManager = LinearLayoutManager(this@DashboardActivity)
            recyclerViewPopular.adapter=popularAdapter
            progressBarPopular.visibility = View.VISIBLE
            viewModel.popular.observe(this@DashboardActivity){data->
                allPopularItems = data
                applyFilters()
                progressBarPopular.visibility=View.GONE
            }

            viewModel.filteredProducts.observe(this@DashboardActivity) { data ->
                popularAdapter.updateData(data)
                progressBarPopular.visibility = View.GONE
            }

            viewModel.searchResults.observe(this@DashboardActivity) { data ->
                if (isSearching) {
                    popularAdapter.updateData(data)
                    binding.progressBarPopular.visibility = View.GONE
                }
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

    private fun initCategoriesFilter() {
        binding.apply {
            val spinner = spinnerCategory
            val context = this@DashboardActivity
            viewModel.categories.observe(context) { list ->
                val names = list.map { it.title }
                val ids = list.map { it.id }
                val adapter = android.widget.ArrayAdapter(context, android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedCategoryId = ids.getOrNull(position)
                        applyFilters()
                    }
                    override fun onNothingSelected(parent: android.widget.AdapterView<*>?) { }
                }
            }
            clearFiltersBtn.setOnClickListener {
                selectedBrandId = null
                selectedCategoryId = null
                applyFilters()
            }
            viewModel.loadCategories()
        }
    }

    private fun applyFilters() {
        val brandId = selectedBrandId
        val categoryId = selectedCategoryId

        if (!brandId.isNullOrEmpty() && !categoryId.isNullOrEmpty()) {
            binding.progressBarPopular.visibility = View.VISIBLE
            viewModel.loadProductsByCategoryThenFilterBrand(categoryId, brandId)
            return
        }
        if (!brandId.isNullOrEmpty()) {
            binding.progressBarPopular.visibility = View.VISIBLE
            viewModel.loadProductsByBrand(brandId)
            return
        }
        if (!categoryId.isNullOrEmpty()) {
            binding.progressBarPopular.visibility = View.VISIBLE
            viewModel.loadProductsByCategory(categoryId)
            return
        }

        val filtered = allPopularItems.toMutableList()
        popularAdapter.updateData(filtered)
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
