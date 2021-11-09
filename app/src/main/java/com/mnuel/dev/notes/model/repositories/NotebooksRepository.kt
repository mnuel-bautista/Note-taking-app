package com.mnuel.dev.notes.model.repositories

import com.mnuel.dev.notes.model.room.daos.CollectionDao
import com.mnuel.dev.notes.model.room.entities.Notebook
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface NotebooksRepository {

    fun getAllNotebooks(): Flow<List<Notebook>>

    suspend fun getNotebooksById(id: Int): Notebook

    suspend fun getNotebooksByIdFlow(id: Int): Flow<Notebook?>

    suspend fun insert(notebook: Notebook)

    suspend fun delete(notebook: Notebook)

    suspend fun getDefaultNotebooks(): List<Notebook>

}

class NotebooksRepositoryImpl(private val collectionDao: CollectionDao):
    NotebooksRepository {

    override fun getAllNotebooks(): Flow<List<Notebook>> {
        return collectionDao.getAllCollections().map { categories ->
            categories.map { cat -> Notebook(cat.id, cat.description) }
        }
    }

    override suspend fun getNotebooksById(id: Int): Notebook {
        val category = collectionDao.getCollectionById(id)
        return Notebook(category.id, category.description)
    }

    override suspend fun getNotebooksByIdFlow(id: Int): Flow<Notebook?> {
        return collectionDao.getCollectionByIdFlow(id)
    }

    override suspend fun insert(notebook: Notebook) {
        collectionDao.insert(Notebook(notebook.id, notebook.description))
    }

    override suspend fun delete(notebook: Notebook) {
        collectionDao.delete(notebook)
    }

    override suspend fun getDefaultNotebooks(): List<Notebook> {
        return collectionDao.getDefaultCollections()
    }


}