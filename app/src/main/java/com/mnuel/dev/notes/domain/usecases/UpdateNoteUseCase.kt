package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.NotesRepository
import java.time.OffsetDateTime

class UpdateNoteUseCase(
    private val id: Int,
    private val title: String,
    private val content: String,
    private val isPinned: Boolean,
    private val isFavorite: Boolean,
    private val repository: NotesRepository,
    private val categoryId: Int,
    private val color: Int,
): BaseUseCase<Unit> {

    override suspend fun execute() {
        val modificationDate = OffsetDateTime.now()
        repository.update(id, title, content, isFavorite, isPinned, color,  categoryId, modificationDate)
    }

}