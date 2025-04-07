package com.example.moviessearch.data.bookmark

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviessearch.R
import com.example.moviessearch.adapters.MoviesAdapter
import com.example.moviessearch.data.search.SearchMoviesActivity
import com.example.moviessearch.databinding.BookmarkMoviesActivityBinding

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookMarkActivity: AppCompatActivity() {
    private lateinit var bookmarkMoviesViewModel : BookMarkViewModel
    private lateinit var binding: BookmarkMoviesActivityBinding
    private lateinit var moviesAdapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BookmarkMoviesActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bookmarkMoviesViewModel = ViewModelProvider(this)[BookMarkViewModel::class.java]
        binding.bookmarkMoviesRecyclerView.layoutManager = GridLayoutManager(this,3, GridLayoutManager.VERTICAL,false)
        moviesAdapter = MoviesAdapter(mutableListOf(), applicationContext)

        bookmarkMoviesViewModel.getAllMovies()
        bookmarkMoviesViewModel._bookmarkLiveData.observe(this) {
            it?.let {
                moviesAdapter.moviesDataEntityDataList = it.toMutableList()
                binding.bookmarkMoviesRecyclerView.adapter = moviesAdapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bookmark_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_movies -> {
                val intent = Intent(this, SearchMoviesActivity::class.java)
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
}