package com.example.rickandmortymvvm.presentation.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.rickandmortymvvm.core.util.applySystemBarsPadding
import com.example.rickandmortymvvm.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private var _binding: ActivityIntroBinding? = null
    private val binding get() = _binding!!
    private val splashHandler = Handler(Looper.getMainLooper())
    private val navigateToLogin = Runnable {
        startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        _binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.applySystemBarsPadding(
            applyLeft = true,
            applyTop = true,
            applyRight = true,
            applyBottom = true
        )

        showStaticIntro()
    }

    private fun showStaticIntro() {
        splashHandler.postDelayed(navigateToLogin, STATIC_SPLASH_DELAY_MS)
    }

    override fun onDestroy() {
        super.onDestroy()
        splashHandler.removeCallbacks(navigateToLogin)
        _binding = null
    }

    private companion object {
        const val STATIC_SPLASH_DELAY_MS = 2500L
    }
}
