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
                                    numberInCart = 1
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
}