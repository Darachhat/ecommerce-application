package kh.sothun.darachhat.rupp.fe.ecommerce_app.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.BrandModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.CategoryModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.ItemModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.SliderModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.repository.MainRepository

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    val brands: LiveData<MutableList<BrandModel>> = repository.brands
    val banners: LiveData<List<SliderModel>> = repository.banners
    val categories: LiveData<MutableList<CategoryModel>> = repository.categories
    val filteredProducts: LiveData<MutableList<ItemModel>> = repository.filteredProducts
    val allProducts: LiveData<MutableList<ItemModel>> = repository.allProducts

    val popular: LiveData<MutableList<ItemModel>> = repository.popular

    fun loadBrands() = repository.loadBrands()
    fun loadBanners() = repository.loadBanners()
    fun loadPopular() = repository.loadPopular()
    fun loadCategories() = repository.loadCategories()
    fun loadProductsByBrand(brandId: String) = repository.loadProductsByBrand(brandId)
    fun loadProductsByCategory(categoryId: String) = repository.loadProductsByCategory(categoryId)
    fun loadProductsByCategoryThenFilterBrand(categoryId: String, brandId: String) = repository.loadProductsByCategoryThenFilterBrand(categoryId, brandId)
    fun loadAllProducts() = repository.loadAllProducts()


}
