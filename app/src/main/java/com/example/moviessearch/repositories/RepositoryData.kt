package com.example.moviessearch.repositories

import android.util.Log
import com.example.moviessearch.database.MovieDatabase
import com.example.moviessearch.database.MoviesDataEntity
import com.example.moviessearch.server.MovieDataApiInterface
import com.example.moviessearch.utils.Constants
import com.example.moviessearch.utils.ErrorUtils.handleError

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class RepositoryData (private val movieDataApiInterface: MovieDataApiInterface, private val movieDatabase: MovieDatabase) {
    private lateinit var moviesDataEntityList : List<MoviesDataEntity>

    fun getMovies(movieType : String, page: String) = flow {
        moviesDataEntityList = getMoviesFromLocal()
        loadMoviesRemote(movieType, page)
            .catch {
                 handleError(it)
                if (::moviesDataEntityList.isInitialized) emit(moviesDataEntityList to 0)
            }
            .collect { emit(it) }
    }

    fun getSearchMovies(movieName : String, page: String) = flow {
        loadSearchMovies(movieName, page).catch {
            handleError(it)
            emit(listOf<MoviesDataEntity>() to 0)
        }.collect {
            emit(it)
        }
    }

    suspend fun getAllBookMarkMovies(): List<MoviesDataEntity> {
        return movieDatabase.getMoviesDao().getAllBookMarkMovies()
    }

    private suspend fun getMoviesFromLocal() : List<MoviesDataEntity>{
        return movieDatabase.getMoviesDao().getAllPopularMovies()
    }

    private fun loadMoviesRemote(movieType: String, page: String) = flow {
        val response = movieDataApiInterface.getMovie(movieType, page, Constants.API_KEY)
        if (response.isSuccessful && response.body() != null) {
            response.body()?.let {
                moviesDataEntityList = getMappedMovies(it.results)
                movieDatabase.getMoviesDao().insertAllMovies(moviesDataEntityList)
                emit(moviesDataEntityList to it.total_pages)
            }
        } else {
            Log.e(TAG, "Error loading movies : ${response.errorBody()}")
            emit(listOf<MoviesDataEntity>() to 0)
        }
    }

    private fun loadSearchMovies(movieName : String, page: String) = flow {
        val response = movieDataApiInterface.getSearchMovie(movieName, page, Constants.API_KEY)
        if (response.isSuccessful && response.body() != null) {
            response.body()?.let {
                emit(it.results to it.total_pages)
            }
        }else{
            Log.e(TAG, "Error searching movies : ${response.errorBody()}" )
        }
    }

    private fun getMappedMovies(movies : List<MoviesDataEntity>) : List<MoviesDataEntity>{
        val localMovies: MutableList<MoviesDataEntity> = mutableListOf()
        movies.forEach {
            localMovies.add(
                MoviesDataEntity(it.id,it.original_title,it.release_date,it.poster_path,it.vote_average)
            )
        }
        return localMovies
    }

    companion object {
        private const val TAG = "RepositoryData"
    }
}