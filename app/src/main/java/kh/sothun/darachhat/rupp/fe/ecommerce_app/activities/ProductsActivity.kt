package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.PopularAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityProductsBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.ItemModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.viewModel.MainViewModel

class ProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductsBinding
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    private val adapter = PopularAdapter(mutableListOf())
    private var allProducts: MutableList<ItemModel> = mutableListOf()
    private var selectedBrandId: String? = null
    private var selectedCategoryId: String? = null
    private var sortOption: String = "Rating"
    private var searchQuery: String = ""
    private var selectedBrandName: String = "All"
    private var selectedCategoryName: String = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pre-fill search if launched with a query
        intent.getStringExtra(EXTRA_SEARCH_QUERY)?.trim()?.takeIf { it.isNotEmpty() }?.let { initialQuery ->
            searchQuery = initialQuery
            binding.searchInput.setText(initialQuery)
            binding.searchInput.setSelection(initialQuery.length)
        }

        initUI()
    }

    private fun initUI() {
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewProducts.adapter = adapter
        binding.backBtn.setOnClickListener { finish() }

        viewModel.allProducts.observe(this) { list ->
            allProducts = list
            applyAll()
            binding.progressBarProducts.visibility = View.GONE
        }
        binding.progressBarProducts.visibility = View.VISIBLE
        viewModel.loadAllProducts()

        viewModel.brands.observe(this) { brands ->
            val names = mutableListOf("All")
            val ids = mutableListOf("")
            brands.forEach { b ->
                names.add(b.title)
                ids.add(b.id)
            }
            val adapterSpinner = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerBrand.adapter = adapterSpinner
            binding.spinnerBrand.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedBrandId = ids.getOrNull(position)
                    selectedBrandName = names.getOrNull(position) ?: "All"
                    binding.selectedBrandLabel.text = "Brand: $selectedBrandName"
                    applyAll()
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
        }
        viewModel.loadBrands()

        viewModel.categories.observe(this) { cats ->
            val names = mutableListOf("All")
            val ids = mutableListOf("")
            cats.forEach { c ->
                names.add(c.title)
                ids.add(c.id)
            }
            val adapterSpinner = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategoryAll.adapter = adapterSpinner
            binding.spinnerCategoryAll.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedCategoryId = ids.getOrNull(position)
                    selectedCategoryName = names.getOrNull(position) ?: "All"
                    binding.selectedCategoryLabel.text = "Category: $selectedCategoryName"
                    applyAll()
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
        }
        viewModel.loadCategories()

        val sortOptions = listOf("Rating", "Price ↑", "Price ↓", "Newest")
        val sortAdapter = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSort.adapter = sortAdapter
        binding.spinnerSort.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                sortOption = sortOptions[position]
                binding.selectedSortLabel.text = "Sort: $sortOption"
                applyAll()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        binding.searchInput.setOnEditorActionListener { v, a, e ->
            searchQuery = v.text.toString().trim()
            applyAll()
            false
        }
        binding.clearBtn.setOnClickListener {
            selectedBrandId = null
            selectedCategoryId = null
            searchQuery = ""
            binding.searchInput.text?.clear()
            binding.spinnerBrand.setSelection(0)
            binding.spinnerCategoryAll.setSelection(0)
            binding.spinnerSort.setSelection(0)
            selectedBrandName = "All"
            selectedCategoryName = "All"
            binding.selectedBrandLabel.text = "Brand: $selectedBrandName"
            binding.selectedCategoryLabel.text = "Category: $selectedCategoryName"
            binding.selectedSortLabel.text = "Sort: Rating"
            applyAll()
        }
    }

    private fun applyAll() {
        var list = allProducts.asSequence()
        val bid = selectedBrandId
        val cid = selectedCategoryId
        if (!bid.isNullOrEmpty()) list = list.filter { it.brandId == bid }
        if (!cid.isNullOrEmpty()) list = list.filter { it.categoryId == cid }
        val q = searchQuery
        if (q.isNotEmpty()) list = list.filter { it.title.contains(q, ignoreCase = true) }
        val result = list.toMutableList()
        when (sortOption) {
            "Price ↑" -> result.sortBy { it.price }
            "Price ↓" -> result.sortByDescending { it.price }
            "Newest" -> result.sortByDescending { it.hashCode() }
            else -> result.sortByDescending { it.rating }
        }
        adapter.updateData(result)
    }

    companion object {
        const val EXTRA_SEARCH_QUERY = "extra_search_query"
    }
}
