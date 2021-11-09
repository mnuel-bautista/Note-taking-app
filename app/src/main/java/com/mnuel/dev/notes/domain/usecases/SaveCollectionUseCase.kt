package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.room.entities.Notebook

class SaveCollectionUseCase(
    private val collection: String,
    private val repository: CollectionsRepository,
): BaseUseCase<Unit> {
    override suspend fun execute() {
        repository.insert(Notebook(0, collection))
    }
}