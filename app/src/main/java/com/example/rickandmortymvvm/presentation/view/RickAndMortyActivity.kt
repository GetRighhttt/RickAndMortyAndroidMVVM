package com.example.rickandmortymvvm.presentation.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.databinding.ActivityRickAndMortyBinding
import com.example.rickandmortymvvm.presentation.viewmodel.RickAndMortyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RickAndMortyActivity : AppCompatActivity() {

    private var _binding: ActivityRickAndMortyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RickAndMortyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRickAndMortyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        collectRickAndMortyResults()
    }

    private fun collectRickAndMortyResults () = lifecycleScope.launch {
        viewModel.rickAndMortyResults.flowWithLifecycle(lifecycle).collectLatest {
            // TODO: Set the adapter to the data from the API
            // TODO: Do some exception handling
        }
    }

    private fun initRecyclerView() {
        // TODO: Add code to initialize recycler view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}