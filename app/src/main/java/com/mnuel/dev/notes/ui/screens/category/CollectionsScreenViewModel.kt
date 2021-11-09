package com.mnuel.dev.notes.ui.screens.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.domain.usecases.DeleteCollectionUseCase
import com.mnuel.dev.notes.domain.usecases.SaveCollectionUseCase
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.room.entities.Notebook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionsScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CollectionsRepository,
) : ViewModel() {

    private val selection: MutableStateFlow<Notebook?> = MutableStateFlow(null)

    private val categories: MutableStateFlow<List<Notebook>> = MutableStateFlow(emptyList())

    private val isSelectionScreen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val mState: MutableStateFlow<CollectionsScreenState> = MutableStateFlow(CollectionsScreenState())

    val state: StateFlow<CollectionsScreenState> = mState

    init {
        viewModelScope.launch {
            repository.getCollections().collect {
                categories.value = it
            }
        }
        viewModelScope.launch {
            combine(categories, selection, isSelectionScreen) { categories, selection, isSelectionScreen->
                CollectionsScreenState(categories, selection, isSelectionScreen)
            }.collect { mState.value = it }
        }

        val collectionId = savedStateHandle.get<Int>("collectionId")
        isSelectionScreen.value = collectionId != null

        if(collectionId != null)  {
            viewModelScope.launch {
                val collection = repository.getCollectionById(collectionId)
                selection.value = collection
            }
        }
    }

    fun selectCollection(id: Int) {
        viewModelScope.launch {
            val cat = repository.getCollectionById(id)
            selection.value = cat
        }
    }

    fun saveCollection(collection: String) {
        viewModelScope.launch {
            SaveCollectionUseCase(collection, repository)
                .execute()
        }
    }

    fun deleteCollection(notebook: Notebook) {
        viewModelScope.launch {
            DeleteCollectionUseCase(notebook, repository)
                .execute()
        }
    }

    fun createNotebook(name: String) {
        viewModelScope.launch {
            repository.insert(Notebook(id = 0, description = name))
        }
    }

}

/**
 * @param isSelectionScreen When the screen is used for selecting a collection, this is true.
 * */
data class CollectionsScreenState(
    val notebooks: List<Notebook> = emptyList(),
    val selection: Notebook? = null,
    val isSelectionScreen: Boolean = false,
)