package com.example.rickandmortymvvm.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rickandmortymvvm.domain.model.RickAndMorty

@Database(entities = [RickAndMorty::class], version = 1, exportSchema = false)
abstract class CharacterDatabase : RoomDatabase() {
    abstract fun getCharacterDAO(): CharacterDAO
}