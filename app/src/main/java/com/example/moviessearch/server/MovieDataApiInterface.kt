package com.example.moviessearch.server


import com.example.moviessearch.database.MoviesDataEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDataApiInterface {

    @GET("/3/movie/{type}")
    suspend fun getMovie(@Path("type") type : String, @Query("page") page: String, @Query("api_key") apiKey : String) : Response<MovieResponse>

    @GET("/3/movie/{id}")
    suspend fun getMovieDetails(@Path("id") id : String, @Query ("api_key") apiKey: String) : Response<MovieResponseDetails>

    @GET("/3/search/movie")
    suspend fun getSearchMovie(@Query("query") name: String, @Query("page") page: String, @Query("api_key") apiKey: String): Response<MovieResponse>

    @GET("/3/search/movie")
    suspend fun getUniqueSearchMovie(
        @Query("api_key") apiKey: String, @Query("id") name: String, @Query("year") year: String,
        @Query("id") movieId: String
    ): Response<MoviesDataEntity>
}