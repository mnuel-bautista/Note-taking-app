package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.room.entities.Collection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Returns the collection with the given id, if the collection does not exist or it was deleted, then it return a default collection.
 * */
class GetCollectionUseCase(
    private val collectionId: Int,
    private val repository: CollectionsRepository,
): BaseUseCase<Flow<Collection?>> {

    override suspend fun execute(): Flow<Collection> {
        return repository.getCollectionByIdFlow(collectionId)
            .map {
                val collection: Collection = it ?: repository.getCollectionById(1)
                collection
            }
    }

}