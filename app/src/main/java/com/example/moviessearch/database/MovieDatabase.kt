package com.example.moviessearch.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MoviesDataEntity::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun getMoviesDao() : MoviesDao
}