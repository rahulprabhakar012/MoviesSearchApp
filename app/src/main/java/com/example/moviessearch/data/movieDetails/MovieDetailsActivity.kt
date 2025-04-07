package com.example.moviessearch.data.movieDetails

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.moviessearch.R
import com.example.moviessearch.data.bookmark.BookMarkActivity
import com.example.moviessearch.data.search.SearchMoviesActivity
import com.example.moviessearch.databinding.ActivityMovieDetailsBinding
import com.example.moviessearch.server.MovieResponseDetails
import com.example.moviessearch.utils.Constants

import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var movieViewModel : MovieDetailsViewModel
    private lateinit var movieId: String
    private lateinit var movieResponseDetails: MovieResponseDetails
    private lateinit var binding: ActivityMovieDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MovieDetailsActivity", "onCreate()")
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        movieViewModel = ViewModelProvider(this)[MovieDetailsViewModel::class.java]
        movieId = intent.getStringExtra("id").toString()

        var isFirst = true
        var isBookmarked = false

        movieViewModel.getMovieBookmarkDetails(movieId.toInt())
        binding.bookmark.setOnClickListener {
            isBookmarked = !isBookmarked
            movieViewModel.getMovieBookmarkDetails(movieId.toInt())
        }

        movieViewModel._movieBookmarkDetailsLiveData.observe(this) {
            it?.let { movie ->
                if (isFirst) {
                    if (movie.bookmark) {
                        binding.bookmark.setBackgroundResource(R.drawable.baseline_bookmark_24)
                    } else {
                        binding.bookmark.setBackgroundResource(R.drawable.baseline_bookmark_border_24)
                    }
                } else {
                    if (movie.bookmark && !isBookmarked) {
                        binding.bookmark.setBackgroundResource(R.drawable.baseline_bookmark_border_24)
                        movie.bookmark = false
                    }
                    else if (!movie.bookmark && isBookmarked) {
                        binding.bookmark.setBackgroundResource(R.drawable.baseline_bookmark_24)
                        movie.bookmark = true
                    }
                }
                isFirst = false
                movieViewModel.updateMovieBookmark(movie)
            } ?: movieViewModel.getNewMovieBookmark(movieId)
        }

        movieViewModel.getMovieDetails(movieId)
        movieViewModel._moviesDetialLiveData.observe(this) {
            it?.let {
                movieResponseDetails = it
                Glide.with(application)
                    .load(Constants.BASE_IMAGE_URL + it.backdrop_path)
                    .into(binding.backdropPath)

                Glide.with(application)
                    .load(Constants.BASE_IMAGE_URL + it.poster_path)
                    .into(binding.posterPath)

                binding.movieName.text = it.original_title
                binding.overviewDetail.text = it.overview
                binding.statusDetails.text = it.status
                binding.releaseDateDetails.text = it.release_date
                binding.voteAverageDetail.text = it.vote_average.toString()
            }
        }

        binding.share.setOnClickListener {
            if (::movieResponseDetails.isInitialized) {
                sendMovieData(movieResponseDetails)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                val intent = Intent(this, SearchMoviesActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.action_bookmark -> {
                val intent = Intent(this, BookMarkActivity::class.java)
                startActivity(intent)
                true
            }

            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendMovieData(movieResponseDetails: MovieResponseDetails) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        val data = Gson().toJson(movieResponseDetails)
        shareIntent.putExtra(Intent.EXTRA_TEXT, data)
        startActivity(Intent.createChooser(shareIntent, "Share Movie"))
    }
}
