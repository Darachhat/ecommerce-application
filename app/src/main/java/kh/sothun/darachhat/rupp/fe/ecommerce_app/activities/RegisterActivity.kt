package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            // Back button
            backBtn.setOnClickListener {
                finish()
            }

            // Register button
            registerButton.setOnClickListener {
                val name = nameEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                if (validateInput(name, email, password, confirmPassword)) {
                    registerUser(name, email, password)
                }
            }

            // Login link
            loginTxt.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }

            // Social sign up buttons
            googleSignUpBtn.setOnClickListener {
                Toast.makeText(this@RegisterActivity, "Google Sign-Up coming soon", Toast.LENGTH_SHORT).show()
            }

            facebookSignUpBtn.setOnClickListener {
                Toast.makeText(this@RegisterActivity, "Facebook Sign-Up coming soon", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            binding.nameEditText.error = "Full name is required"
            binding.nameEditText.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            binding.emailEditText.error = "Email is required"
            binding.emailEditText.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "Please enter a valid email"
            binding.emailEditText.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.passwordEditText.error = "Password is required"
            binding.passwordEditText.requestFocus()
            return false
        }

        if (password.length < 6) {
            binding.passwordEditText.error = "Password must be at least 6 characters"
            binding.passwordEditText.requestFocus()
            return false
        }

        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordEditText.error = "Please confirm your password"
            binding.confirmPasswordEditText.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            binding.confirmPasswordEditText.error = "Passwords do not match"
            binding.confirmPasswordEditText.requestFocus()
            return false
        }

        if (!binding.termsCheckbox.isChecked) {
            Toast.makeText(this, "Please accept the Terms & Conditions", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun registerUser(name: String, email: String, password: String) {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            registerButton.isEnabled = false

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@RegisterActivity) { task ->
                    if (task.isSuccessful) {
                        // Update user profile with display name
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    // Save user data to Realtime Database
                                    val userId = user.uid
                                    val userMap = hashMapOf(
                                        "email" to email,
                                        "role" to "user",
                                        "createdAt" to (System.currentTimeMillis() / 1000)
                                    )
                                    
                                    FirebaseDatabase.getInstance().getReference("users")
                                        .child(userId)
                                        .setValue(userMap)
                                        .addOnCompleteListener { databaseTask ->
                                            progressBar.visibility = View.GONE
                                            registerButton.isEnabled = true
                                            
                                            if (databaseTask.isSuccessful) {
                                                Toast.makeText(
                                                    this@RegisterActivity,
                                                    "Registration successful! Welcome, $name",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    this@RegisterActivity,
                                                    "Account created but profile setup incomplete",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            
                                            // Navigate to dashboard
                                            startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java).apply {
                                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            })
                                            finish()
                                        }
                                } else {
                                    progressBar.visibility = View.GONE
                                    registerButton.isEnabled = true
                                    
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Profile update failed, but account created",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    
                                    // Still navigate to dashboard
                                    startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    })
                                    finish()
                                }
                            }
                    } else {
                        progressBar.visibility = View.GONE
                        registerButton.isEnabled = true
                        
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}
