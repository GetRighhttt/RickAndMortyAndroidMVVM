package com.example.rickandmortymvvm.presentation.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.core.util.addDelay
import com.example.rickandmortymvvm.core.util.setToast
import com.example.rickandmortymvvm.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.example.rickandmortymvvm.core.util.applySystemBarsPadding

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.linearLogin.applySystemBarsPadding(
            applyLeft = true,
            applyTop = true,
            applyRight = true,
            applyBottom = true,
            applyIme = true
        )
        updateScreenState()
    }

    val loadData: () -> Unit = {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val savedData = sharedPreferences.getString(LOGIN, null)

        binding.apply {
            loadName.visibility = if (savedData.isNullOrBlank()) View.GONE else View.VISIBLE
            if (!savedData.isNullOrBlank()) {
                loadName.text = getString(R.string.welcome_back, savedData)
            }
        }
    }

    val saveData: () -> ActivityLoginBinding = {
        binding.apply {
            val nameText = nameLogin.text.toString()
            val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            sharedPreferences.edit {
                apply {
                    putString(LOGIN, nameText)
                }.also {
                    val savedIntent = Intent(this@LoginActivity, RickAndMortyActivity::class.java)
                    Bundle().apply {
                        savedIntent.putExtra(LOGIN, nameText)
                    }
                    setToast("$nameText logged in to application", Toast.LENGTH_SHORT)
                }
            }
        }
    }

    val navigateToNewScreen: () -> ActivityLoginBinding = {
        binding.apply {
            loginButton.setOnClickListener {
                lifecycleScope.launch {
                    addDelay { 200 }
                }
                saveData()
                val intent = Intent(this@LoginActivity, RickAndMortyActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun updateScreenState() {
        loadData()
        navigateToNewScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val LOGIN = "LOGIN"
    }
}
