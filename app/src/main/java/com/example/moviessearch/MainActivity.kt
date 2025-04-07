package com.example.moviessearch

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviessearch.adapters.MoviesAdapter
import com.example.moviessearch.data.bookmark.BookMarkActivity
import com.example.moviessearch.data.search.SearchMoviesActivity
import com.example.moviessearch.database.MoviesDataEntity
import com.example.moviessearch.databinding.ActivityMainBinding
import com.example.moviessearch.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var moviesViewModel : MovieViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var moviesAdapter: MoviesAdapter

    private var currentPage = 1
    private var totalPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moviesViewModel = ViewModelProvider(this)[MovieViewModel::class.java]

        binding.movieRecyclerView.layoutManager = GridLayoutManager(this,3,
            GridLayoutManager.VERTICAL,false)
        moviesAdapter = MoviesAdapter(mutableListOf(), applicationContext)

        lifecycleScope.launch {
            getRecyclerViewData(R.id.popular_tab)
        }

        var prevView: View = binding.popularTab
        binding.popularTab.setOnClickListener {
            setCurrentButton(it, prevView)
            prevView = it
        }

        binding.nowPlayingTab.setOnClickListener {
            setCurrentButton(it, prevView)
            prevView = it
        }

        moviesViewModel._livedata.observe(this) {
            loadMoviesToAdapter(it.first, it.second)
        }

        /*binding.movieRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && !isLoading.get() && currentPage < totalPage) {
                    currentPage++
                    lifecycleScope.launch { getRecyclerViewData(recyclerView.id) }
                }
            }
        })*/
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private suspend fun getRecyclerViewData(id : Int){
        val type = when(id){
            R.id.popular_tab ->  Constants.POPULAR
            else -> Constants.NOW_PLAYING
        }
        repeat(5) {
            moviesViewModel.getMovies(type, currentPage)
            delay(500)
            currentPage++
        }
    }

    private fun setCurrentButton(view: View, prevView: View) {
        currentPage = 1
        lifecycleScope.launch { getRecyclerViewData(view.id) }
        view.setBackgroundResource(R.drawable.home_button_set)
        prevView.setBackgroundResource(R.drawable.home_button)
    }

    private fun loadMoviesToAdapter(moviesDataEntityList: List<MoviesDataEntity>, total: Int) {
        totalPage = total
        if(currentPage == 1) {
            binding.movieRecyclerView.adapter = moviesAdapter
        } else {
            if(::moviesAdapter.isInitialized) moviesAdapter.addMovies(moviesDataEntityList)
        }
    }
}
