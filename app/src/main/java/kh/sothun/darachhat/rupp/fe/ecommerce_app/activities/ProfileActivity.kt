package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        updateUIBasedOnAuthState()
        setupViews()
    }

    private fun updateUIBasedOnAuthState() {
        val currentUser = auth.currentUser
        binding.apply {
            if (currentUser != null) {
                // User is logged in - show user info
                userNameTxt.text = currentUser.displayName ?: "User"
                userEmailTxt.text = currentUser.email ?: "user@example.com"
                
                // Show logout button
                logoutText.text = "Logout"
                logoutText.setTextColor(getColor(android.R.color.holo_red_dark))
                logoutIcon.setColorFilter(getColor(android.R.color.holo_red_dark))
                logoutArrow.setColorFilter(getColor(android.R.color.holo_red_dark))
            } else {
                // User is not logged in - show guest info
                userNameTxt.text = "Guest User"
                userEmailTxt.text = "Not logged in"
                
                // Show login button
                logoutText.text = "Login / Sign Up"
                logoutText.setTextColor(getColor(com.google.android.material.R.color.design_default_color_primary))
                logoutIcon.setColorFilter(getColor(com.google.android.material.R.color.design_default_color_primary))
                logoutArrow.setColorFilter(getColor(com.google.android.material.R.color.design_default_color_primary))
            }
        }
    }

    private fun setupViews() {
        binding.apply {
            // Back button
            backBtn.setOnClickListener {
                finish()
            }

            // Edit Profile
            editProfileCard.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, EditProfileActivity::class.java))
            }

            // Change Password
            changePasswordCard.setOnClickListener {
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    android.widget.Toast.makeText(
                        this@ProfileActivity,
                        "Please login to change password",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                } else {
                    startActivity(Intent(this@ProfileActivity, ChangePasswordActivity::class.java))
                }
            }

            // Orders
            ordersCard.setOnClickListener {
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Please login to view your orders",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                } else {
                    startActivity(Intent(this@ProfileActivity, MyOrdersActivity::class.java))
                }
            }

            // Logout or Login
            logoutCard.setOnClickListener {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    showLogoutDialog()
                } else {
                    // Navigate to login
                    startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                auth.signOut()
                showToast("Logged out successfully")
                // Navigate to dashboard
                startActivity(Intent(this, DashboardActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
