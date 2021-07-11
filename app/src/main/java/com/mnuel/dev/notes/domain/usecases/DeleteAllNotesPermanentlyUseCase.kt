package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.NotesRepository

class DeleteAllNotesPermanentlyUseCase(
    private val repository: NotesRepository,
): BaseUseCase<Unit> {

    override suspend fun execute() {
        repository.deleteAllNotesPermanently()
    }

}