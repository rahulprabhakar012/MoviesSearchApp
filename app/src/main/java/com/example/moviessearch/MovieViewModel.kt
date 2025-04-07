package com.example.moviessearch

import android.util.Log
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
class MovieViewModel @Inject constructor( private val repository: RepositoryData) : ViewModel() {
    private val moviesDataEntity : MutableLiveData<Pair<List<MoviesDataEntity>, Int>> by lazy { MutableLiveData<Pair<List<MoviesDataEntity>, Int>>() }
    val _livedata = moviesDataEntity

    fun getMovies(type : String, page: Int) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repository.getMovies(type, page.toString()).collect {
                    moviesDataEntity.postValue(it)
                }
            }
        } catch (e: Exception){
            Log.e(TAG, "Exception occurred in fetching movies data $e")
        }
    }

    companion object {
        private const val TAG = "MovieViewModel"
    }
}