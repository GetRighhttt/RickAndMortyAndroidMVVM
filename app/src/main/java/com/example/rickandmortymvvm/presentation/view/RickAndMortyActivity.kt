package com.example.rickandmortymvvm.presentation.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rickandmortymvvm.databinding.ActivityRickAndMortyBinding
import com.example.rickandmortymvvm.presentation.viewmodel.RickAndMortyViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException

@AndroidEntryPoint
class RickAndMortyActivity : AppCompatActivity() {

    private var _binding: ActivityRickAndMortyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RickAndMortyViewModel by viewModels()
    private lateinit var rmAdapter: RickAndMortyAdapter

    companion object {
        const val EXTRA_MAIN = "EXTRA_MAIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRickAndMortyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        collectRickAndMortyResults()
        backPressed()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collectRickAndMortyResults() = lifecycleScope.launch {
        binding.apply {
            pbRm.visibility = View.VISIBLE
            delay(800)
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
                        val bundle = Bundle().apply {
                            detailIntent.putExtra(EXTRA_MAIN, it)
                        }
                        startActivity(detailIntent)
                        finish()
                    }

                    createSnackBar("Data successfully fetched!")
                    pbRm.visibility = View.GONE
                } catch (e: HttpException) {
                    createSnackBar(e.printStackTrace().toString())
                    pbRm.visibility = View.GONE
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvRmList.apply {
            rmAdapter = RickAndMortyAdapter(this@RickAndMortyActivity)
            adapter = rmAdapter
            layoutManager = StaggeredGridLayoutManager(
                2,
                GridLayoutManager.VERTICAL
            )
            hasFixedSize()
        }.also {
            it.smoothScrollToPosition(0)
        }
    }

    private fun backPressed() = onBackPressedDispatcher.addCallback(
        this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@RickAndMortyActivity, RickAndMortyActivity::class.java)
                startActivity(backIntent)
                finish()
            }
        }
    )

    private fun materialDialog(
        context: Context,
        title: String,
        message: String
    ) = object : MaterialAlertDialogBuilder(this) {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun createSnackBar(message: String) = Snackbar.make(
        binding.root, message, Snackbar.LENGTH_SHORT
    ).show()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}