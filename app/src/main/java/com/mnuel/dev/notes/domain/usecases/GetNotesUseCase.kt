package com.mnuel.dev.notes.domain.usecases

import com.mnuel.dev.notes.domain.BaseUseCase
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.repositories.NotesRepository.Companion.CREATED_FIELD
import com.mnuel.dev.notes.model.repositories.NotesRepository.Companion.MODIFIED_FIELD
import com.mnuel.dev.notes.model.repositories.NotesRepository.Companion.TITLE_FIELD
import com.mnuel.dev.notes.model.room.entities.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetNotesUseCase(
    private val repository: NotesRepository,
    private val sort: Int = SORT_CREATED_DSC
) : BaseUseCase<Flow<List<Note>>> {

    override suspend fun execute(): Flow<List<Note>> {
        return when(sort) {
            SORT_ALPHABETICALLY -> {
                repository.getAllNotesSorted(TITLE_FIELD, true)
            }
            SORT_CREATED_ASC -> {
                repository.getAllNotesSorted(CREATED_FIELD, true)
            }
            SORT_MODIFIED_DSC -> {
                repository.getAllNotesSorted(MODIFIED_FIELD, asc = false)
            }
            SORT_MODIFIED_ASC -> {
                repository.getAllNotesSorted(MODIFIED_FIELD, asc = true)
            }
            else -> repository.getAllNotesSorted(CREATED_FIELD, asc = false)
        }
    }

    companion object {
        const val SORT_CREATED_ASC = 1
        const val SORT_CREATED_DSC = 2
        const val SORT_MODIFIED_ASC = 3
        const val SORT_MODIFIED_DSC = 4
        const val SORT_ALPHABETICALLY = 5
    }

}