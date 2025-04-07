package com.example.moviessearch.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MoviesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: MoviesDataEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMovies(movies : List<MoviesDataEntity>)

    @Query("SELECT * FROM MoviesDataEntity ORDER BY id ASC")
    suspend fun getAllPopularMovies() : List<MoviesDataEntity>

    @Query("SELECT * FROM MoviesDataEntity WHERE id = :movieId LIMIT 1")
    suspend fun getBookMarkMovie(movieId: Int): MoviesDataEntity

    @Query("SELECT * FROM MoviesDataEntity WHERE bookmark = 1")
    suspend fun getAllBookMarkMovies(): List<MoviesDataEntity>

    @Update
    suspend fun updateMovie(movie: MoviesDataEntity)
}