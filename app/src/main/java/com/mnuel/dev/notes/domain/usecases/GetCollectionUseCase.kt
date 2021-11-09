package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.NotebooksRepository
import com.mnuel.dev.notes.model.room.entities.Notebook
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Returns the collection with the given id, if the collection does not exist or it was deleted, then it return a default collection.
 * */
class GetCollectionUseCase(
    private val collectionId: Int,
    private val repository: NotebooksRepository,
): BaseUseCase<Flow<Notebook?>> {

    override suspend fun execute(): Flow<Notebook> {
        return repository.getNotebooksByIdFlow(collectionId)
            .map {
                val notebook: Notebook = it ?: repository.getNotebooksById(1)
                notebook
            }
    }

}