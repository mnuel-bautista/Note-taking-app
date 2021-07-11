package com.mnuel.dev.notes.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.room.entities.Collection
import com.mnuel.dev.notes.ui.components.DrawerSect
import com.mnuel.dev.notes.ui.components.Routes
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

    private val mDefaultCollections = MutableStateFlow(emptyList<Collection>())

    private val mDrawerSections = MutableStateFlow(emptyList<DrawerSect>())

    val drawerSections: StateFlow<List<DrawerSect>> = mDrawerSections

    init {
        viewModelScope.launch {
            val collections = repository.getDefaultCollections()
            mDrawerSections.value = buildList {
                add(
                    DrawerSect(
                        route = Routes.HOME.name,
                        icon = Icons.Outlined.Home,
                        title = "Inicio"
                    )
                )
                add(
                    DrawerSect(
                        route = Routes.FAVORITES.name,
                        icon = Icons.Outlined.Favorite,
                        title = "Favorites"
                    )
                )
                collections.forEach {
                    add(
                        DrawerSect(
                            route = "${Routes.COLLECTIONS.name}/${it.id}",
                            icon = Icons.Outlined.Bookmark,
                            title = it.description
                        )
                    )
                }
                add(
                    DrawerSect(
                        route = Routes.CREATE.name,
                        icon = Icons.Outlined.Edit,
                        title = "Create New"
                    )
                )
                add(
                    DrawerSect(
                        route = Routes.BACKUP.name,
                        icon = Icons.Outlined.SettingsBackupRestore,
                        title = "Backup & Restore"
                    )
                )
                add(
                    DrawerSect(
                        route = Routes.TRASH.name,
                        icon = Icons.Outlined.Delete,
                        title = "Trash"
                    )
                )
                add(
                    DrawerSect(
                        route = Routes.SETTINGS.name,
                        icon = Icons.Outlined.Settings,
                        title = "Settings"
                    )
                )
            }
        }
    }

    fun getDrawerSectionFromBackstackEntry(entry: NavBackStackEntry?): DrawerSect? {
        var route = entry?.destination?.route?.substringBefore("/")
        val collectionId = entry?.arguments?.getInt("collectionId")

        return when(route) {
            Routes.COLLECTIONS.name -> {
                route = "$route/$collectionId"
                mDrawerSections.value.find { it.route == route }
            }
            else -> mDrawerSections.value.find { it.route == route }
        }
    }


}