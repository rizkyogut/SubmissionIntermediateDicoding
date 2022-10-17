package com.example.storyapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.databinding.ActivitySplashScreenBinding
import com.example.storyapp.ui.auth.login.LoginActivity
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.ui.main.MainViewModel

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        Handler(Looper.getMainLooper()).postDelayed({

            mainViewModel.getUserKey().observe(this){
                if(it.isNullOrEmpty()){
                    startActivity(Intent(this, LoginActivity::class.java))
                }else{
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            finish()
        }, SPLASH_DELAY)
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]
    }

    companion object {
        const val SPLASH_DELAY = 2000L
    }
}