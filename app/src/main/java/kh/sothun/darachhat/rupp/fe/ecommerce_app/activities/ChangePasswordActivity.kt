package kh.sothun.darachhat.rupp.fe.ecommerce_app.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnChangePassword.setOnClickListener { handleChangePassword() }
    }

    private fun handleChangePassword() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_LONG).show()
            return
        }

        val current = binding.editCurrentPassword.text?.toString()?.trim() ?: ""
        val newPass = binding.editNewPassword.text?.toString()?.trim() ?: ""
        val confirm = binding.editConfirmPassword.text?.toString()?.trim() ?: ""

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPass.length < 6) {
            Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPass != confirm) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val email = user.email
        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "Password change not available for this account", Toast.LENGTH_LONG).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        val credential = EmailAuthProvider.getCredential(email, current)
        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (!reauthTask.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_LONG).show()
                return@addOnCompleteListener
            }

            user.updatePassword(newPass).addOnCompleteListener { updateTask ->
                binding.progressBar.visibility = View.GONE
                if (updateTask.isSuccessful) {
                    Toast.makeText(this, "Password updated", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update password", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

