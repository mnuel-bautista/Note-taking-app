package com.mnuel.dev.notes.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.domain.usecases.*
import com.mnuel.dev.notes.domain.usecases.GetNotesUseCase.Companion.SORT_ALPHABETICALLY
import com.mnuel.dev.notes.domain.usecases.GetNotesUseCase.Companion.SORT_CREATED_ASC
import com.mnuel.dev.notes.domain.usecases.GetNotesUseCase.Companion.SORT_CREATED_DSC
import com.mnuel.dev.notes.domain.usecases.GetNotesUseCase.Companion.SORT_MODIFIED_ASC
import com.mnuel.dev.notes.domain.usecases.GetNotesUseCase.Companion.SORT_MODIFIED_DSC
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.room.entities.Note
import com.mnuel.dev.notes.ui.screens.home.NoteScreenState.Companion.contextMenuItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotesScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: NotesRepository,
    private val collectionsRepository: CollectionsRepository,
) : ViewModel() {

    /**
     * Title shown in the app bar for a collection.
     * */
    var title: String = ""
    /**
     * All notes, including pinned notes.
     * */
    private var notes: List<Note> = emptyList()

    private val mState = MutableStateFlow(NoteScreenState())

    val state: StateFlow<NoteScreenState> = mState

    private var notesJob: Job? = null

    init {
        notesJob = viewModelScope.launch {
            launch {
                GetNotesUseCase(repository).execute().collect {
                    notes = it
                    val grouped = it.groupBy { it.isPinned }
                    val pinnedNotes = grouped[true] ?: emptyList()
                    val notes = grouped[false] ?: emptyList()
                    mState.value = mState.value.copy(notes = notes, pinnedNotes = pinnedNotes)
                }
            }
        }
        viewModelScope.launch {
            val collectionId = savedStateHandle.get<Int>("collectionId")
            if (collectionId != null) {
                val collection = collectionsRepository.getCollectionById(collectionId)
                title = collection.description
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
        val note = notes.firstOrNull { it.id == noteId }
        if (note != null) {
            val items = contextMenuItems.toMutableList()
            if (note.isFavorite) {
                items[3] = ContextMenuItem(Icons.Outlined.Favorite, "Remove from favorites",
                    HomeScreenEvent.RemoveFavorite)
            }
            if (note.isPinned) {
                items[4] = ContextMenuItem(Icons.Rounded.PushPin, "Unpin Note",
                    HomeScreenEvent.UnpinNote)
            }

            mState.value = mState.value.copy(selection = note, contextMenuItems = items)
        }
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

    fun removeFromFavorites() {
        val note = mState.value.selection
        note?.let {
            viewModelScope.launch {
                UpdateNoteUseCase(
                    id = note.id,
                    title = note.title,
                    content = note.content,
                    isFavorite = false,
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
                MoveToTrashUseCase(note.id, repository).execute()
            }
        }
    }

    /**
     * Pins the selected note.
     * */
    fun pinNote() {
        val note = mState.value.selection
        note?.let {
            viewModelScope.launch {
                //Pin the note
                PinNoteUseCase(repository, note.id, pin = true).execute()
            }
        }
    }

    fun unpinNote() {
        val note = mState.value.selection
        note?.let {
            viewModelScope.launch {
                //Unpin the note.
                PinNoteUseCase(repository, note.id, pin = false).execute()
            }
        }
    }

    fun showUndoMessage() {
        mState.value = mState.value.copy(showUndoMessage = true)
    }

    fun sortAlphabetically() {
        sortNotes(SORT_ALPHABETICALLY)
    }

    fun sortByModified(asc: Boolean = true) {
        if (asc) sortNotes(SORT_MODIFIED_ASC) else sortNotes(SORT_MODIFIED_DSC)
    }

    fun sortByCreated(asc: Boolean = true) {
        if (asc) sortNotes(SORT_CREATED_ASC) else sortNotes(SORT_CREATED_DSC)
    }

    private fun sortNotes(sortMethod: Int) {
        notesJob?.cancel()
        notesJob = viewModelScope.launch {
            GetNotesUseCase(repository, sortMethod)
                .execute().collect {
                    notes = it
                    val grouped = it.groupBy { it.isPinned }
                    val pinnedNotes = grouped[true] ?: emptyList()
                    val notes = grouped[false] ?: emptyList()
                    mState.value = mState.value.copy(notes = notes, pinnedNotes = pinnedNotes)
                }
        }
    }

}

data class NoteScreenState(
    val selection: Note? = null,
    val notes: List<Note> = emptyList(),
    val pinnedNotes: List<Note> = emptyList(),
    val showUndoMessage: Boolean = false,
    val contextMenuItems: List<ContextMenuItem> = NoteScreenState.contextMenuItems,
) {

    var isMenuExpanded: Boolean by mutableStateOf(false)
        private set

    fun showDropdownMenu() {
        isMenuExpanded = true
    }

    fun hideDropdownMenu() {
        isMenuExpanded = false
    }


    companion object {
        val contextMenuItems = listOf(
            ContextMenuItem(Icons.Outlined.ContentCopy, "Copy", HomeScreenEvent.CopyNote),
            ContextMenuItem(Icons.Outlined.Share, "Share", HomeScreenEvent.ShareNote),
            ContextMenuItem(Icons.Outlined.Edit, "Edit", HomeScreenEvent.EditNote),
            ContextMenuItem(Icons.Outlined.FavoriteBorder, "Favorite", HomeScreenEvent.AddFavorite),
            ContextMenuItem(Icons.Outlined.PushPin, "Pin", HomeScreenEvent.PinNote),
            ContextMenuItem(Icons.Outlined.Delete, "Delete", HomeScreenEvent.DeleteNote),
        )
    }

}

data class ContextMenuItem(
    val icon: ImageVector,
    val description: String,
    val event: HomeScreenEvent,
)
