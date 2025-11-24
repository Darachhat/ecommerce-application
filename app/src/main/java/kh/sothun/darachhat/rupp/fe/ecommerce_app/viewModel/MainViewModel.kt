package kh.sothun.darachhat.rupp.fe.ecommerce_app.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.BrandModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.repository.MainRepository

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    val brands: LiveData<MutableList<BrandModel>> = repository.brands

    fun loadBrands() = repository.loadBrands()
}