package com.example.rickandmortymvvm.presentation.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.rickandmortymvvm.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private var _binding: ActivityIntroBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimation()
    }

    private fun startAnimation() {
        binding.ivRm.animate().apply {
            duration = 200L
            translationXBy(-360f)
        }.withEndAction {
            binding.ivRm.animate().apply {
                duration = 400L
                rotationX(360f)
                translationXBy(360f)
            }
        }
        // delays before moving to other activity
        Handler(Looper.getMainLooper()).postDelayed(
            {
                startActivity(
                    Intent(this@IntroActivity, LoginActivity::class.java)
                )
                finish()
            }, 1200
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}