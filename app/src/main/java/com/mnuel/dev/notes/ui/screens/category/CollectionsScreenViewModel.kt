package com.mnuel.dev.notes.ui.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.domain.usecases.SaveCollectionUseCase
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.room.entities.Collection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionsScreenViewModel @Inject constructor(
    private val repository: CollectionsRepository,
) : ViewModel() {

    private val mSelectedCollection: MutableStateFlow<Collection?> = MutableStateFlow(null)

    private val mCategories: MutableStateFlow<List<Collection>> = MutableStateFlow(emptyList())

    private val mState: MutableStateFlow<CollectionsScreenState> = MutableStateFlow(CollectionsScreenState())

    val selectedCategory: StateFlow<Collection?> = mSelectedCollection

    val categories: StateFlow<List<Collection>> = mCategories

    val state: StateFlow<CollectionsScreenState> = mState

    init {
        viewModelScope.launch {
            repository.getCollections().collect {
                mCategories.value = it
            }
        }
        viewModelScope.launch {
            combine(mCategories, mSelectedCollection) { categories, selection ->
                CollectionsScreenState(categories, selection)
            }.collect { mState.value = it }
        }
    }

    fun selectCategory(id: Int) {
        viewModelScope.launch {
            val cat = repository.getCollectionById(id)
            mSelectedCollection.value = cat
        }
    }

    fun saveCollection(collection: String) {
        viewModelScope.launch {
            SaveCollectionUseCase(collection, repository)
                .execute()
        }
    }

}

data class CollectionsScreenState(
    val collections: List<Collection> = emptyList(),
    val selection: Collection? = null,
    val isSelectionScreen: Boolean = false,
)