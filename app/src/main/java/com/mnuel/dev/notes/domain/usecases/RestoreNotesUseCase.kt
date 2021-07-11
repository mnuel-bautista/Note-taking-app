package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.NotesRepository

class RestoreNotesUseCase(
    private val repository: NotesRepository,
    private val notesIds: List<Int>
): BaseUseCase<Unit> {
    override suspend fun execute() {
        repository.restoreNotes(notesIds)
    }

}