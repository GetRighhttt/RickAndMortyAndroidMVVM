package com.example.rickandmortymvvm.presentation.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rickandmortymvvm.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RickAndMortyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rick_and_morty)
    }
}