package com.mnuel.dev.notes.ui.screens.category

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.domain.usecases.DeleteCollectionUseCase
import com.mnuel.dev.notes.domain.usecases.SaveCollectionUseCase
import com.mnuel.dev.notes.model.repositories.NotebooksRepository
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
    private val repository: NotebooksRepository,
) : ViewModel() {

    private val selection: MutableStateFlow<Notebook?> = MutableStateFlow(null)

    private val notebooks: MutableStateFlow<List<Notebook>> = MutableStateFlow(emptyList())

    private val isSelectionScreen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val mState: MutableStateFlow<CollectionsScreenState> =
        MutableStateFlow(CollectionsScreenState())

    private var allNotebooks: List<Notebook> = emptyList()

    val query = mutableStateOf("")

    val state: StateFlow<CollectionsScreenState> = mState

    init {
        viewModelScope.launch {
            repository.getAllNotebooks().collect {
                allNotebooks = it
                search(query.value)
            }
        }
        viewModelScope.launch {
            combine(
                notebooks,
                selection,
                isSelectionScreen
            ) { categories, selection, isSelectionScreen ->
                CollectionsScreenState(categories, selection, isSelectionScreen)
            }.collect { mState.value = it }
        }

        val collectionId = savedStateHandle.get<Int>("collectionId")
        isSelectionScreen.value = collectionId != null

        if (collectionId != null) {
            viewModelScope.launch {
                val collection = repository.getNotebooksById(collectionId)
                selection.value = collection
            }
        }
    }

    fun selectNotebook(id: Int) {
        viewModelScope.launch {
            val cat = repository.getNotebooksById(id)
            selection.value = cat
        }
    }

    fun saveNotebook(collection: String) {
        viewModelScope.launch {
            SaveCollectionUseCase(collection, repository)
                .execute()
        }
    }

    fun deleteNotebook(notebook: Notebook) {
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

    /**
     * Change the query so that the notebooks that are returned by the viewmodel contains these query.
     * */
    fun search(query: String) {
        this.query.value = query

        //When the query is blank, return all the notebooks
        if (query.isBlank()) {
            notebooks.value = allNotebooks
        } else {
            notebooks.value =
                allNotebooks.filter { it.description.lowercase().startsWith(query.lowercase()) }
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