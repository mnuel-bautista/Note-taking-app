package com.mnuel.dev.notes.ui.screens.note

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.domain.usecases.CopyNoteUseCase
import com.mnuel.dev.notes.domain.usecases.CreateNoteUseCase
import com.mnuel.dev.notes.domain.usecases.UpdateNoteUseCase
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.room.entities.Collection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@HiltViewModel
class EditScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: NotesRepository,
    private val collectionsRepository: CollectionsRepository,
) : ViewModel() {


    val noteColors = listOf(
        Color(0xffffffff),
        Color(0xff9575cd),
        Color(0xff4fc3f7),
        Color(0xff4db6ac),
        Color(0xffaed581),
        Color(0xfffff176),
        Color(0xffffb74d),
    )

    private var mCurrentNoteId: Int = NEW_NOTE

    private val mTitle: MutableStateFlow<String> = MutableStateFlow("")

    private val mContent: MutableStateFlow<String> = MutableStateFlow("")

    private val mIsFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val mIsPinned: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val mCreationDate: MutableStateFlow<String> = MutableStateFlow("")

    private val mModificationDate: MutableStateFlow<String> = MutableStateFlow("")

    private val mSelectedCategory: MutableStateFlow<Collection> =
        MutableStateFlow(Collection(1, "School"))

    private val mCategories: MutableStateFlow<List<Collection>> = MutableStateFlow(emptyList())

    private val mShowEmptyFieldsMessage: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val mShowCopiedNoteMessage: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val mSelectedColor: MutableStateFlow<Color> = MutableStateFlow(noteColors.first())

    val title: StateFlow<String> = mTitle

    val content: StateFlow<String> = mContent

    val isFavorite: StateFlow<Boolean> = mIsFavorite

    val isPinned: StateFlow<Boolean> = mIsPinned

    val selectedCategory: StateFlow<Collection> = mSelectedCategory

    val selectedColor: StateFlow<Color> = mSelectedColor

    val categories: StateFlow<List<Collection>> = mCategories

    val creationDate: StateFlow<String> = mCreationDate

    val modificationDate: StateFlow<String> = mModificationDate

    val showEmptyFieldsMessage: StateFlow<Boolean> = mShowEmptyFieldsMessage

    val showCopiedNoteMessage: StateFlow<Boolean> = mShowCopiedNoteMessage

    init {
        val noteIdArg = savedStateHandle.get<String>("noteId")
        mCurrentNoteId = noteIdArg?.toInt() ?: NEW_NOTE
        val creationDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val modificationDateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)

        if (mCurrentNoteId != NEW_NOTE) {
            viewModelScope.launch {
                val note = repository.getNoteById(mCurrentNoteId)

                mTitle.value = note.title
                mContent.value = note.content
                mIsFavorite.value = note.isFavorite
                mIsPinned.value = note.isPinned
                mSelectedColor.value = noteColors[note.color]
                mCreationDate.value = creationDateFormatter.format(note.creationDate)
                mModificationDate.value = modificationDateFormatter.format(note.modificationDate)
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
        mSelectedColor.value = color
    }

    fun selectCategory(id: Int) {
        viewModelScope.launch {
            val category = collectionsRepository.getCollectionById(id)
            mSelectedCategory.value = category
        }
    }

    fun saveNote() {
        viewModelScope.launch {
            if (isContentBlank()) {
                mShowEmptyFieldsMessage.value = true
                delay(2000)
                mShowEmptyFieldsMessage.value = false
            } else {
                if(mCurrentNoteId != NEW_NOTE) {
                    UpdateNoteUseCase(
                        id = mCurrentNoteId,
                        title = title.value,
                        content = content.value,
                        repository = repository,
                        isPinned = isPinned.value,
                        isFavorite = isFavorite.value,
                        categoryId = mSelectedCategory.value.id,
                        color = noteColors.indexOf(mSelectedColor.value)
                    ).execute()
                } else {
                    CreateNoteUseCase(
                        title = title.value,
                        content = content.value,
                        repository = repository,
                        isPinned = isPinned.value,
                        isFavorite = isFavorite.value,
                        categoryId = mSelectedCategory.value.id,
                        color = noteColors.indexOf(mSelectedColor.value)
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
                    collectionId = mSelectedCategory.value.id,
                    color = noteColors.indexOf(mSelectedColor.value)
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