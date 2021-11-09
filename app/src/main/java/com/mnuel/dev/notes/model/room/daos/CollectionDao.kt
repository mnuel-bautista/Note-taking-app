package com.mnuel.dev.notes.model.room.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mnuel.dev.notes.model.room.entities.Notebook
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Query("SELECT * FROM notebooks")
    fun getAllCollections(): Flow<List<Notebook>>

    @Query("SELECT * FROM notebooks WHERE id = :id")
    suspend fun getCollectionById(id: Int): Notebook

    @Query("SELECT * FROM notebooks WHERE id = :id")
    fun getCollectionByIdFlow(id: Int): Flow<Notebook?>

    @Query("SELECT * FROM notebooks LIMIT 3")
    suspend fun getDefaultCollections(): List<Notebook>

    @Insert
    suspend fun insert(category: Notebook)

    @Delete
    suspend fun delete(notebook: Notebook)

}