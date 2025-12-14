package kh.sothun.darachhat.rupp.fe.ecommerce_app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.BrandModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.ItemModel
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.SliderModel

class MainRepository {
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _brands = MutableLiveData<MutableList<BrandModel>>()
    private val _banners = MutableLiveData<List<SliderModel>>()
    private val _popular = MutableLiveData<MutableList<ItemModel>>()



    val brands: LiveData<MutableList<BrandModel>> get() = _brands
    val banners: LiveData<List<SliderModel>> get() = _banners
    val popular: LiveData<MutableList<ItemModel>> get()= _popular


    fun loadBrands(){
        // Try new schema first (brands), fallback to old schema (Category)
        val newRef = firebaseDatabase.getReference("brands")
        val oldRef = firebaseDatabase.getReference("Category")
        
        newRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.childrenCount > 0) {
                    // New schema has data
                    loadBrandsFromNewSchema()
                } else {
                    // Fallback to old schema
                    loadBrandsFromOldSchema()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Fallback to old schema on error
                loadBrandsFromOldSchema()
            }
        })
    }
    
    private fun loadBrandsFromNewSchema() {
        val ref = firebaseDatabase.getReference("brands")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BrandModel>()
                for (child in snapshot.children){
                    val id = child.key ?: ""
                    val name = child.child("name").getValue(String::class.java) ?: ""
                    val picUrl = child.child("picUrl").getValue(String::class.java) ?: ""
                    val active = child.child("active").getValue(Boolean::class.java) ?: true
                    
                    if (active && name.isNotEmpty()) {
                        list.add(BrandModel(
                            id = id.hashCode(),
                            title = name,
                            picUrl = picUrl
                        ))
                    }
                }
                _brands.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load brands: ${error.message}")
                _brands.value = mutableListOf()
            }
        })
    }
    
    private fun loadBrandsFromOldSchema() {
        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BrandModel>()
                for (child in snapshot.children){
                    child.getValue(BrandModel::class.java)?.let {
                        list.add(it)
                    }
                }
                _brands.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load brands: ${error.message}")
                _brands.value = mutableListOf()
            }
        })
    }

    fun loadBanners(){
        // Try new schema first (banners), fallback to old schema (Banner)
        val newRef = firebaseDatabase.getReference("banners")
        val oldRef = firebaseDatabase.getReference("Banner")
        
        newRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.childrenCount > 0) {
                    loadBannersFromNewSchema()
                } else {
                    loadBannersFromOldSchema()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                loadBannersFromOldSchema()
            }
        })
    }
    
    private fun loadBannersFromNewSchema() {
        val ref = firebaseDatabase.getReference("banners")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<SliderModel>()
                for (child in snapshot.children){
                    val id = child.key ?: ""
                    val imageUrl = child.child("imageUrl").getValue(String::class.java) ?: ""
                    val active = child.child("active").getValue(Boolean::class.java) ?: true
                    
                    if (active && imageUrl.isNotEmpty()) {
                        list.add(SliderModel(url = imageUrl))
                    }
                }
                _banners.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load banners: ${error.message}")
                _banners.value = emptyList()
            }
        })
    }
    
    private fun loadBannersFromOldSchema() {
        val ref = firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<SliderModel>()
                for (child in snapshot.children){
                    child.getValue(SliderModel::class.java)?.let {
                        list.add(it)
                    }
                }
                _banners.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load banners: ${error.message}")
                _banners.value = emptyList()
            }
        })
    }

    fun loadPopular(){
        // Try new schema first (products), fallback to old schema (Items)
        val newRef = firebaseDatabase.getReference("products")
        val oldRef = firebaseDatabase.getReference("Items")
        
        newRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.childrenCount > 0) {
                    android.util.Log.d("MainRepository", "Loading products from NEW schema (${snapshot.childrenCount} items)")
                    loadProductsFromNewSchema()
                } else {
                    android.util.Log.d("MainRepository", "No data in new schema, trying OLD schema")
                    loadProductsFromOldSchema()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Error checking new schema: ${error.message}")
                loadProductsFromOldSchema()
            }
        })
    }
    
    private fun loadProductsFromNewSchema() {
        val ref = firebaseDatabase.getReference("products")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (child in snapshot.children){
                    val id = child.key ?: ""
                    val title = child.child("title").getValue(String::class.java) ?: ""
                    val description = child.child("description").getValue(String::class.java) ?: ""
                    val price = child.child("price").getValue(Double::class.java) ?: 0.0
                    val oldPrice = child.child("oldPrice").getValue(Double::class.java) ?: 0.0
                    val thumbnail = child.child("thumbnail").getValue(String::class.java) ?: ""
                    val categoryId = child.child("categoryId").getValue(String::class.java) ?: ""
                    val brandId = child.child("brandId").getValue(String::class.java) ?: ""
                    val active = child.child("active").getValue(Boolean::class.java) ?: true
                    val rating = child.child("rating").getValue(Double::class.java) ?: 0.0
                    
                    // Read picUrl array
                    val picUrlList = arrayListOf<String>()
                    child.child("picUrl").children.forEach { picChild ->
                        picChild.getValue(String::class.java)?.let { picUrlList.add(it) }
                    }
                    if (picUrlList.isEmpty() && thumbnail.isNotEmpty()) {
                        picUrlList.add(thumbnail)
                    }
                    
                    // Read size array
                    val sizeList = arrayListOf<String>()
                    child.child("size").children.forEach { sizeChild ->
                        sizeChild.getValue(String::class.java)?.let { sizeList.add(it) }
                    }
                    
                    // Read color array
                    val colorList = arrayListOf<String>()
                    child.child("color").children.forEach { colorChild ->
                        colorChild.getValue(String::class.java)?.let { colorList.add(it) }
                    }
                    
                    if (active && title.isNotEmpty()) {
                        list.add(ItemModel(
                            title = title,
                            description = description,
                            picUrl = picUrlList,
                            size = sizeList,
                            color = colorList,
                            price = price,
                            oldPrice = oldPrice,
                            rating = rating,
                            numberInCart = 1
                        ))
                    }
                }
                // Sort by rating (highest first) and take top 10
                val topRated = list.sortedByDescending { it.rating }.take(10).toMutableList()
                android.util.Log.d("MainRepository", "Loaded ${list.size} products, showing top 10 by rating")
                _popular.value = topRated
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load popular items: ${error.message}")
                _popular.value = mutableListOf()
            }
        })
    }
    
    private fun loadProductsFromOldSchema() {
        val ref = firebaseDatabase.getReference("Items")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (child in snapshot.children){
                    child.getValue(ItemModel::class.java)?.let {
                        list.add(it)
                    }
                }
                // Sort by rating (highest first) and take top 10
                val topRated = list.sortedByDescending { it.rating }.take(10).toMutableList()
                android.util.Log.d("MainRepository", "Loaded ${list.size} products, showing top 10 by rating")
                _popular.value = topRated
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load popular items: ${error.message}")
                _popular.value = mutableListOf()
            }
        })
    }

}