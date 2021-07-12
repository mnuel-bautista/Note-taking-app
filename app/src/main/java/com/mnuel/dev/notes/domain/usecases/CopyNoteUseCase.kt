package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.room.entities.Note

class CopyNoteUseCase(
    private val id: Int,
    private val title: String,
    private val content: String,
    private val isPinned: Boolean,
    private val isFavorite: Boolean,
    private val repository: NotesRepository,
    private val collectionId: Int,
    private val color: Int,
) : BaseUseCase<Unit> {
    override suspend fun execute() {
        val note = Note(
            id = id,
            title = title,
            content = content,
            isFavorite = isFavorite,
            isPinned = isPinned,
            collectionId = collectionId,
            color = color,
        )
        val copiedNote = note.copy(id = 0)
        repository.update(note)
        repository.insert(copiedNote)
    }
}