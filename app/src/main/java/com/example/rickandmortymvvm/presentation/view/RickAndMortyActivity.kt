package com.example.rickandmortymvvm.presentation.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.navigation.ui.AppBarConfiguration
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.databinding.ActivityRickAndMortyBinding
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import com.example.rickandmortymvvm.presentation.viewmodel.RickAndMortyViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.random.Random


@AndroidEntryPoint
class RickAndMortyActivity : AppCompatActivity() {

    private var _binding: ActivityRickAndMortyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RickAndMortyViewModel by viewModels()
    private lateinit var rmAdapter: RickAndMortyAdapter
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        const val EXTRA_MAIN = "EXTRA_MAIN"
    }

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

            navView.setNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_account -> {
                        drawerLayout.close()
                    }

                    R.id.nav_share -> {
                        drawerLayout.close()
                    }

                    R.id.nav_setting -> {
                        drawerLayout.close()
                    }

                    R.id.nav_list -> {
                        drawerLayout.close()
                    }

                    R.id.nav_male -> {
                        rvRmList.smoothScrollToPosition(0)
                        collectMaleData()
                        drawerLayout.close()
                    }

                    R.id.nav_female -> {
                        rvRmList.smoothScrollToPosition(0)
                        collectFemaleData()
                        drawerLayout.close()
                    }

                    R.id.nav_home -> {
                        val rickAndMortyList = listOf<String>("abcdefgirls", "mnopfesdf")
                        rvRmList.smoothScrollToPosition(0)
                        viewModel.searchCharacters(rickAndMortyList.subList(0,1).random().first().toString())
                        drawerLayout.close()
                    }
                }
                true
            }
        }

        initRecyclerViewAndLoadStateAdapter()
        setupSearchView()
        collectRickAndMortyResults()
        addLoadStateListener()
        onSwipeBackPressed()
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
            viewModel.rickAndMortyResults.observe(this@RickAndMortyActivity) {
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
                    createMaterialDialog(
                        this@RickAndMortyActivity,
                        "Error retrieving data! ${e.printStackTrace()}"
                    )
                    pbRm.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collectMaleData() = lifecycleScope.launch {
        binding.apply {
            viewModel.maleGender.observe(this@RickAndMortyActivity) {
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
                    createMaterialDialog(
                        this@RickAndMortyActivity,
                        "Error retrieving data! ${e.printStackTrace()}"
                    )
                    pbRm.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collectFemaleData() = lifecycleScope.launch {
        binding.apply {
            viewModel.femaleGender.observe(this@RickAndMortyActivity) {
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
                    createMaterialDialog(
                        this@RickAndMortyActivity,
                        "Error retrieving data! ${e.printStackTrace()}"
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
                    viewModel.searchCharacters(query)
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

    private fun createMaterialDialog(
        context: Context,
        message: String
    ) = MaterialAlertDialogBuilder(context)
        .setTitle("Error!")
        .setMessage(message)
        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        .show()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}