package com.example.rickandmortymvvm.presentation.view

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.core.util.createNegativeDialog
import com.example.rickandmortymvvm.core.util.createSnackBar
import com.example.rickandmortymvvm.databinding.ActivityRickAndMortyBinding
import com.example.rickandmortymvvm.presentation.viewmodel.RickAndMortyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.HttpException


@AndroidEntryPoint
class RickAndMortyActivity : AppCompatActivity() {

    private var _binding: ActivityRickAndMortyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RickAndMortyViewModel by viewModels()
    private lateinit var rmAdapter: RickAndMortyAdapter
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRickAndMortyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            toggle = ActionBarDrawerToggle(
                this@RickAndMortyActivity,
                binding.drawerLayout,
                R.string.open,
                R.string.close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            setSupportActionBar(topUserAppBar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setNavigationDrawer()
        }

        updateScreenState()
    }

    private fun updateScreenState() {
        initRecyclerViewAndLoadStateAdapter()
        setupSearchView()
        collectRickAndMortyResults()
        addLoadStateListener()
        onSwipeBackPressed()
    }

    private fun setNavigationDrawer() = binding.apply {
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_account -> {
                    rvRmList.smoothScrollToPosition(0)
                    rmSearchView.clearFocus()
                    createSnackBar("Loading Website...", binding.root)

                    // navigate to Rick and Morty website
                    val uriString = "https://www.adultswim.com/videos/rick-and-morty/rick-and-morty"
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(uriString)
                    }
                    try { // error handling
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                    drawerLayout.close()
                }

                R.id.nav_share -> {
                    rvRmList.smoothScrollToPosition(0)
                    rmSearchView.clearFocus()
                    createSnackBar("Loading...", binding.root)

                    // share an email about the application
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_EMAIL, arrayOf(
                                "stefanbayne@gmail.com"
                            )
                        )
                        putExtra(Intent.EXTRA_SUBJECT, "Sharing application")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Please checkout my Rick and Morty application that I have created!"
                        )
                    }
                    // another approach to error handling with resolve activity
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                    drawerLayout.close()
                }

                R.id.nav_youtube -> {
                    rvRmList.smoothScrollToPosition(0)
                    rmSearchView.clearFocus()

                    // go to youtube
                    val youtubeString =
                        "https://www.youtube.com/results?search_query=rick+and+morty"
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(youtubeString)
                    }
                    try { // error handling
                        createSnackBar("Loading Youtube...", binding.root)
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }

                    drawerLayout.close()
                }

                R.id.nav_list -> {
                    rvRmList.smoothScrollToPosition(0)
                    rmSearchView.clearFocus()
                    val savedIntent =
                        Intent(this@RickAndMortyActivity, SavedActivity::class.java)
                    startActivity(savedIntent)
                    drawerLayout.close()
                }

                R.id.nav_male -> {
                    rvRmList.smoothScrollToPosition(0)
                    rmSearchView.clearFocus()
                    collectMaleData()
                    topUserAppBar.title = "Males"
                    drawerLayout.close()
                }

                R.id.nav_female -> {
                    rvRmList.smoothScrollToPosition(0)
                    rmSearchView.clearFocus()
                    collectFemaleData()
                    topUserAppBar.title = "Females"
                    drawerLayout.close()
                }

                R.id.nav_home -> {
                    val rickAndMortyList = listOf("abcdefgirls", "mnopfesdf")
                    rvRmList.smoothScrollToPosition(0)
                    viewModel.searchCharacterJob(
                        rickAndMortyList.subList(0, 1).random().first().toString()
                    )
                    // sets name of Home screen to user name entered in Login Activity
                    topUserAppBar.title =
                        "${getSharedPrefsData(this@RickAndMortyActivity)}'s Home Page"
                    createSnackBar("Going Home", binding.root)
                    drawerLayout.close()
                }
            }
            true
        }
    }

    // Using shared preferences from Login activity to populate Home page action bar title
    private fun getSharedPrefsData(context: Context?): String? {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        return sharedPreferences.getString(LoginActivity.LOGIN, null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collectRickAndMortyResults() = lifecycleScope.launch {
        binding.apply {
            viewModel.rickAndMortyResult.observe(this@RickAndMortyActivity) {
                try {
                    rmAdapter.notifyDataSetChanged()
                    rmAdapter.submitData(lifecycle, it)

                    rmAdapter.setOnItemClickListener {
                        val detailIntent =
                            Intent(
                                this@RickAndMortyActivity,
                                DetailsActivity::class.java
                            )
                        Bundle().apply {
                            detailIntent.putExtra(EXTRA_MAIN, it)
                        }
                        startActivity(detailIntent)
                        finish()
                    }

                    pbRm.visibility = View.GONE
                } catch (e: HttpException) {
                    createNegativeDialog(
                        "Error!",
                        "Error retrieving data! ${e.printStackTrace()}",
                        "Cancel"
                    )
                    pbRm.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collectMaleData() = lifecycleScope.launch {
        binding.apply {
            viewModel.maleGenderResult.observe(this@RickAndMortyActivity) {
                try {
                    rmAdapter.notifyDataSetChanged()
                    rmAdapter.submitData(lifecycle, it)

                    rmAdapter.setOnItemClickListener {
                        val detailIntent =
                            Intent(
                                this@RickAndMortyActivity,
                                DetailsActivity::class.java
                            )
                        Bundle().apply {
                            detailIntent.putExtra(EXTRA_MAIN, it)
                        }
                        startActivity(detailIntent)
                        finish()
                    }

                    pbRm.visibility = View.GONE
                } catch (e: HttpException) {
                    createNegativeDialog(
                        "Error!",
                        "Error retrieving data! ${e.printStackTrace()}",
                        "Cancel"
                    )
                    pbRm.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collectFemaleData() = lifecycleScope.launch {
        binding.apply {
            viewModel.femaleGenderResult.observe(this@RickAndMortyActivity) {
                try {
                    rmAdapter.notifyDataSetChanged()
                    rmAdapter.submitData(lifecycle, it)

                    rmAdapter.setOnItemClickListener {
                        val detailIntent =
                            Intent(
                                this@RickAndMortyActivity,
                                DetailsActivity::class.java
                            )
                        Bundle().apply {
                            detailIntent.putExtra(EXTRA_MAIN, it)
                        }
                        startActivity(detailIntent)
                        finish()
                    }

                    pbRm.visibility = View.GONE
                } catch (e: HttpException) {
                    createNegativeDialog(
                        "Error!",
                        "Error retrieving data! ${e.printStackTrace()}",
                        "Cancel"
                    )
                    pbRm.visibility = View.GONE
                }
            }
        }
    }

    private fun initRecyclerViewAndLoadStateAdapter() {
        binding.rvRmList.apply {
            rmAdapter = RickAndMortyAdapter(this@RickAndMortyActivity)
            adapter = rmAdapter.withLoadStateHeaderAndFooter(
                header = RMLoadStateAdapter { rmAdapter.retry() }, // paging3 retry method
                footer = RMLoadStateAdapter { rmAdapter.retry() },
            )
            layoutManager = StaggeredGridLayoutManager(
                2,
                GridLayoutManager.VERTICAL
            )
            hasFixedSize()
        }
    }

    private fun addLoadStateListener() {
        rmAdapter.addLoadStateListener { loadState ->
            binding.apply {
                pbRm.isVisible = loadState.source.refresh is LoadState.Loading
                rvRmList.isVisible = loadState.source.refresh is LoadState.NotLoading
            }
        }
    }

    private fun setupSearchView() = binding.rmSearchView.apply {
        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    binding.rvRmList.smoothScrollToPosition(0)
                    viewModel.searchCharacterJob(query)
                    clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun onSwipeBackPressed() = onBackPressedDispatcher.addCallback(
        this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@RickAndMortyActivity, RickAndMortyActivity::class.java)
                startActivity(backIntent)
                finish()
            }
        }
    )

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_MAIN = "EXTRA_MAIN"
    }
}
