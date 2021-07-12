package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.room.entities.Note
import kotlinx.coroutines.flow.Flow

class GetFavoriteNotes(
    private val repository: NotesRepository
): BaseUseCase<Flow<List<Note>>> {

    override suspend fun execute(): Flow<List<Note>> {
        return repository.getAllNotes()
    }

}