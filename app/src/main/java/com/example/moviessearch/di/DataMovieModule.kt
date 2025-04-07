package com.example.moviessearch.di

import android.content.Context
import androidx.room.Room
import com.example.moviessearch.database.MovieDatabase
import com.example.moviessearch.repositories.RepositoryData
import com.example.moviessearch.repositories.RepositoryMovieDetailsData
import com.example.moviessearch.server.MovieDataApiInterface
import com.example.moviessearch.utils.Constants

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DataMovieModule {
    @Singleton
    @Provides
    fun provideRetrofitInterface(): MovieDataApiInterface {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(MovieDataApiInterface::class.java)
    }

    @Singleton
    @Provides
    fun bindUserDataBase(@ApplicationContext context: Context): MovieDatabase {
        return Room.databaseBuilder(
            context.applicationContext, MovieDatabase::class.java, "movie_db"
        ).build()
    }

    @Provides
    fun provideRepositoryData(movieDataApiInterface: MovieDataApiInterface, movieDataBase: MovieDatabase): RepositoryData {
        return RepositoryData(movieDataApiInterface, movieDataBase)
    }

    @Provides
    fun provideRepositoryDetailsData(movieDataApiInterface: MovieDataApiInterface, movieDataBase: MovieDatabase): RepositoryMovieDetailsData {
        return RepositoryMovieDetailsData(movieDataApiInterface, movieDataBase)
    }

}