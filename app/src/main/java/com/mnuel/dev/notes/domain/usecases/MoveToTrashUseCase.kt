package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.NotesRepository

class MoveToTrashUseCase(
    private val noteId: Int,
    private val repository: NotesRepository,
): BaseUseCase<Unit> {
    override suspend fun execute() {
        val note = repository.getNoteById(noteId)
        val newNote = note.copy(isDeleted = true)
        repository.update(newNote)
    }
}