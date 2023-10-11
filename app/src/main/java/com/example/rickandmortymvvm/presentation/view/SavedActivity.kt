package com.example.rickandmortymvvm.presentation.view

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.rickandmortymvvm.core.util.createPositiveDialog
import com.example.rickandmortymvvm.core.util.createSnackBar
import com.example.rickandmortymvvm.core.util.createSnackBarWithCoroutineAction
import com.example.rickandmortymvvm.core.util.setVisibilityOf
import com.example.rickandmortymvvm.databinding.ActivitySavedBinding
import com.example.rickandmortymvvm.presentation.viewmodel.SavedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        binding.topUserAppBar.title =
            "${getSharedPrefsData(this@SavedActivity)}'s Saved Characters"

        observeLiveData()
        initRecyclerView()
        setNavigationIcon()
        onMenuItemSelected()
        onSwipeBackPressed()
        createItemCallBack()
        observeLoadingState()
    }

    // Using shared preferences from Login activity to populate Home page
    private fun getSharedPrefsData(context: Context?): String? {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        return sharedPreferences.getString(LoginActivity.LOGIN, null)
    }

    private fun observeLoadingState() = viewModel.isLoading.observe(this) { isLoading ->
        binding.pbSaved.visibility = this setVisibilityOf { isLoading }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeLiveData() {
        viewModel.currentState.observe(this) { character ->
            scAdapter.notifyDataSetChanged()
            scAdapter.differ.submitList(character)

            // onclick listener for when the user is clicked
            scAdapter.setOnItemClickListener {

                createPositiveDialog(
                    it.name,
                    "${it.name} is a ${it.gender.lowercase()} ${it.species.lowercase()}. " +
                            "${it.name} was created ${it.created.dropLast(14)} " +
                            "and is currently ${it.status.lowercase()}.",
                    "OK"
                )
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
                    R.id.delete_character -> {
                        if (scAdapter.differ.currentList.isEmpty()) {
                            // button won't work if the list is empty
                            createSnackBar("List is Empty", binding.root)
                        } else {
                            MaterialAlertDialogBuilder(this@SavedActivity)
                                .setTitle("Delete All Characters?")
                                .setMessage("Are you sure you want to delete all characters from your database?")
                                .setNeutralButton("Cancel") { dialog, _ -> dialog.cancel() }
                                .setNegativeButton("No") { dialog, _ ->
                                    createSnackBar(
                                        "Characters not deleted as requested by user.",
                                        binding.root
                                    )
                                }
                                .setPositiveButton("Yes") { _, _ ->
                                    lifecycleScope.launch {
                                        viewModel.deleteAllCharacters()
                                        createSnackBar(
                                            "Characters deleted from Database Successfully",
                                            binding.root
                                        )
                                    }
                                }.show()
                        }
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
                createSnackBarWithCoroutineAction(
                    "Character Deleted",
                    binding.root,
                    action = {
                        lifecycleScope.launch {
                            viewModel.addCharacter(character)
                        }
                    },
                    "Undo"
                )

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