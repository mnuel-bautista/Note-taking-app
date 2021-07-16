package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.room.entities.Note
import java.time.OffsetDateTime

class CreateNoteUseCase(
    private val title: String,
    private val content: String,
    private val isPinned: Boolean,
    private val isFavorite: Boolean,
    private val repository: NotesRepository,
    private val collectionId: Int,
    private val color: Int,
) : BaseUseCase<Unit> {

    override suspend fun execute() {
        val date = OffsetDateTime.now()
        val note = Note(
            id = 0,
            title = title,
            content = content,
            isFavorite = isFavorite,
            isPinned = isPinned,
            color = color,
            collectionId = collectionId,
            creationDate = date,
            modificationDate = date,
        )
        repository.insert(note)
    }
}