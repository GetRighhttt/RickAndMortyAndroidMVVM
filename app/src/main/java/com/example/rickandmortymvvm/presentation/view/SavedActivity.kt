package com.example.rickandmortymvvm.presentation.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.databinding.ActivitySavedBinding
import com.example.rickandmortymvvm.presentation.viewmodel.SavedViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedActivity : AppCompatActivity() {

    private var _binding: ActivitySavedBinding? = null
    private val binding get() = _binding!!
    private lateinit var scAdapter: SavedCharactersAdapter
    private val viewModel: SavedViewModel by viewModels()

    companion object {
        const val SAVED_CHARACTERS = "SAVED_CHARACTERS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySavedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeLiveData()
        initRecyclerView()
        setNavigationIcon()
        onMenuItemSelected()
        onSwipeBackPressed()
        createItemCallBack()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeLiveData() {
        viewModel.currentState.observe(this) {
            scAdapter.notifyDataSetChanged()
            scAdapter.differ.submitList(it)

            scAdapter.setOnItemClickListener {
                val intent = Intent(this@SavedActivity, DetailsActivity::class.java)
                Bundle().apply {
                    intent.putExtra(SAVED_CHARACTERS, it)
                    startActivity(intent)
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvSavedCharacters.apply {
            scAdapter = SavedCharactersAdapter(this@SavedActivity)
            adapter = scAdapter
            layoutManager = StaggeredGridLayoutManager(
                2,
                GridLayoutManager.VERTICAL
            )
            hasFixedSize()
        }
    }

    private fun onMenuItemSelected() {
        binding.apply {
            topUserAppBar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.nav_share -> {
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
        }
    }

    private fun setNavigationIcon() = binding.apply {
        topUserAppBar.setNavigationOnClickListener {
            val backIntent = Intent(this@SavedActivity, RickAndMortyActivity::class.java)
            startActivity(backIntent)
        }
    }

    private fun onSwipeBackPressed() = onBackPressedDispatcher.addCallback(
        this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@SavedActivity, RickAndMortyActivity::class.java)
                startActivity(backIntent)
            }
        }
    )

    private fun createSnackBar(message: String) = Snackbar.make(
        binding.root, message, Snackbar.LENGTH_SHORT
    ).show()

    private fun createItemCallBack() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition
                val character = scAdapter.differ.currentList[position]
                viewModel.deleteCharacter(character)
                this@SavedActivity.let {
                    Snackbar.make(binding.root, "Character Deleted", Snackbar.LENGTH_LONG)
                        .apply {
                            setAction("Undo") {
                                lifecycleScope.launch {
                                    viewModel.addCharacter(character)
                                }
                            }
                        }
                        .show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedCharacters)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}