package com.mnuel.dev.notes.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.room.entities.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: NotesRepository,
): ViewModel() {

    private val mSearchResult: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())

    private val mQuery: MutableStateFlow<String> = MutableStateFlow("")

    val searchResult: StateFlow<List<Note>> = mSearchResult

    val query: StateFlow<String> = mQuery

    private var searchJob: Job? = null

    val queryPlaceholder: String

    init {
        val isFavorite = savedStateHandle.get<String>("isFavorite").toBoolean()
        queryPlaceholder = if(isFavorite) "Search your favorites notes..." else "Search your notes..."
    }

    fun search(query: String) {
        mQuery.value = query
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            repository.search(query.trim()).collect {
                mSearchResult.value = it
            }
        }
    }

}