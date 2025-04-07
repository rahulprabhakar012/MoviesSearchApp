package com.example.moviessearch.repositories

import android.util.Log
import com.example.moviessearch.database.MovieDatabase
import com.example.moviessearch.database.MoviesDataEntity
import com.example.moviessearch.server.MovieDataApiInterface
import com.example.moviessearch.server.MovieResponseDetails
import com.example.moviessearch.utils.Constants
import com.example.moviessearch.utils.ErrorUtils.handleError

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RepositoryMovieDetailsData @Inject constructor(
    private val movieDataApiInterface: MovieDataApiInterface, private val movieDatabase: MovieDatabase
) {
    fun getMovieDetail(movieId: String): Flow<MovieResponseDetails> = flow {
        try {
            val response = movieDataApiInterface.getMovieDetails(movieId, Constants.API_KEY)
            if (response.isSuccessful && response.body() != null) {
                emit(response.body()!!)
            } else {
                Log.e(TAG, "Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    suspend fun getMovieBookmarkInfo(movieId: Int): MoviesDataEntity {
        return movieDatabase.getMoviesDao().getBookMarkMovie(movieId)
    }


    suspend fun updateMovieBookmarkInfo(movie: MoviesDataEntity) {
        movieDatabase.getMoviesDao().updateMovie(movie)
    }

    suspend fun insertMovie(movie: MoviesDataEntity) {
        movieDatabase.getMoviesDao().insertMovie(movie)
    }

    companion object {
        private const val TAG = "RepositoryMovieDetailsData"
    }
}