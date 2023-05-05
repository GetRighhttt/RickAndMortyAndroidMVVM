package com.example.rickandmortymvvm.presentation.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.databinding.ActivityRickAndMortyBinding
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import com.example.rickandmortymvvm.presentation.viewmodel.RickAndMortyViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import retrofit2.HttpException

@AndroidEntryPoint
class RickAndMortyActivity : AppCompatActivity() {

    private var _binding: ActivityRickAndMortyBinding? = null
    private val binding get() = _binding!!

    private val rmList = mutableListOf<RickAndMorty>()

    private val viewModel: RickAndMortyViewModel by viewModels()
    private lateinit var rmAdapter: RickAndMortyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRickAndMortyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        collectRickAndMortyResults()
        backPressed()
    }

    private fun collectRickAndMortyResults() = lifecycleScope.launch {
        binding.apply {
            pbRm.visibility = View.VISIBLE
            viewModel.rickAndMortyResults.observe(this@RickAndMortyActivity) { pagingData ->
                try {
                    rmAdapter.submitData(lifecycle, pagingData)
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
            hasFixedSize()
            rmAdapter = RickAndMortyAdapter(this@RickAndMortyActivity)
            adapter = rmAdapter
            layoutManager = StaggeredGridLayoutManager(
                2,
                GridLayoutManager.VERTICAL
            )
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