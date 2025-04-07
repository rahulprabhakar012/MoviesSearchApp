package com.example.moviessearch.data.search

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
class SearchMovieViewModel @Inject constructor(private val repositoryData: RepositoryData) : ViewModel() {
    private val movieResults : MutableLiveData<Pair<List<MoviesDataEntity>, Int>> by lazy { MutableLiveData<Pair<List<MoviesDataEntity>, Int>>() }
    val _liveData = movieResults

    fun getSearchMovies(movieName: String, page: Int) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repositoryData.getSearchMovies(movieName, page.toString()).collect {
                    movieResults.postValue(it)
                }
            }
        } catch (e: Exception){
            Log.e(TAG, "Exception occurred in fetching searched movies data")
        }

    }

    companion object {
        private const val TAG = "SearchMovieViewModel"
    }
}