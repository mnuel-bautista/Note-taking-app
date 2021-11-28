package com.mnuel.dev.notes.ui.screens.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.domain.usecases.DeleteCollectionUseCase
import com.mnuel.dev.notes.domain.usecases.SaveCollectionUseCase
import com.mnuel.dev.notes.model.repositories.NotebooksRepository
import com.mnuel.dev.notes.model.room.entities.Notebook
import com.mnuel.dev.notes.ui.screens.notebooks.OrderBy
import com.mnuel.dev.notes.ui.screens.notebooks.OrderProperty
import com.mnuel.dev.notes.ui.screens.notebooks.OrderState
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

    val orderState: OrderState = OrderState()

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

    /**
     * Deletes selected notebooks
     * */
    fun deleteSelected() {
        viewModelScope.launch {
            val selected = state.value.selected
            val notebooks = state.value.notebooks
            selected.forEach { id ->
                val notebook = notebooks.find { notebook -> notebook.id == id }
                notebook?.let { repository.delete(notebook) }
            }
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

        setNotes()
    }

    fun sortNotebooks() {
        setNotes()
        orderState.visible = false
    }

    fun setNotes() {

        //When the query is blank, return all the notebooks
        var mNotebooks: List<Notebook> = if (query.value.isBlank()) {
            allNotebooks
        } else {
            allNotebooks.filter { it.description.lowercase().startsWith(query.value.lowercase()) }
        }

        mNotebooks = if (orderState.orderBy == OrderBy.Ascending) {
            when (orderState.orderProperty) {
                OrderProperty.Alphabetically -> mNotebooks.sortedBy { it.description }
                else -> mNotebooks.sortedBy { it.id }
            }
        } else {
            when (orderState.orderProperty) {
                OrderProperty.Alphabetically -> mNotebooks.sortedByDescending { it.description }
                else -> mNotebooks.sortedByDescending { it.id }
            }
        }

        notebooks.value = mNotebooks
    }

}

/**
 * @param isSelectionScreen When the screen is used for selecting a collection, this is true.
 * */
data class CollectionsScreenState(
    val notebooks: List<Notebook> = emptyList(),
    val selection: Notebook? = null,
    val isSelectionScreen: Boolean = false,
) {

    var selected by mutableStateOf(emptySet<Int>())
        private set

    val selectionCount: Int
        get() {
            return selected.size
        }

    fun isSelected(notebookId: Int): Boolean {
        return selected.contains(notebookId)
    }

    fun selectNotebook(notebookId: Int) {
        selected = selected.toMutableSet().apply { add(notebookId) }
    }

    fun unselectNotebook(notebookId: Int) {
        selected = selected.toMutableSet().apply { remove(notebookId) }
    }

    fun unselectAll() {
        selected = selected.toMutableSet().apply { clear() }
    }

}