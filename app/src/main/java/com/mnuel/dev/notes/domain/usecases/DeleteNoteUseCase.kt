package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.NotesRepository

class DeleteNoteUseCase(
    private val repository: NotesRepository,
    private val id: Int,
): BaseUseCase<Unit> {
    override suspend fun execute() {
        repository.delete(id)
    }
}