package com.mnuel.dev.notes.model.room.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mnuel.dev.notes.model.room.entities.Collection
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Query("SELECT * FROM collections")
    fun getAllCollections(): Flow<List<Collection>>

    @Query("SELECT * FROM collections WHERE id = :id")
    suspend fun getCollectionById(id: Int): Collection

    @Query("SELECT * FROM collections LIMIT 3")
    suspend fun getDefaultCollections(): List<Collection>

    @Insert
    suspend fun insert(category: Collection)

    @Delete
    suspend fun delete(collection: Collection)

}