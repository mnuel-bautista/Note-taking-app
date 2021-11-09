package com.mnuel.dev.notes.model.repositories

import com.mnuel.dev.notes.model.room.daos.CollectionDao
import com.mnuel.dev.notes.model.room.entities.Notebook
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CollectionsRepository {

    fun getCollections(): Flow<List<Notebook>>

    suspend fun getCollectionById(id: Int): Notebook

    suspend fun getCollectionByIdFlow(id: Int): Flow<Notebook?>

    suspend fun insert(notebook: Notebook)

    suspend fun delete(notebook: Notebook)

    suspend fun getDefaultCollections(): List<Notebook>

}

class CollectionsRepositoryImpl(private val collectionDao: CollectionDao):
    CollectionsRepository {

    override fun getCollections(): Flow<List<Notebook>> {
        return collectionDao.getAllCollections().map { categories ->
            categories.map { cat -> Notebook(cat.id, cat.description) }
        }
    }

    override suspend fun getCollectionById(id: Int): Notebook {
        val category = collectionDao.getCollectionById(id)
        return Notebook(category.id, category.description)
    }

    override suspend fun getCollectionByIdFlow(id: Int): Flow<Notebook?> {
        return collectionDao.getCollectionByIdFlow(id)
    }

    override suspend fun insert(notebook: Notebook) {
        collectionDao.insert(Notebook(notebook.id, notebook.description))
    }

    override suspend fun delete(notebook: Notebook) {
        collectionDao.delete(notebook)
    }

    override suspend fun getDefaultCollections(): List<Notebook> {
        return collectionDao.getDefaultCollections()
    }


}