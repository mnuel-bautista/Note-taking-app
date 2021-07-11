package com.mnuel.dev.notes.ui.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.room.entities.Collection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCategoryViewModel @Inject constructor(
    private val repository: CollectionsRepository,
) : ViewModel() {

    private val mSelectedCollection: MutableStateFlow<Collection?> = MutableStateFlow(null)

    private val mCategories: MutableStateFlow<List<Collection>> = MutableStateFlow(emptyList())

    val selectedCategory: StateFlow<Collection?> = mSelectedCollection

    val categories: StateFlow<List<Collection>> = mCategories

    init {
        viewModelScope.launch {
            repository.getCollections().collect {
                mCategories.value = it
            }
        }
    }

    fun selectCategory(id: Int) {
        viewModelScope.launch {
            val cat = repository.getCollectionById(id)
            mSelectedCollection.value = cat
        }
    }

}