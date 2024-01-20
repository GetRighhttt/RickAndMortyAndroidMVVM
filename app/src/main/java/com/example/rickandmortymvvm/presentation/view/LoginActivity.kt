package com.example.rickandmortymvvm.presentation.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rickandmortymvvm.core.util.addDelay
import com.example.rickandmortymvvm.core.util.setToast
import com.example.rickandmortymvvm.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateScreenState()
    }

    @SuppressLint("SetTextI18n")
   val loadData: () -> Unit = {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val savedData = sharedPreferences.getString(LOGIN, null)

        binding.apply {
            if ((savedData?.isEmpty() == true) || (savedData?.isBlank() == true)) loadName.visibility =
                View.GONE else View.VISIBLE
            loadName.text = "Hello ${savedData.toString()}!"
        }
    }

    val saveData: () -> ActivityLoginBinding = {
        binding.apply {
            val nameText = nameLogin.text.toString()
            val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.apply {
                // value save to edit text in string
                putString(LOGIN, nameText)
            }.also {

                val savedIntent = Intent(this@LoginActivity, RickAndMortyActivity::class.java)
                Bundle().apply {
                    savedIntent.putExtra(LOGIN, nameText)
                }

                setToast("$nameText logged in to application", Toast.LENGTH_SHORT)
            }.apply()
        }
    }

    val navigateToNewScreen: () -> ActivityLoginBinding = {
        binding.apply {
            loginButton.setOnClickListener {
                lifecycleScope.launch {
                    addDelay { 500 }
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
        const val TAG = "LOGCAT_LOGIN"
    }
}
