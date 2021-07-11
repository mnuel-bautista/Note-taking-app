package com.mnuel.dev.notes.ui.screens.home

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.domain.usecases.GetFavoriteNotes
import com.mnuel.dev.notes.domain.usecases.GetNotesByCategoryUseCase
import com.mnuel.dev.notes.domain.usecases.GetNotesUseCase
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.room.entities.Collection
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

    private val mNotes: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())

    private val mDefaultCollections: MutableStateFlow<List<Collection>> =
        MutableStateFlow(emptyList())

    val notes: StateFlow<List<Note>> = mNotes

    val defaultCollections: StateFlow<List<Collection>> = mDefaultCollections

    val title: String = savedStateHandle.get<String>("title") ?: "Home"

    private val isFavorite = savedStateHandle.get<String?>("isFavorite")?.toBooleanStrictOrNull()

    private val collectionId = savedStateHandle.get<Int>("collectionId")


    init {


        Log.d("HomeViewModel", "${savedStateHandle.keys()}")

        viewModelScope.launch {
            GetNotesUseCase(repository).execute().collect {
                mNotes.value = it
            }
        }
        viewModelScope.launch {
            mDefaultCollections.value = collectionsRepository.getDefaultCollections()
        }


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

}