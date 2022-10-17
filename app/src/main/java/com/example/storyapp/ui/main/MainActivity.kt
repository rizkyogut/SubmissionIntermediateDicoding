package com.example.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.PagingViewModel
import com.example.storyapp.ui.auth.login.LoginActivity
import com.example.storyapp.ui.main.addStories.AddStoriesActivity
import com.example.storyapp.ui.main.maps.MapsActivity
import com.example.storyapp.util.GridItem
import com.example.storyapp.util.Result
import com.example.storyapp.util.UserPreferences
import com.example.storyapp.util.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private var storyAdapter = ListStoryAdapter()
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")

    private val pagingViewModel: PagingViewModel by viewModels {
        PagingViewModel.ViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_languages -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.menu_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                mainViewModel.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                Toast.makeText(this@MainActivity,
                    getString(R.string.logout_alert),
                    Toast.LENGTH_SHORT).show()
                true
            }
            else -> true
        }
    }

    private fun setupViewModel() {
        val preferences = UserPreferences.getInstance(dataStore)
        val viewModelFactory = ViewModelFactory(preferences)
        viewModelFactory.setApplication(application)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        mainViewModel.stories.observe(this) {
            when (it) {
                is Result.Success -> {
                    it.data?.let {
                        dataStories(preferences)
                    }
                    showLoading(false)
                }
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }

        binding.addStory.setOnClickListener {
            startActivity(Intent(this, AddStoriesActivity::class.java))
        }

        fetchData()
    }

    private fun dataStories(preferences: UserPreferences) {
        mainViewModel.getUserKey().observe(this) {
            pagingViewModel.getPagingStories(preferences).observe(this) { data ->
                storyAdapter.submitData(lifecycle, data)
            }
        }
    }

    private fun setupView() {
        with(binding.rvStories) {
            adapter = storyAdapter
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            addItemDecoration(GridItem(2, 16, true))
        }
    }


    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            mainViewModel.getAllStories()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
