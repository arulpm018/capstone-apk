package com.example.myapplication

import AuthViewModel
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityLoginBinding
import android.content.Intent
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    // View binding to access UI elements easily
    private lateinit var binding: ActivityLoginBinding

    // ViewModel for handling authentication logic
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the link to navigate to the Signup screen
        val signupLink = findViewById<TextView>(R.id.signupLink)
        signupLink.setOnClickListener {
            navigateToSignup()
        }

        // Initialize UI interactions and observe authentication states
        setupViews()
        observeAuthState()
    }

    /**
     * Set up user interactions like login button behavior.
     */
    private fun setupViews() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim() // Remove extra spaces
            val password = binding.passwordInput.text.toString().trim()

            // Validate input before proceeding with login
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(email, password)
            }
        }
    }

    /**
     * Observe authentication state from ViewModel and update UI accordingly.
     */
    private fun observeAuthState() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    // Show loading state and disable the login button
                    binding.loginButton.isEnabled = false
                }

                is AuthViewModel.AuthState.Success -> {
                    // Navigate to MainActivity on successful login
                    navigateToMainActivity()
                }

                is AuthViewModel.AuthState.Error -> {
                    // Display the error message and re-enable the login button
                    binding.loginButton.isEnabled = true
                    showToast(state.message)
                }

                else -> {
                    // Handle unexpected states if necessary
                    binding.loginButton.isEnabled = true
                }
            }
        }
    }

    /**
     * Navigate to the Signup screen.
     */
    private fun navigateToSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    /**
     * Navigate to the MainActivity after a successful login.
     */
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity to prevent returning to it
    }

    /**
     * Show a toast message for feedback.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
