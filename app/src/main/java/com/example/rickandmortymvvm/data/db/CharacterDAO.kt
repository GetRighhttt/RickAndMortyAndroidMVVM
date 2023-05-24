package com.example.rickandmortymvvm.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: RickAndMorty)

    @Query("SELECT * FROM characters")
    fun getAllCharacters(): Flow<List<RickAndMorty>>

    @Query("DELETE FROM characters")
    fun deleteAll()

    @Delete
    suspend fun deleteCharacter(character: RickAndMorty)
}