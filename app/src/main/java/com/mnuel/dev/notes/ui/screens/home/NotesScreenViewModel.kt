package com.mnuel.dev.notes.ui.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.domain.usecases.*
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.room.entities.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: NotesRepository,
    private val collectionsRepository: CollectionsRepository
) : ViewModel() {

    val title: String = savedStateHandle.get<String>("title") ?: "Home"

    private val mNotes: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())

    val notes: StateFlow<List<Note>> = mNotes

    private val mState = MutableStateFlow(NoteScreenState())

    val state: StateFlow<NoteScreenState> = mState

    init {

        viewModelScope.launch {
            GetNotesUseCase(repository).execute().collect {
                val grouped = it.groupBy { it.isPinned }
                val pinnedNotes = grouped[true] ?: emptyList()
                val notes = grouped[false] ?: emptyList()
                mState.value = mState.value.copy(notes = notes, pinnedNotes = pinnedNotes)
            }
        }

    }

    fun getAllNotes(): StateFlow<NoteScreenState> {
        val uiState = MutableStateFlow(NoteScreenState())
        viewModelScope.launch {
            GetNotesUseCase(repository).execute().collect {
                uiState.value = uiState.value.copy(notes = it)
            }
        }
        return uiState
    }


    fun selectNote(noteId: Int) {
        val notes = mState.value.notes
        val note = notes.firstOrNull { it.id == noteId }
        mState.value = mState.value.copy(selection = note)
    }

    fun copyNote() {
        val note = mState.value.selection
        note?.let {
            viewModelScope.launch {
                CopyNoteUseCase(
                    id = note.id,
                    title = note.title,
                    content = note.content,
                    isFavorite = note.isFavorite,
                    isPinned = note.isPinned,
                    collectionId = note.collectionId,
                    color = note.color,
                    repository = repository,
                ).execute()
            }
        }
    }

    fun addToFavorites() {
        val note = mState.value.selection
        note?.let {
            viewModelScope.launch {
                UpdateNoteUseCase(
                    id = note.id,
                    title = note.title,
                    content = note.content,
                    isFavorite = true,
                    isPinned = note.isPinned,
                    categoryId = note.collectionId,
                    color = note.color,
                    repository = repository,
                ).execute()
            }
        }
    }

    /**
     * Deletes the currently selected note. If there is no selected note, then it does nothing.
     * */
    fun deleteSelectedNote() {
        val note = mState.value.selection
        note?.let {
            viewModelScope.launch {
                DeleteNoteUseCase(repository, note.id).execute()
            }
        }
    }

}

data class NoteScreenState(
    val selection: Note? = null,
    val notes: List<Note> = emptyList(),
    val pinnedNotes: List<Note> = emptyList(),
) {

}