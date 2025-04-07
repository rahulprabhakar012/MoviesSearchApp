package com.example.moviessearch.server

import com.example.moviessearch.database.MoviesDataEntity


data class MovieResponse(
    val results: List<MoviesDataEntity>,
    val total_pages: Int,
    val page: Int
)