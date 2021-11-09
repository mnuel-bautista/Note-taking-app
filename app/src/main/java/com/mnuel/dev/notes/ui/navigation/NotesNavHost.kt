package com.mnuel.dev.notes.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import com.mnuel.dev.notes.R
import com.mnuel.dev.notes.Section
import com.mnuel.dev.notes.ui.screens.category.CollectionsScreen
import com.mnuel.dev.notes.ui.screens.category.CollectionsScreenViewModel
import com.mnuel.dev.notes.ui.screens.home.NotesScreen
import com.mnuel.dev.notes.ui.screens.home.NotesScreenViewModel
import com.mnuel.dev.notes.ui.screens.note.EditNoteScreen
import com.mnuel.dev.notes.ui.screens.note.EditNoteScreenEvent
import com.mnuel.dev.notes.ui.screens.note.EditScreenViewModel
import com.mnuel.dev.notes.ui.screens.notebooks.NotebooksScreen
import com.mnuel.dev.notes.ui.screens.search.SearchScreen
import com.mnuel.dev.notes.ui.screens.search.SearchScreenViewModel
import com.mnuel.dev.notes.ui.screens.trash.TrashScreen
import com.mnuel.dev.notes.ui.screens.trash.TrashScreenViewModel
import com.mnuel.dev.notes.ui.util.handleNoteScreenEvents

@Composable
fun NotesNavHost(
    navController: NavHostController,
    onNavigationIconClick: () -> Unit = {},
    modifier: Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(route = Routes.HOME) { entry ->

            val viewModel = hiltViewModel<NotesScreenViewModel>()

            val context = LocalContext.current

            val uiState by viewModel.state.collectAsState()

            NotesScreen(
                onEvent = { event ->
                    handleNoteScreenEvents(navController, viewModel, uiState, context, event)
                },
                title = stringResource(id = R.string.home),
                uiState = uiState,
                onNavigation = onNavigationIconClick,
            )
        }

        composable(route = Routes.FAVORITES) { entry ->

            val viewModel = hiltViewModel<NotesScreenViewModel>()

            val context = LocalContext.current

            val uiState by viewModel.state.collectAsState()

            NotesScreen(
                onEvent = { event ->
                    handleNoteScreenEvents(navController, viewModel, uiState, context, event)
                },
                title = stringResource(R.string.favorites_screen),
                uiState = uiState,
                onNavigation = onNavigationIconClick,
            )
        }

        composable(
            route = "${Routes.COLLECTIONS}/{collectionId}",
            arguments = listOf(navArgument("collectionId") { type = NavType.IntType })
        ) {

            val viewModel = hiltViewModel<NotesScreenViewModel>()

            val context = LocalContext.current

            val uiState by viewModel.state.collectAsState()

            NotesScreen(
                onEvent = { event ->
                    handleNoteScreenEvents(navController, viewModel, uiState, context, event)
                },
                title = uiState.title,
                uiState = uiState,
                onNavigation = onNavigationIconClick,
            )
        }

        navigation(
            startDestination = "start?noteId={noteId}",
            route = "${Routes.EDIT_NOTE}?noteId={noteId}",
        ) {
            composable(
                route = "start?noteId={noteId}",
                arguments = listOf(navArgument("noteId") { nullable = true })
            ) {
                EditNoteScreen(
                    onEvent = { event ->
                        when (event) {
                            EditNoteScreenEvent.OnBackEvent -> {
                                navController.popBackStack()
                            }
                            is EditNoteScreenEvent.SelectCollection -> {
                                navController.navigate(route = "${Routes.SELECT_COLLECTION}/${event.collectionId}")
                            }
                        }
                    },
                    navController = navController,
                )
            }

            composable(
                route = "${Routes.SELECT_COLLECTION}/{collectionId}",
                arguments = listOf(navArgument("collectionId") { type = NavType.IntType })
            ) {

                val viewModel = hiltViewModel<CollectionsScreenViewModel>()
                val editScreenViewModel =
                    hiltViewModel<EditScreenViewModel>(navController.getBackStackEntry("start?noteId={noteId}"))

                val uiState by viewModel.state.collectAsState()

                CollectionsScreen(
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onSelectCollection = {
                        viewModel.selectNotebook(it.id)
                        editScreenViewModel.selectCollection(it.id)
                    },
                    onCreateCollection = { viewModel.saveNotebook(it) },
                    onDeleteCollection = { viewModel.deleteNotebook(it) },
                    uiState = uiState
                )
            }


        }



        composable(route = "${Section.Search.route}?isFavorite={isFavorite}&collectionId={collectionId}") {
            val viewModel = hiltViewModel<SearchScreenViewModel>()
            SearchScreen(
                onSelectNote = { id -> navController.navigate(route = "${Section.EditNote.route}?noteId=$id") },
                onClose = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(route = Routes.CREATE_COLLECTION) {

            val viewModel = hiltViewModel<CollectionsScreenViewModel>()

            val uiState by viewModel.state.collectAsState()
            val query by viewModel.query

            NotebooksScreen(
                notebooks = uiState.notebooks,
                onCreateNotebook = { name ->
                    viewModel.createNotebook(name)
                },
                onBack = { navController.popBackStack() },
                query = query,
                onSearch = {query ->  viewModel.search(query)}
            )
        }

        composable(route = Routes.TRASH) {

            val viewModel = hiltViewModel<TrashScreenViewModel>()

            val trashScreenState by viewModel.trashScreenState.collectAsState()

            TrashScreen(
                trashScreenState = trashScreenState,
                onNavigateUp = { navController.navigateUp() },
                onDeleteAllPermanently = { viewModel.deleteAllPermanently() },
                onRestoreNotes = { viewModel.restoreNotes() },
                onDeleteSelected = { viewModel.deleteSelected() },
                onUnselectAll = { viewModel.unSelectAll() },
                onSelect = { id, selected ->
                    if (selected) viewModel.selectNote(id) else viewModel.unSelectNote(id)
                    Log.d("NotesNavHost", "selected: $selected id: $id")
                }
            )
        }


    }
}