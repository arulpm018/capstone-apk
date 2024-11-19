package com.example.myapplication
import AuthViewModel
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.example.myapplication.databinding.SignupActivityBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: SignupActivityBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observing AuthState for updates
        authViewModel.authState.observe(this) { authState ->
            when (authState) {
                is AuthViewModel.AuthState.Loading -> {
                }
                is AuthViewModel.AuthState.Success -> {
                    Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }
                is AuthViewModel.AuthState.Error -> {
                    Snackbar.make(binding.root, authState.message, Snackbar.LENGTH_LONG).show()
                }

                else -> {}
            }
        }

        binding.signupButton.setOnClickListener {
            val fullName = binding.nameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.signUp(email, password, fullName)
            } else {
                Snackbar.make(binding.root, "Please fill in all fields", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.loginLink.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
