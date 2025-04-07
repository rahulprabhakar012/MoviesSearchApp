package com.example.moviessearch.data.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager

import com.example.moviessearch.R
import com.example.moviessearch.adapters.MoviesAdapter
import com.example.moviessearch.data.bookmark.BookMarkActivity
import com.example.moviessearch.database.MoviesDataEntity
import com.example.moviessearch.databinding.SearchMoviesActivityBinding

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class SearchMoviesActivity: AppCompatActivity() {
    private lateinit var searchMoviesViewModel : SearchMovieViewModel
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var binding: SearchMoviesActivityBinding

    private var searchJob: Job? = null
    private val isLoading = AtomicBoolean(false)
    private var currentPage = 1
    private var totalPage = 1
    private var currentSearchMovie: String = ""
    private var prevSearchMovie: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SearchMoviesActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        searchMoviesViewModel = ViewModelProvider(this)[SearchMovieViewModel::class.java]

        binding.searchMoviesRecyclerView.layoutManager = GridLayoutManager(this,3, GridLayoutManager.VERTICAL,false)

        moviesAdapter = MoviesAdapter(mutableListOf(),applicationContext)
        binding.searchMovies.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch(Dispatchers.IO) {
                    currentSearchMovie = s.toString()
                    currentPage = 1
                    loadSearchMovies(currentSearchMovie)
                }
            }
        })

        searchMoviesViewModel._liveData.observe(this) {
            it?.let { loadMoviesToAdapter(it.first, it.second) }
            prevSearchMovie = currentSearchMovie
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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

    private suspend fun loadSearchMovies(movieName: String) {
        searchMoviesViewModel.getSearchMovies(movieName, currentPage)
        delay(500)
        currentPage++
    }

    private fun loadMoviesToAdapter(moviesDataEntityList: List<MoviesDataEntity>, total: Int) {
        totalPage = total
        isLoading.set(false)
        if(currentPage == 1 || prevSearchMovie != currentSearchMovie) {
            moviesAdapter.moviesDataEntityDataList = moviesDataEntityList.toMutableList()
            binding.searchMoviesRecyclerView.adapter = moviesAdapter
        } else {
            if(::moviesAdapter.isInitialized) moviesAdapter.addMovies(moviesDataEntityList)
        }
    }
}