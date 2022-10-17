package com.example.storyapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.ui.main.StoryPagingSource
import com.example.storyapp.util.UserPreferences

class PagingViewModel : ViewModel() {

    fun getPagingStories(preferences: UserPreferences): LiveData<PagingData<ListStoryItem>> = Pager(
        config = PagingConfig(
            pageSize = 5
        ),
        pagingSourceFactory = {
            StoryPagingSource(preferences)
        }
    ).liveData.cachedIn(viewModelScope)

    class ViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PagingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PagingViewModel() as T
            } else throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}