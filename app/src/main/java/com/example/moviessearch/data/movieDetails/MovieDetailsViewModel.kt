package com.example.moviessearch.data.movieDetails

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviessearch.database.MoviesDataEntity
import com.example.moviessearch.repositories.RepositoryMovieDetailsData
import com.example.moviessearch.server.MovieResponseDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repositoryMovieDetailsData: RepositoryMovieDetailsData
) : ViewModel() {
    private val movieDetails : MutableLiveData<MovieResponseDetails> by lazy { MutableLiveData<MovieResponseDetails>() }
    val _moviesDetialLiveData = movieDetails
    private val movieBookmarkDetails: MutableLiveData<MoviesDataEntity> by lazy { MutableLiveData<MoviesDataEntity>() }
    val _movieBookmarkDetailsLiveData = movieBookmarkDetails

    fun getMovieDetails(movieId : String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repositoryMovieDetailsData.getMovieDetail(movieId).collect {
                    movieDetails.postValue(it)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "failed to fetch movie details $e")
        }

    }

    fun getMovieBookmarkDetails(movieId: Int) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                movieBookmarkDetails.postValue(repositoryMovieDetailsData.getMovieBookmarkInfo(movieId))
            }
        } catch (e: Exception) {
            Log.e(TAG,"failed to fetch bookmarked movie $e")
        }

    }

    fun getNewMovieBookmark(movieId: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repositoryMovieDetailsData.getMovieDetail(movieId).collect {
                    val movie = MoviesDataEntity(
                        movieId.toInt(), it.original_title, it.release_date, it.poster_path,
                        it.vote_average.toFloat(),false
                    )
                    repositoryMovieDetailsData.insertMovie(movie)
                }
            }
        } catch (e: Exception){
            Log.e(TAG,"failed to bookmark the movie $e")
        }

    }

    fun updateMovieBookmark(movie: MoviesDataEntity) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repositoryMovieDetailsData.updateMovieBookmarkInfo(movie)
            }
        } catch (e: Exception) {
            Log.e(TAG, "failed to updating bookmark")
        }

    }

    companion object {
        private const val TAG = "MovieDetailsViewModel"
    }
}