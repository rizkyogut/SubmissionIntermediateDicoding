package com.example.storyapp.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.data.model.StoriesResponse
import com.example.storyapp.util.Result
import com.example.storyapp.util.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val preferences: UserPreferences): ViewModel() {
    private val _stories = MutableLiveData<Result<ArrayList<ListStoryItem>>>()
    val stories: LiveData<Result<ArrayList<ListStoryItem>>> = _stories

     suspend fun getAllStories() {
        _stories.postValue(Result.Loading())
        val client = ApiConfig.getApiService().getStories(token = "Bearer ${preferences.getUserKey().first()}",1)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>,
            ) {
                val responseBody = response.body()?.listStory
                if (response.isSuccessful && responseBody != null) {
                    _stories.postValue(Result.Success(ArrayList(responseBody)))
                } else {
                    _stories.postValue(Result.Error(response.message().toString()))
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                Log.d("MainViewModel", "onFailure: getAllStories")
                _stories.postValue(Result.Error(t.message))
            }
        })
    }

    fun logout() = deleteUserKey()

    private fun deleteUserKey(){
        viewModelScope.launch {
            preferences.deleteUserKey()
        }
    }

    fun getUserKey() = preferences.getUserKey().asLiveData()
}