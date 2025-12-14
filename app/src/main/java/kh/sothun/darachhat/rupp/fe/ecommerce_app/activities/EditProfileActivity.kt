package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        loadUserData()
        setupUI()
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.apply {
            // Load basic Firebase Auth data
            nameEditText.setText(currentUser.displayName ?: "")
            emailEditText.setText(currentUser.email ?: "")

            // Load additional data from Realtime Database
            val userId = currentUser.uid
            database.getReference("users").child(userId).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        phoneEditText.setText(snapshot.child("phone").value?.toString() ?: "")
                        addressEditText.setText(snapshot.child("address").value?.toString() ?: "")
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Failed to load profile data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun setupUI() {
        binding.apply {
            // Back button
            backBtn.setOnClickListener {
                finish()
            }

            // Change photo button
            changePhotoBtn.setOnClickListener {
                Toast.makeText(this@EditProfileActivity, "Photo upload coming soon", Toast.LENGTH_SHORT).show()
            }

            // Save button
            saveButton.setOnClickListener {
                val name = nameEditText.text.toString().trim()
                val phone = phoneEditText.text.toString().trim()
                val address = addressEditText.text.toString().trim()

                if (validateInput(name)) {
                    saveProfile(name, phone, address)
                }
            }
        }
    }

    private fun validateInput(name: String): Boolean {
        if (name.isEmpty()) {
            binding.nameEditText.error = "Name is required"
            binding.nameEditText.requestFocus()
            return false
        }
        return true
    }

    private fun saveProfile(name: String, phone: String, address: String) {
        val currentUser = auth.currentUser ?: return

        binding.apply {
            progressBar.visibility = View.VISIBLE
            saveButton.isEnabled = false

            // Update Firebase Auth display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        // Save additional data to Realtime Database
                        val userMap = hashMapOf(
                            "name" to name,
                            "email" to currentUser.email,
                            "phone" to phone,
                            "address" to address,
                            "updatedAt" to System.currentTimeMillis()
                        )

                        database.getReference("users").child(currentUser.uid)
                            .updateChildren(userMap as Map<String, Any>)
                            .addOnSuccessListener {
                                progressBar.visibility = View.GONE
                                saveButton.isEnabled = true
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            .addOnFailureListener { dbError ->
                                progressBar.visibility = View.GONE
                                saveButton.isEnabled = true
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "Failed to save profile: ${dbError.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    } else {
                        progressBar.visibility = View.GONE
                        saveButton.isEnabled = true
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Failed to update name: ${authTask.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}
