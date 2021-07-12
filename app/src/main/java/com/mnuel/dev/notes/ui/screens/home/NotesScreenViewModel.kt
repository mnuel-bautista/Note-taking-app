package com.mnuel.dev.notes.ui.screens.home

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.domain.usecases.*
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.room.entities.Collection
import com.mnuel.dev.notes.model.room.entities.Note
import com.mnuel.dev.notes.ui.theme.noteColors
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

    private val mNotes: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())

    private val mDefaultCollections: MutableStateFlow<List<Collection>> =
        MutableStateFlow(emptyList())

    val notes: StateFlow<List<Note>> = mNotes

    val defaultCollections: StateFlow<List<Collection>> = mDefaultCollections

    val title: String = savedStateHandle.get<String>("title") ?: "Home"

    private val isFavorite = savedStateHandle.get<String?>("isFavorite")?.toBooleanStrictOrNull()

    private val collectionId = savedStateHandle.get<Int>("collectionId")

    private val mState = MutableStateFlow(NoteScreenState())

    val state: StateFlow<NoteScreenState> = MutableStateFlow(NoteScreenState())


    init {


        Log.d("HomeViewModel", "${savedStateHandle.keys()}")

        viewModelScope.launch {
            GetNotesUseCase(repository).execute().collect {
                mNotes.value = it
                mState.value = mState.value.copy(notes = it)
            }
        }
        viewModelScope.launch {
            mDefaultCollections.value = collectionsRepository.getDefaultCollections()
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

    fun getNotesByCollection(collectionId: Int): StateFlow<List<Note>> {
        val mNotes = MutableStateFlow(emptyList<Note>())
        viewModelScope.launch {
            GetNotesByCategoryUseCase(collectionId, repository).execute().collect {
                mNotes.value = it
            }
        }
        return mNotes
    }

    fun getFavoriteNotes(): StateFlow<List<Note>> {
        val mFavoriteNotes = MutableStateFlow(emptyList<Note>())
        viewModelScope.launch {
            GetFavoriteNotes(repository).execute().collect {
                mFavoriteNotes.value = it
            }
        }
        return mFavoriteNotes
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
                    categoryId = note.collectionId,
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

}

data class NoteScreenState(
    val selection: Note? = null,
    val notes: List<Note> = emptyList()
) {

}