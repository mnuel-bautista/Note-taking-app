package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.NotebooksRepository
import com.mnuel.dev.notes.model.room.entities.Notebook

class DeleteCollectionUseCase(
    private val notebook: Notebook,
    private val repository: NotebooksRepository,
): BaseUseCase<Unit> {
    override suspend fun execute() {
        repository.delete(notebook)
    }
}