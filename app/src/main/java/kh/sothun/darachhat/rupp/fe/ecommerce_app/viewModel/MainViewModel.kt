package kh.sothun.darachhat.rupp.fe.ecommerce_app.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.BrandModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.SliderModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.repository.MainRepository

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    val brands: LiveData<MutableList<BrandModel>> = repository.brands
    val banners: LiveData<List<SliderModel>> = repository.banners

    fun loadBrands() = repository.loadBrands()
    fun loadBanners() = repository.loadBanners()

}