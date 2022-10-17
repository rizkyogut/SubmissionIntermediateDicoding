package com.example.storyapp.ui.auth.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.ui.auth.register.RegisterActivity
import com.example.storyapp.util.Result
import com.example.storyapp.util.UserPreferences
import com.example.storyapp.util.ViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    private var mShouldFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
    }

    override fun onStop() {
        super.onStop()
        if (mShouldFinish)
            finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]
        loginViewModel.login.observe(this){
            when (it) {
                is Result.Success -> {
                    showLoading(false)
                    startActivity(Intent(this, MainActivity::class.java))
                    mShouldFinish = true
                }
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun setupView() {
        with(binding) {
            registerTextView.setOnClickListener(this@LoginActivity)
            loginButton.setOnClickListener(this@LoginActivity)
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.registerTextView -> startActivity(Intent(this, RegisterActivity::class.java))
            binding.loginButton -> {
                if (binding.emailEditText.error == null && binding.passwordEditText.error == null && !binding.emailEditText.text.isNullOrEmpty() && !binding.passwordEditText.text.isNullOrEmpty()){
                    val email = binding.emailEditText.text.toString()
                    val password = binding.passwordEditText.text.toString()

                    loginViewModel.loginPost(email, password)
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.check_input),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}