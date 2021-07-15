package com.mnuel.dev.notes.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.ui.components.DrawerSect
import com.mnuel.dev.notes.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalStdlibApi::class)
@HiltViewModel
class NotesAppViewModel @Inject constructor(
    repository: CollectionsRepository,
) : ViewModel() {

    private val mCollectionsSections = MutableStateFlow(emptyList<DrawerSect>())

    val collectionSections: StateFlow<List<DrawerSect>> = mCollectionsSections

    init {
        viewModelScope.launch {
            val collections = repository.getDefaultCollections()
            mCollectionsSections.value = buildList {
                collections.forEach {
                    add(
                        DrawerSect(
                            route = "${Routes.COLLECTIONS.substringBefore("/")}/${it.id}",
                            icon = Icons.Outlined.Bookmark,
                            title = it.description
                        )
                    )
                }
                add(
                    DrawerSect(
                        route = Routes.CREATE_COLLECTION,
                        icon = Icons.Outlined.Edit,
                        title = "Create New"
                    )
                )
            }
        }
    }

    fun getDrawerSectionFromBackstackEntry(entry: NavBackStackEntry?): DrawerSect? {
        var route = entry?.destination?.route?.substringBefore("/")
        val collectionId = entry?.arguments?.getInt("collectionId")

        return when(route) {
            Routes.COLLECTIONS -> {
                route = "$route/$collectionId"
                mCollectionsSections.value.find { it.route == route }
            }
            else -> mCollectionsSections.value.find { it.route == route }
        }
    }


}