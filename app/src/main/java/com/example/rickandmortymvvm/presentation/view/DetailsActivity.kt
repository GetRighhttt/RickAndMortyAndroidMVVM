package com.example.rickandmortymvvm.presentation.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.databinding.ActivityDetailsBinding
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import com.example.rickandmortymvvm.presentation.viewmodel.DetailsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private var _binding: ActivityDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsViewModel by viewModels()

    companion object {
        const val EXTRA_DETAIL = "EXTRA_DETAIL"
    }

    init {
        Log.d(EXTRA_DETAIL, "Detail class started")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.pbLoading.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun displayUserInfoFromMain() {
        binding.apply {
            // get reference to character info from main activity
            val rmDetails = getCharacterDetails()
            lifecycleScope.launch {

                rmDetails?.let {

                    // scale and transform image to our needs using Glide.
                    Glide.with(ivImage.context)
                        .load(rmDetails.image)
                        .placeholder(R.drawable.baseline_person_24)
                        .circleCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivImage)

                    tvName.text = it.name
                    tvGender.text = "| ${it.gender}"
                    tvSpecies.text = it.species
                    tvStatus.text = it.status
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

                    // scale and transform image to our needs using Glide.
                    Glide.with(ivImage.context)
                        .load(rmDetails.image)
                        .placeholder(R.drawable.baseline_person_24)
                        .circleCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivImage)

                    tvName.text = it.name
                    tvGender.text = "| ${it.gender}"
                    tvSpecies.text = it.species
                    tvStatus.text = it.status
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

        MaterialAlertDialogBuilder(this)
            .setTitle("Character Saved!")
            .setMessage("Character has been successfully saved to the database.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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
            val backIntent = Intent(this@DetailsActivity, RickAndMortyActivity::class.java)
            startActivity(backIntent)
        }
    }

    private fun createDialogResponses(context: Context, message: String) =
        MaterialAlertDialogBuilder(context)
            .setTitle("Save Character")
            .setMessage(message)
            .setNeutralButton("Dismiss") { _, _ ->
                createSnackBar("Dialog Dismissed.")
            }
            .setNegativeButton("No") { _, _ ->
                createSnackBar("Character not saved.")
            }
            .setPositiveButton("Yes") { _, _ ->
                saveCharacterToDatabase()
            }
            .show()

    private fun createSnackBar(message: String) = Snackbar.make(
        binding.root, message, Snackbar.LENGTH_SHORT
    ).show()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

