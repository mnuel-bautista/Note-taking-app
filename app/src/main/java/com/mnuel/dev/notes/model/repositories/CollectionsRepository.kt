package com.mnuel.dev.notes.model.repositories

import com.mnuel.dev.notes.model.room.daos.CollectionDao
import com.mnuel.dev.notes.model.room.entities.Collection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CollectionsRepository {

    fun getCollections(): Flow<List<Collection>>

    suspend fun getCollectionById(id: Int): Collection

    suspend fun insert(collection: Collection)

    suspend fun getDefaultCollections(): List<Collection>

}

class CollectionsRepositoryImpl(private val collectionDao: CollectionDao):
    CollectionsRepository {

    override fun getCollections(): Flow<List<Collection>> {
        return collectionDao.getAllCollections().map { categories ->
            categories.map { cat -> Collection(cat.id, cat.description) }
        }
    }

    override suspend fun getCollectionById(id: Int): Collection {
        val category = collectionDao.getCollectionById(id)
        return Collection(category.id, category.description)
    }

    override suspend fun insert(collection: Collection) {
        collectionDao.insert(Collection(collection.id, collection.description))
    }

    override suspend fun getDefaultCollections(): List<Collection> {
        return collectionDao.getDefaultCollections()
    }


}