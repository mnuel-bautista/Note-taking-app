package com.mnuel.dev.notes.ui.screens.note

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.domain.usecases.CopyNoteUseCase
import com.mnuel.dev.notes.domain.usecases.CreateNoteUseCase
import com.mnuel.dev.notes.domain.usecases.GetCollectionUseCase
import com.mnuel.dev.notes.domain.usecases.UpdateNoteUseCase
import com.mnuel.dev.notes.model.repositories.NotebooksRepository
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.room.entities.Notebook
import com.mnuel.dev.notes.ui.theme.DEFAULT_COLOR
import com.mnuel.dev.notes.ui.theme.noteColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@HiltViewModel
class EditScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: NotesRepository,
    private val notebooksRepository: NotebooksRepository,
) : ViewModel() {

    private var mCurrentNoteId: Int = NEW_NOTE

    private val mTitle: MutableStateFlow<String> = MutableStateFlow("")

    private val mContent: MutableStateFlow<String> = MutableStateFlow("")

    private val mIsFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val mIsPinned: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val mCreationDate: MutableStateFlow<String> = MutableStateFlow("")

    private val mModificationDate: MutableStateFlow<String> = MutableStateFlow("")

    private val mSelectedNotebook: MutableStateFlow<Notebook> =
        MutableStateFlow(Notebook(1, "Notes"))

    private val mCategories: MutableStateFlow<List<Notebook>> = MutableStateFlow(emptyList())

    private val mShowEmptyFieldsMessage: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val mShowCopiedNoteMessage: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val mSelectedColor: MutableStateFlow<Int> = MutableStateFlow(DEFAULT_COLOR)

    val title: StateFlow<String> = mTitle

    val content: StateFlow<String> = mContent

    val isFavorite: StateFlow<Boolean> = mIsFavorite

    val isPinned: StateFlow<Boolean> = mIsPinned

    val selectedNotebook: StateFlow<Notebook> = mSelectedNotebook

    val selectedColor: StateFlow<Int> = mSelectedColor

    val categories: StateFlow<List<Notebook>> = mCategories

    val creationDate: StateFlow<String> = mCreationDate

    val modificationDate: StateFlow<String> = mModificationDate

    val showEmptyFieldsMessage: StateFlow<Boolean> = mShowEmptyFieldsMessage

    val showCopiedNoteMessage: StateFlow<Boolean> = mShowCopiedNoteMessage

    private var collectionJob: Job? = null

    init {
        val noteIdArg = savedStateHandle.get<String>("noteId")
        mCurrentNoteId = noteIdArg?.toInt() ?: NEW_NOTE
        val creationDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val modificationDateFormatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)

        if (mCurrentNoteId != NEW_NOTE) {
            viewModelScope.launch {
                val note = repository.getNoteById(mCurrentNoteId)
                val collection = notebooksRepository.getNotebooksById(note.collectionId)
                mTitle.value = note.title
                mContent.value = note.content
                mIsFavorite.value = note.isFavorite
                mIsPinned.value = note.isPinned
                mSelectedColor.value = note.color
                mCreationDate.value = creationDateFormatter.format(note.creationDate)
                mModificationDate.value = modificationDateFormatter.format(note.modificationDate)
                mSelectedNotebook.value = collection
            }
        } else {
            mCreationDate.value = ""
            mModificationDate.value = ""
        }

    }

    fun changeTitle(title: String) {
        mTitle.value = title
    }

    fun changeContent(content: String) {
        mContent.value = content
    }

    fun markAsFavorite() {
        mIsFavorite.value = !isFavorite.value
    }

    fun pinNote() {
        mIsPinned.value = !isPinned.value
    }

    fun deleteNote() {
        if (mCurrentNoteId != NEW_NOTE) {
            viewModelScope.launch {
                repository.delete(mCurrentNoteId)
            }
        }
    }

    fun selectColor(color: Color) {
        val keys = noteColors.keys
        keys.forEach {
            val cl = noteColors[it]
            if (cl == color) {
                mSelectedColor.value = it
                return
            }
        }
        mSelectedColor.value = DEFAULT_COLOR
    }

    fun selectCollection(id: Int) {
        collectionJob?.cancel()
        collectionJob = viewModelScope.launch {
            GetCollectionUseCase(id, notebooksRepository)
                .execute()
                .collect { mSelectedNotebook.value = it }
        }
    }

    fun saveNote() {
        viewModelScope.launch {
            if (isContentBlank()) {
                mShowEmptyFieldsMessage.value = true
                delay(2000)
                mShowEmptyFieldsMessage.value = false
            } else {
                if (mCurrentNoteId != NEW_NOTE) {
                    UpdateNoteUseCase(
                        id = mCurrentNoteId,
                        title = title.value,
                        content = content.value,
                        repository = repository,
                        isPinned = isPinned.value,
                        isFavorite = isFavorite.value,
                        collectionId = mSelectedNotebook.value.id,
                        color = mSelectedColor.value
                    ).execute()
                } else {
                    CreateNoteUseCase(
                        title = title.value,
                        content = content.value,
                        repository = repository,
                        isPinned = isPinned.value,
                        isFavorite = isFavorite.value,
                        collectionId = mSelectedNotebook.value.id,
                        color = mSelectedColor.value
                    ).execute()
                }
            }
        }
    }

    fun copyNote() {
        viewModelScope.launch {
            if (isContentBlank()) {
                mShowEmptyFieldsMessage.value = true
                delay(2000)
                mShowEmptyFieldsMessage.value = false
            } else {
                CopyNoteUseCase(
                    id = mCurrentNoteId,
                    title = title.value,
                    content = content.value,
                    repository = repository,
                    isPinned = isPinned.value,
                    isFavorite = isFavorite.value,
                    collectionId = mSelectedNotebook.value.id,
                    color = mSelectedColor.value
                ).execute()

                mShowCopiedNoteMessage.value = true
                delay(2000)
                mShowCopiedNoteMessage.value = false
            }
        }
    }

    fun isContentBlank(): Boolean {
        return mContent.value.isBlank() || mTitle.value.isBlank()
    }


    companion object {
        const val NEW_NOTE: Int = Int.MIN_VALUE
    }

}

