package com.example.rickandmortymvvm.presentation.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.rickandmortymvvm.databinding.ActivityLoginBinding
import com.example.rickandmortymvvm.presentation.viewmodel.LoginViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    companion object {
        const val LOGIN = "LOGIN"
        const val TAG = "LOGCAT_LOGIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()
        navigateToNewScreen()
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val savedData = sharedPreferences.getString(LOGIN, null)

        binding.apply {
            if(savedData!!.isEmpty()) loadName.visibility = View.GONE else View.VISIBLE
            loadName.text = "Hello ${savedData.toString()}!"
        }
    }

    private fun saveData() = binding.apply {
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

            Toast.makeText(
                this@LoginActivity,
                "$nameText saved to application",
                Toast.LENGTH_LONG)
                .show()
        }.apply()
    }

    private fun navigateToNewScreen() = binding.apply {
        loginButton.setOnClickListener {
            saveData()
            val intent = Intent(this@LoginActivity, RickAndMortyActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}