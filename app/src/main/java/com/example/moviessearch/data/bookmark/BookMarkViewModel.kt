package com.example.moviessearch.data.bookmark

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviessearch.database.MoviesDataEntity
import com.example.moviessearch.repositories.RepositoryData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookMarkViewModel @Inject constructor(private val repositoryData: RepositoryData) : ViewModel() {
    private val bookmarkMoviesDataEntity : MutableLiveData<List<MoviesDataEntity>> by lazy { MutableLiveData<List<MoviesDataEntity>>() }
    val _bookmarkLiveData = bookmarkMoviesDataEntity

    fun getAllMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkMoviesDataEntity.postValue(repositoryData.getAllBookMarkMovies())
        }
    }

}