package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

            // Login button
            loginButton.setOnClickListener {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString()

                if (validateInput(email, password)) {
                    loginUser(email, password)
                }
            }

            // Sign up link
            signUpTxt.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                finish()
            }

            // Forgot password
            forgotPasswordTxt.setOnClickListener {
                val email = emailEditText.text.toString().trim()
                if (email.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Please enter your email first", Toast.LENGTH_SHORT).show()
                } else {
                    resetPassword(email)
                }
            }

            // Social login buttons
            googleLoginBtn.setOnClickListener {
                Toast.makeText(this@LoginActivity, "Google Sign-In coming soon", Toast.LENGTH_SHORT).show()
            }

            facebookLoginBtn.setOnClickListener {
                Toast.makeText(this@LoginActivity, "Facebook Sign-In coming soon", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
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

        return true
    }

    private fun loginUser(email: String, password: String) {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false

            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { checkTask ->
                    val methods = if (checkTask.isSuccessful) {
                        checkTask.result?.signInMethods ?: emptyList()
                    } else emptyList()
                    if (methods.isEmpty()) {
                        // Advisory only; still try sign-in in case of transient issues or newly created accounts
                        Toast.makeText(this@LoginActivity, "Checking account...", Toast.LENGTH_SHORT).show()
                    } else if (!methods.contains("password")) {
                        Toast.makeText(this@LoginActivity, "Account may use a different sign-in method", Toast.LENGTH_SHORT).show()
                    }

                    Log.d("LoginActivity", "Starting signInWithEmailAndPassword for $email")
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            Log.d("LoginActivity", "signIn complete: ${task.isSuccessful}")
                            progressBar.visibility = View.GONE
                            loginButton.isEnabled = true

                            if (task.isSuccessful) {
                                Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                val friendly = mapAuthError(task.exception)
                                Toast.makeText(this@LoginActivity, friendly, Toast.LENGTH_LONG).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            progressBar.visibility = View.GONE
                            loginButton.isEnabled = true
                            val friendly = mapAuthError(e)
                            Toast.makeText(this@LoginActivity, friendly, Toast.LENGTH_LONG).show()
                        }
                        .addOnCanceledListener {
                            progressBar.visibility = View.GONE
                            loginButton.isEnabled = true
                            Toast.makeText(this@LoginActivity, "Login canceled", Toast.LENGTH_LONG).show()
                        }
                }
        }
    }

    private fun mapAuthError(e: Exception?): String {
        val msg = e?.message ?: "Authentication error"
        return when {
            msg.contains("password is invalid", ignoreCase = true) -> "Incorrect password"
            msg.contains("no user record", ignoreCase = true) -> "No account found for this email"
            msg.contains("malformed", ignoreCase = true) -> "Invalid credentials. Please re-enter your email and password"
            else -> "Login failed: $msg"
        }
    }

    private fun resetPassword(email: String) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { t ->
                val methods = t.result?.signInMethods ?: emptyList()
                if (!methods.contains("password")) {
                    Toast.makeText(this@LoginActivity, "This account does not use a password", Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener
                }
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@LoginActivity, "Password reset email sent to $email", Toast.LENGTH_LONG).show()
                        } else {
                            val friendly = mapAuthError(task.exception)
                            Toast.makeText(this@LoginActivity, friendly, Toast.LENGTH_LONG).show()
                        }
                    }
            }
    }
}
