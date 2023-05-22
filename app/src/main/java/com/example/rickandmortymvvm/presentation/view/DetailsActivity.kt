package com.example.rickandmortymvvm.presentation.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.databinding.ActivityDetailsBinding
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailsActivity : AppCompatActivity() {

    private var _binding: ActivityDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        displayUserInfo()
        setNavigationIcon()
        onSwipeBackPressed()
        onMenuItemSelected()
    }

    @SuppressLint("SetTextI18n")
    private fun displayUserInfo() {
        val rmDetails = intent.getParcelableExtra<RickAndMorty>(RickAndMortyActivity.EXTRA_MAIN)
        binding.apply {
            pbLoading.visibility = View.VISIBLE
            lifecycleScope.launch {
                delay(1000)

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
                    tvLocation.text = it.location.name
                    tvStatus.text = it.status
                    tvCreated.text = it.created.dropLast(14)
                    pbLoading.visibility = View.GONE

                }
            }
        }
    }

    private fun onMenuItemSelected() {

        binding.apply {
            topUserAppBar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.add_character -> {
                        createDialogResponses(
                            this@DetailsActivity,
                            "Save Character?",
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


    private fun onSwipeBackPressed() = onBackPressedDispatcher.addCallback(
        this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@DetailsActivity, RickAndMortyActivity::class.java)
                startActivity(backIntent)
            }
        }
    )

    private fun createDialogResponses(context: Context, title: String, message: String) =
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setNeutralButton("Dismiss") { _, _ ->
                createSnackBar("Dialog Dismissed.")
            }
            .setNegativeButton("No") { _, _ ->
               createSnackBar("Character not saved.")
            }
            .setPositiveButton("Yes") { _, _ ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("Character Saved!")
                    .setMessage("Character has been successfully saved to the database.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
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

