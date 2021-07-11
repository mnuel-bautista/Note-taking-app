package com.mnuel.dev.notes.ui.screens.trash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.domain.usecases.DeleteAllNotesPermanentlyUseCase
import com.mnuel.dev.notes.domain.usecases.GetDeletedNotesUseCase
import com.mnuel.dev.notes.domain.usecases.RestoreNotesUseCase
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.room.entities.Note
import com.mnuel.dev.notes.ui.screens.SelectableNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TrashScreenViewModel @Inject constructor(
    private val repository: NotesRepository,
) : ViewModel() {

    private val mDeletedNotes = MutableStateFlow(emptyList<SelectableNote>())

    private val mSelectedNotes: HashSet<Int> = hashSetOf()

    private var selectCount: Int = 0

    private val mState = MutableStateFlow(TrashScreenState())

    val trashScreenState: StateFlow<TrashScreenState> = mState


    init {
        viewModelScope.launch {
            GetDeletedNotesUseCase(repository)
                .execute().collect {
                    mState.value = mState.value.copy(notes = it)
                }
        }
    }


    fun deleteAllPermanently() {
        viewModelScope.launch {
            DeleteAllNotesPermanentlyUseCase(repository).execute()
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            repository.deleteNotes(notesIds = mSelectedNotes.toList())
            clearSelection()
        }
    }

    fun restoreNotes() {
        viewModelScope.launch {
            RestoreNotesUseCase(repository, mSelectedNotes.toList())
                .execute()
            selectCount = 0
            mSelectedNotes.clear()
            mState.value = mState.value.copy(selected = mSelectedNotes, selectCount = selectCount)
        }
    }


    fun selectNote(id: Int) {
        val added = mSelectedNotes.add(id)
        if(added) selectCount++
        mState.value = mState.value.copy(selected = mSelectedNotes, selectCount = selectCount)
    }

    fun unSelectNote(id: Int) {
        val removed = mSelectedNotes.remove(id)
        if(removed) selectCount--
        mState.value = mState.value.copy(selected = mSelectedNotes, selectCount = selectCount)
    }


    private fun selectNote(id: Int, selected: Boolean) {

        if (selected) mSelectedNotes.add(id) else mSelectedNotes.remove(id)

        mState.value = mState.value.copy(selected = mSelectedNotes)
        Log.d("NotesNavHost", " TrashScreen selected: $selected id: $id")
        /*with(mDeletedNotes) {
            val notes = value.toMutableList()
            val noteId = value.indexOfFirst { it.id == id }

            if (noteId != -1) {
                val older = notes[noteId]
                notes[noteId] = older.copy(selected = selected)
                value = notes
                if (selected) selectCount++ else selectCount--
            }
        }*/
    }

    private fun clearSelection() {
        selectCount = 0
        mSelectedNotes.clear()
        mState.value = mState.value.copy(selected = mSelectedNotes, selectCount = selectCount)
    }

}

data class TrashScreenState(
    val notes: List<Note> = emptyList(),
    private val selected: HashSet<Int> = hashSetOf(),
    val selectCount: Int = 0,
    val showUndoMessage: Boolean = false,
) {

    /**
     * Checks if the note is currently selected
     * */
    fun isSelected(noteId: Int): Boolean {
        return selected.contains(noteId)
    }

}