package com.example.storyapp.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.ui.auth.login.LoginViewModel
import com.example.storyapp.ui.auth.register.RegisterViewModel
import com.example.storyapp.ui.main.MainViewModel
import com.example.storyapp.ui.main.addStories.AddStoriesViewModel

class ViewModelFactory(private val preferences: UserPreferences) :
    ViewModelProvider.NewInstanceFactory() {
    private lateinit var mApplication: Application

    fun setApplication(application: Application) {
        mApplication = application
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(preferences) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel() as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(preferences) as T
        }
        if (modelClass.isAssignableFrom(AddStoriesViewModel::class.java)) {
            return AddStoriesViewModel(preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}