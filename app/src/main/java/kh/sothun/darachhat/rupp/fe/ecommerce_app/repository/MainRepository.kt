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
    private val _filteredProducts = MutableLiveData<MutableList<ItemModel>>()
    private val _searchResults = MutableLiveData<MutableList<ItemModel>>()
    private val _allProducts = MutableLiveData<MutableList<ItemModel>>()



    val brands: LiveData<MutableList<BrandModel>> get() = _brands
    val banners: LiveData<List<SliderModel>> get() = _banners
    val popular: LiveData<MutableList<ItemModel>> get()= _popular
    val filteredProducts: LiveData<MutableList<ItemModel>> get() = _filteredProducts
    val searchResults: LiveData<MutableList<ItemModel>> get() = _searchResults
    val allProducts: LiveData<MutableList<ItemModel>> get() = _allProducts


    fun loadBrands() {
        val ref = firebaseDatabase.getReference("brands")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BrandModel>()
                for (child in snapshot.children) {
                    val id = child.key ?: ""
                    val name = child.child("name").getValue(String::class.java) ?: ""
                    val picUrl = child.child("picUrl").getValue(String::class.java) ?: ""
                    val active = child.child("active").getValue(Boolean::class.java) ?: true

                    if (active && name.isNotEmpty()) {
                        list.add(BrandModel(
                            id = id,
                            title = name,
                            picUrl = picUrl
                        ))
                    }
                }
                _brands.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load brands: ${error.message}")
            }
        })
    }

    fun loadBanners() {
        val ref = firebaseDatabase.getReference("banners")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<SliderModel>()
                for (child in snapshot.children) {
                    val imageUrl = child.child("picUrl").getValue(String::class.java) ?: ""
                    val active = child.child("active").getValue(Boolean::class.java) ?: true

                    if (active && imageUrl.isNotEmpty()) {
                        list.add(SliderModel(url = imageUrl))
                    }
                }
                _banners.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load banners: ${error.message}")
            }
        })
    }

    fun loadPopular() {
        val ref = firebaseDatabase.getReference("products")
        // OPTIMIZATION: Query only the top 10 rated items.
        // limitToLast(10) gets the highest values when sorting by numbers.
        val query = ref.orderByChild("rating").limitToLast(10)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (child in snapshot.children) {
                    try {
                        val title = child.child("title").getValue(String::class.java) ?: ""
                        val description = child.child("description").getValue(String::class.java) ?: ""
                        val price = child.child("price").getValue(Double::class.java) ?: 0.0
                        val oldPrice = child.child("oldPrice").getValue(Double::class.java) ?: 0.0
                        val thumbnail = child.child("thumbnail").getValue(String::class.java) ?: ""
                        val active = child.child("active").getValue(Boolean::class.java) ?: true
                        val rating = child.child("rating").getValue(Double::class.java) ?: 0.0
                        val brandId = child.child("brandId").getValue(String::class.java) ?: ""
                        val categoryId = child.child("categoryId").getValue(String::class.java) ?: ""

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
                            list.add(
                                ItemModel(
                                    title = title,
                                    description = description,
                                    picUrl = picUrlList,
                                    size = sizeList,
                                    color = colorList,
                                    price = price,
                                    oldPrice = oldPrice,
                                    rating = rating,
                                    numberInCart = 1,
                                    brandId = brandId,
                                    categoryId = categoryId
                                )
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainRepository", "Error parsing item: ${e.message}")
                    }
                }
                // Reverse to show highest rating first (limitToLast returns ascending order)
                list.reverse()
                _popular.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load popular items: ${error.message}")
            }
        })
    }

    fun loadAllProducts() {
        val ref = firebaseDatabase.getReference("products")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (child in snapshot.children) {
                    try {
                        val title = child.child("title").getValue(String::class.java) ?: ""
                        val description = child.child("description").getValue(String::class.java) ?: ""
                        val price = child.child("price").getValue(Double::class.java) ?: 0.0
                        val oldPrice = child.child("oldPrice").getValue(Double::class.java) ?: 0.0
                        val thumbnail = child.child("thumbnail").getValue(String::class.java) ?: ""
                        val active = child.child("active").getValue(Boolean::class.java) ?: true
                        val rating = child.child("rating").getValue(Double::class.java) ?: 0.0
                        val brandIdVal = child.child("brandId").getValue(String::class.java) ?: ""
                        val categoryIdVal = child.child("categoryId").getValue(String::class.java) ?: ""

                        val picUrlList = arrayListOf<String>()
                        child.child("picUrl").children.forEach { it.getValue(String::class.java)?.let(picUrlList::add) }
                        if (picUrlList.isEmpty() && thumbnail.isNotEmpty()) picUrlList.add(thumbnail)

                        val sizeList = arrayListOf<String>()
                        child.child("size").children.forEach { it.getValue(String::class.java)?.let(sizeList::add) }

                        val colorList = arrayListOf<String>()
                        child.child("color").children.forEach { it.getValue(String::class.java)?.let(colorList::add) }

                        if (active && title.isNotEmpty()) {
                            list.add(
                                ItemModel(
                                    title = title,
                                    description = description,
                                    picUrl = picUrlList,
                                    size = sizeList,
                                    color = colorList,
                                    price = price,
                                    oldPrice = oldPrice,
                                    rating = rating,
                                    numberInCart = 1,
                                    brandId = brandIdVal,
                                    categoryId = categoryIdVal
                                )
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainRepository", "Error parsing all products item: ${e.message}")
                    }
                }
                _allProducts.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load all products: ${error.message}")
            }
        })
    }

    fun searchProductsByTitlePrefix(queryText: String) {
        val q = queryText.trim()
        if (q.isEmpty()) {
            _searchResults.value = mutableListOf()
            return
        }
        val ref = firebaseDatabase.getReference("products")
        val query = ref.orderByChild("title").startAt(q).endAt(q + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (child in snapshot.children) {
                    try {
                        val title = child.child("title").getValue(String::class.java) ?: ""
                        val description = child.child("description").getValue(String::class.java) ?: ""
                        val price = child.child("price").getValue(Double::class.java) ?: 0.0
                        val oldPrice = child.child("oldPrice").getValue(Double::class.java) ?: 0.0
                        val thumbnail = child.child("thumbnail").getValue(String::class.java) ?: ""
                        val active = child.child("active").getValue(Boolean::class.java) ?: true
                        val rating = child.child("rating").getValue(Double::class.java) ?: 0.0
                        val brandIdVal = child.child("brandId").getValue(String::class.java) ?: ""
                        val categoryIdVal = child.child("categoryId").getValue(String::class.java) ?: ""

                        val picUrlList = arrayListOf<String>()
                        child.child("picUrl").children.forEach { it.getValue(String::class.java)?.let(picUrlList::add) }
                        if (picUrlList.isEmpty() && thumbnail.isNotEmpty()) picUrlList.add(thumbnail)

                        val sizeList = arrayListOf<String>()
                        child.child("size").children.forEach { it.getValue(String::class.java)?.let(sizeList::add) }

                        val colorList = arrayListOf<String>()
                        child.child("color").children.forEach { it.getValue(String::class.java)?.let(colorList::add) }

                        if (active && title.isNotEmpty()) {
                            list.add(
                                ItemModel(
                                    title = title,
                                    description = description,
                                    picUrl = picUrlList,
                                    size = sizeList,
                                    color = colorList,
                                    price = price,
                                    oldPrice = oldPrice,
                                    rating = rating,
                                    numberInCart = 1,
                                    brandId = brandIdVal,
                                    categoryId = categoryIdVal
                                )
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainRepository", "Error parsing search item: ${e.message}")
                    }
                }
                _searchResults.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to search products: ${error.message}")
            }
        })
    }

    fun loadProductsByBrand(brandId: String) {
        val ref = firebaseDatabase.getReference("products")
        val query = ref.orderByChild("brandId").equalTo(brandId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (child in snapshot.children) {
                    try {
                        val title = child.child("title").getValue(String::class.java) ?: ""
                        val description = child.child("description").getValue(String::class.java) ?: ""
                        val price = child.child("price").getValue(Double::class.java) ?: 0.0
                        val oldPrice = child.child("oldPrice").getValue(Double::class.java) ?: 0.0
                        val thumbnail = child.child("thumbnail").getValue(String::class.java) ?: ""
                        val active = child.child("active").getValue(Boolean::class.java) ?: true
                        val rating = child.child("rating").getValue(Double::class.java) ?: 0.0
                        val brandIdVal = child.child("brandId").getValue(String::class.java) ?: ""
                        val categoryIdVal = child.child("categoryId").getValue(String::class.java) ?: ""

                        val picUrlList = arrayListOf<String>()
                        child.child("picUrl").children.forEach { it.getValue(String::class.java)?.let(picUrlList::add) }
                        if (picUrlList.isEmpty() && thumbnail.isNotEmpty()) picUrlList.add(thumbnail)

                        val sizeList = arrayListOf<String>()
                        child.child("size").children.forEach { it.getValue(String::class.java)?.let(sizeList::add) }

                        val colorList = arrayListOf<String>()
                        child.child("color").children.forEach { it.getValue(String::class.java)?.let(colorList::add) }

                        if (active && title.isNotEmpty()) {
                            list.add(
                                ItemModel(
                                    title = title,
                                    description = description,
                                    picUrl = picUrlList,
                                    size = sizeList,
                                    color = colorList,
                                    price = price,
                                    oldPrice = oldPrice,
                                    rating = rating,
                                    numberInCart = 1,
                                    brandId = brandIdVal,
                                    categoryId = categoryIdVal
                                )
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainRepository", "Error parsing brand items: ${e.message}")
                    }
                }
                _filteredProducts.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load products by brand: ${error.message}")
            }
        })
    }

    fun loadProductsByCategory(categoryId: String) {
        val ref = firebaseDatabase.getReference("products")
        val query = ref.orderByChild("categoryId").equalTo(categoryId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (child in snapshot.children) {
                    try {
                        val title = child.child("title").getValue(String::class.java) ?: ""
                        val description = child.child("description").getValue(String::class.java) ?: ""
                        val price = child.child("price").getValue(Double::class.java) ?: 0.0
                        val oldPrice = child.child("oldPrice").getValue(Double::class.java) ?: 0.0
                        val thumbnail = child.child("thumbnail").getValue(String::class.java) ?: ""
                        val active = child.child("active").getValue(Boolean::class.java) ?: true
                        val rating = child.child("rating").getValue(Double::class.java) ?: 0.0
                        val brandIdVal = child.child("brandId").getValue(String::class.java) ?: ""
                        val categoryIdVal = child.child("categoryId").getValue(String::class.java) ?: ""

                        val picUrlList = arrayListOf<String>()
                        child.child("picUrl").children.forEach { it.getValue(String::class.java)?.let(picUrlList::add) }
                        if (picUrlList.isEmpty() && thumbnail.isNotEmpty()) picUrlList.add(thumbnail)

                        val sizeList = arrayListOf<String>()
                        child.child("size").children.forEach { it.getValue(String::class.java)?.let(sizeList::add) }

                        val colorList = arrayListOf<String>()
                        child.child("color").children.forEach { it.getValue(String::class.java)?.let(colorList::add) }

                        if (active && title.isNotEmpty()) {
                            list.add(
                                ItemModel(
                                    title = title,
                                    description = description,
                                    picUrl = picUrlList,
                                    size = sizeList,
                                    color = colorList,
                                    price = price,
                                    oldPrice = oldPrice,
                                    rating = rating,
                                    numberInCart = 1,
                                    brandId = brandIdVal,
                                    categoryId = categoryIdVal
                                )
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainRepository", "Error parsing category items: ${e.message}")
                    }
                }
                _filteredProducts.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load products by category: ${error.message}")
            }
        })
    }

    fun loadProductsByCategoryThenFilterBrand(categoryId: String, brandId: String) {
        val ref = firebaseDatabase.getReference("products")
        val query = ref.orderByChild("categoryId").equalTo(categoryId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (child in snapshot.children) {
                    try {
                        val title = child.child("title").getValue(String::class.java) ?: ""
                        val description = child.child("description").getValue(String::class.java) ?: ""
                        val price = child.child("price").getValue(Double::class.java) ?: 0.0
                        val oldPrice = child.child("oldPrice").getValue(Double::class.java) ?: 0.0
                        val thumbnail = child.child("thumbnail").getValue(String::class.java) ?: ""
                        val active = child.child("active").getValue(Boolean::class.java) ?: true
                        val rating = child.child("rating").getValue(Double::class.java) ?: 0.0
                        val brandIdVal = child.child("brandId").getValue(String::class.java) ?: ""
                        val categoryIdVal = child.child("categoryId").getValue(String::class.java) ?: ""

                        if (brandIdVal != brandId) continue

                        val picUrlList = arrayListOf<String>()
                        child.child("picUrl").children.forEach { it.getValue(String::class.java)?.let(picUrlList::add) }
                        if (picUrlList.isEmpty() && thumbnail.isNotEmpty()) picUrlList.add(thumbnail)

                        val sizeList = arrayListOf<String>()
                        child.child("size").children.forEach { it.getValue(String::class.java)?.let(sizeList::add) }

                        val colorList = arrayListOf<String>()
                        child.child("color").children.forEach { it.getValue(String::class.java)?.let(colorList::add) }

                        if (active && title.isNotEmpty()) {
                            list.add(
                                ItemModel(
                                    title = title,
                                    description = description,
                                    picUrl = picUrlList,
                                    size = sizeList,
                                    color = colorList,
                                    price = price,
                                    oldPrice = oldPrice,
                                    rating = rating,
                                    numberInCart = 1,
                                    brandId = brandIdVal,
                                    categoryId = categoryIdVal
                                )
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainRepository", "Error parsing filtered items: ${e.message}")
                    }
                }
                _filteredProducts.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load products by category+brand: ${error.message}")
            }
        })
    }

    private val _categories = MutableLiveData<MutableList<kh.sothun.darachhat.rupp.fe.ecommerce_app.model.CategoryModel>>()
    val categories: LiveData<MutableList<kh.sothun.darachhat.rupp.fe.ecommerce_app.model.CategoryModel>> get() = _categories

    fun loadCategories() {
        val ref = firebaseDatabase.getReference("categories")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<kh.sothun.darachhat.rupp.fe.ecommerce_app.model.CategoryModel>()
                for (child in snapshot.children) {
                    val id = child.key ?: ""
                    val name = child.child("name").getValue(String::class.java) ?: ""
                    val picUrl = child.child("picUrl").getValue(String::class.java) ?: ""
                    val active = child.child("active").getValue(Boolean::class.java) ?: true

                    if (active && name.isNotEmpty()) {
                        list.add(kh.sothun.darachhat.rupp.fe.ecommerce_app.model.CategoryModel(
                            id = id,
                            title = name,
                            picUrl = picUrl
                        ))
                    }
                }
                _categories.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainRepository", "Failed to load categories: ${error.message}")
            }
        })
    }
}
