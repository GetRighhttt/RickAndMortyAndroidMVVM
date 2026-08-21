package com.example.rickandmortymvvm.presentation.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.core.util.applySystemBarsPadding
import com.example.rickandmortymvvm.core.util.createPositiveDialog
import com.example.rickandmortymvvm.core.util.createSnackBar
import com.example.rickandmortymvvm.core.util.setImage
import com.example.rickandmortymvvm.core.util.setVisibilityOf
import com.example.rickandmortymvvm.core.util.toDisplayLabel
import com.example.rickandmortymvvm.databinding.ActivityDetailsBinding
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import com.example.rickandmortymvvm.presentation.viewmodel.DetailsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private var _binding: ActivityDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsViewModel by viewModels()

    init {
        Log.d(EXTRA_DETAIL, "Detail class started")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.applySystemBarsPadding(applyLeft = true, applyRight = true)
        binding.toolBarLayout.applySystemBarsPadding(applyTop = true)
        binding.detailsContent.applySystemBarsPadding(applyBottom = true)

        updateScreenState()
    }


    private fun updateScreenState() {
        displayUserInfoFromSaved()
        displayUserInfoFromMain()
        setNavigationIcon()
        onMenuItemSelected()
        observeLoadingState()
    }

    private fun getCharacterDetails() =
        intent.getParcelableExtra<RickAndMorty>(RickAndMortyActivity.EXTRA_MAIN)

    private fun getSavedDetails() =
        intent.getParcelableExtra<RickAndMorty>(SavedActivity.SAVED_CHARACTERS)

    private fun observeLoadingState() = viewModel.isLoading.observe(this) { isLoading ->
        binding.pbLoading.visibility = this setVisibilityOf { isLoading }
    }

    @SuppressLint("SetTextI18n")
    private fun displayUserInfoFromMain() {
        binding.apply {
            // get reference to character info from main activity
            val rmDetails = getCharacterDetails()
            lifecycleScope.launch {

                rmDetails?.let {

                    // set image with Glide extension method
                    ivImage.setImage(rmDetails.image, ivImage)
                    tvName.text = it.name
                    tvGender.text = it.gender
                    tvSpecies.text = it.species
                    tvStatus.text = it.status.toDisplayLabel()
                    tvCreated.text = it.created.dropLast(14)
                    pbLoading.visibility = View.GONE

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayUserInfoFromSaved() {
        binding.apply {
            // get reference to character info from main activity
            val rmDetails = getSavedDetails()
            lifecycleScope.launch {

                rmDetails?.let {

                    ivImage.setImage(rmDetails.image, ivImage)

                    tvName.text = it.name
                    tvGender.text = it.gender
                    tvSpecies.text = it.species
                    tvStatus.text = it.status.toDisplayLabel()
                    tvCreated.text = it.created.dropLast(14)
                    pbLoading.visibility = View.GONE

                }
            }
        }
    }

    private fun saveCharacterToDatabase() {

        lifecycleScope.launch {
            viewModel.addCharacter(getCharacterDetails()!!)
        }

        createPositiveDialog(
            "Character Saved!",
            "Character has been successfully saved to the database.",
            "OK"
        )
    }

    private fun onMenuItemSelected() {

        binding.apply {
            topUserAppBar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.add_character -> {
                        createDialogResponses(
                            this@DetailsActivity,
                            "Are you sure you'd like to save this character to your database?"
                        )
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
            finish()
        }
    }

    private fun createDialogResponses(context: Context, message: String) =
        MaterialAlertDialogBuilder(context)
            .setTitle("Save Character")
            .setMessage(message)
            .setNeutralButton("Dismiss") { _, _ ->
                createSnackBar("Dialog Dismissed.", binding.root)
            }
            .setNegativeButton("No") { _, _ ->
                createSnackBar("Character not saved.", binding.root)
            }
            .setPositiveButton("Yes") { _, _ ->
                saveCharacterToDatabase()
            }
            .show()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_DETAIL = "EXTRA_DETAIL"
    }
}
