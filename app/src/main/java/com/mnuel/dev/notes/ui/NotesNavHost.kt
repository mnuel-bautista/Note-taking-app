package com.mnuel.dev.notes.ui

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.*
import androidx.navigation.NavType.Companion.StringType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import com.mnuel.dev.notes.Section
import com.mnuel.dev.notes.ui.components.Routes
import com.mnuel.dev.notes.ui.screens.category.SelectCategoryScreen
import com.mnuel.dev.notes.ui.screens.home.NotesScreen
import com.mnuel.dev.notes.ui.screens.home.HomeScreenEvent
import com.mnuel.dev.notes.ui.screens.home.NotesScreenViewModel
import com.mnuel.dev.notes.ui.screens.note.EditNoteScreen
import com.mnuel.dev.notes.ui.screens.note.EditNoteScreenEvent
import com.mnuel.dev.notes.ui.screens.search.SearchScreen
import com.mnuel.dev.notes.ui.screens.search.SearchScreenViewModel
import com.mnuel.dev.notes.ui.screens.trash.TrashScreen
import com.mnuel.dev.notes.ui.screens.trash.TrashScreenViewModel
import java.lang.IllegalStateException

@Composable
fun NotesNavHost(
    navController: NavHostController,
    onNavigationIconClick: () -> Unit = {},
    modifier: Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Routes.HOME.name
    ) {
        composable(route = Routes.HOME.name) { entry ->

            val navArgument2=NavArgument.Builder().setDefaultValue("Hello").build()
            entry.destination.addArgument("route", navArgument2)

            Log.d("NotesModule", "Hehe")

            val viewModel = hiltViewModel<NotesScreenViewModel>()

            val context = LocalContext.current

            val notes by viewModel.notes.collectAsState()

            val notesScreenUiState by viewModel.getAllNotes().collectAsState()

            NotesScreen(
                onEvent = { event ->
                    when (event) {
                        HomeScreenEvent.CreateNoteEvent -> {
                            navController.navigate(route = Section.EditNote.route)
                        }
                        is HomeScreenEvent.EditNoteEvent -> {
                            navController.navigate(route = "${Section.EditNote.route}?noteId=${event.noteId}")
                        }
                        HomeScreenEvent.SearchEvent -> {
                            navController.navigate(route = Section.Search.route)
                        }
                        is HomeScreenEvent.SelectNoteEvent -> {
                            viewModel.selectNote(event.noteId)
                        }
                        HomeScreenEvent.AddFavorite -> {
                            viewModel.addToFavorites()
                        }
                        HomeScreenEvent.CopyNote -> {
                            viewModel.copyNote()
                        }
                        HomeScreenEvent.DeleteNote -> TODO()
                        HomeScreenEvent.EditNote -> TODO()
                        HomeScreenEvent.PinNote -> TODO()
                        HomeScreenEvent.ShareNote -> {
                            val text = notesScreenUiState.selection?.content
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, text)
                                type = "text/plain"
                            }

                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }
                    }
                },
                notes = notes,
                title = "Home",
                viewModel = viewModel,
                onNavigation = onNavigationIconClick,
            )
        }

        composable(route = Routes.FAVORITES.name) { entry ->

            val navArgument2=NavArgument.Builder().setDefaultValue("Hello").build()
            entry.destination.addArgument("route", navArgument2)
            entry.savedStateHandle.set("route", entry.destination.route)


            CompositionLocalProvider(LocalViewModelStoreOwner provides entry) {
                val viewModel = hiltViewModel<NotesScreenViewModel>()
            }

            Log.d("NotesModule", "Hehe")
            val viewModel = hiltViewModel<NotesScreenViewModel>()
            val s = LocalViewModelStoreOwner.current

            val favoriteNotes by viewModel.getFavoriteNotes().collectAsState()

            NotesScreen(
                onEvent = { event ->
                    when (event) {
                        HomeScreenEvent.CreateNoteEvent -> {
                            navController.navigate(route = Section.EditNote.route)
                        }
                        is HomeScreenEvent.EditNoteEvent -> {
                            navController.navigate(route = "${Section.EditNote.route}?noteId=${event.noteId}")
                        }
                        HomeScreenEvent.SearchEvent -> {
                            navController.navigate(route = Section.Search.route)
                        }
                    }
                },
                notes = favoriteNotes,
                viewModel = viewModel,
                onNavigation = onNavigationIconClick,
            )
        }

        composable(
            route = "${Routes.COLLECTIONS}/{collectionId}",
            arguments = listOf(navArgument("collectionId") { type = NavType.IntType })
        ) { entry ->
            val navArgument2=NavArgument.Builder().setDefaultValue("Hello").build()
            entry.destination.addArgument("route", navArgument2)
            entry.savedStateHandle.set("route", entry.destination.route)
            val viewModel = hiltViewModel<NotesScreenViewModel>()


            val collectionId = entry.arguments?.getInt("collectionId")
                ?: throw IllegalStateException("Pass a valid collection id")



            val notes by viewModel.getNotesByCollection(collectionId).collectAsState()

            NotesScreen(
                onEvent = { event ->
                    when (event) {
                        HomeScreenEvent.CreateNoteEvent -> {
                            navController.navigate(route = Section.EditNote.route)
                        }
                        is HomeScreenEvent.EditNoteEvent -> {
                            navController.navigate(route = "${Section.EditNote.route}?noteId=${event.noteId}")
                        }
                        HomeScreenEvent.SearchEvent -> {
                            navController.navigate(route = Section.Search.route)
                        }
                    }
                },
                notes = notes,
                viewModel = viewModel,
                onNavigation = onNavigationIconClick,
            )
        }

        navigation(
            startDestination = "start?noteId={noteId}",
            route = "${Section.EditNote.route}?noteId={noteId}",
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
                            is EditNoteScreenEvent.SelectCategoryEvent -> {
                                navController.navigate(route = "${Section.SelectCategory.route}/${event.categoryId}")
                            }
                        }
                    },
                    navController = navController,
                )
            }

            composable(
                route = "${Section.SelectCategory.route}/{categoryId}",
                arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
            ) { entry ->

                SelectCategoryScreen(
                    selectedCategoryId = entry.arguments?.getInt("categoryId") ?: 1,
                    navController = navController,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
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

        composable(route = Routes.TRASH.name) {

            val viewModel = hiltViewModel<TrashScreenViewModel>()

            val trashScreenState by viewModel.trashScreenState.collectAsState()

            TrashScreen(
                trashScreenState = trashScreenState,
                onNavigateUp = { navController.navigateUp() },
                onDeleteAllPermanently = { viewModel.deleteAllPermanently() },
                onRestoreNotes = { viewModel.restoreNotes() },
                onDeleteSelected = { viewModel.deleteSelected() },
                onSelect = { id, selected ->
                    if (selected) viewModel.selectNote(id) else viewModel.unSelectNote(id)
                    Log.d("NotesNavHost", "selected: $selected id: $id")
                }
            )
        }

    }
}