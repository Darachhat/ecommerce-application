package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters.FavoriteAdapter
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityFavoriteBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.helpers.TinyDB
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.ItemModel

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var tinyDB: TinyDB
    private lateinit var auth: FirebaseAuth
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tinyDB = TinyDB(this)
        auth = FirebaseAuth.getInstance()
        
        // Check if user is logged in, if not redirect to login
        if (auth.currentUser == null) {
            Toast.makeText(
                this,
                "Please login to view your favorites",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        initializeAdapter()
        setupViews()
        loadFavorites()
    }

    private fun initializeAdapter() {
        favoriteAdapter = FavoriteAdapter(mutableListOf()) { item, position ->
            removeFromFavorites(item, position)
        }
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun setupViews() {
        binding.apply {
            backBtn.setOnClickListener {
                finish()
            }

            favoriteView.layoutManager = LinearLayoutManager(this@FavoriteActivity)
            favoriteView.adapter = favoriteAdapter
        }
    }

    private fun removeFromFavorites(item: ItemModel, position: Int) {
        val favoriteList = getFavoriteList()
        favoriteList.removeAll { it.title == item.title }
        tinyDB.putListObject("FavoriteList", ArrayList(favoriteList))
        
        favoriteAdapter.removeItem(position)
        
        Toast.makeText(
            this,
            "Removed from favorites",
            Toast.LENGTH_SHORT
        ).show()
        
        // Update UI if list is now empty
        if (favoriteList.isEmpty()) {
            loadFavorites()
        } else {
            binding.itemCountTxt.text = favoriteList.size.toString()
        }
    }

    private fun loadFavorites() {
        val favoriteList = getFavoriteList()
        
        binding.apply {
            if (favoriteList.isEmpty()) {
                emptyStateLayout.visibility = View.VISIBLE
                favoriteView.visibility = View.GONE
                countBadge.visibility = View.GONE
            } else {
                emptyStateLayout.visibility = View.GONE
                favoriteView.visibility = View.VISIBLE
                countBadge.visibility = View.VISIBLE
                itemCountTxt.text = favoriteList.size.toString()
                favoriteAdapter.updateData(favoriteList)
            }
        }
    }

    private fun getFavoriteList(): MutableList<ItemModel> {
        return tinyDB.getListObject("FavoriteList") ?: mutableListOf()
    }
}
