package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.NotesRepository

class PinNoteUseCase(
    private val repository: NotesRepository,
    private val noteId: Int,
    private val pin: Boolean = false,
) : BaseUseCase<Unit> {
    override suspend fun execute() {
        val note = repository.getNoteById(noteId)
        val updatedNote = note.copy(isPinned = pin)
        repository.update(updatedNote)
    }
}