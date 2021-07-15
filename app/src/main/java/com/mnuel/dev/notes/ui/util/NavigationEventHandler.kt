package com.mnuel.dev.notes.ui.util

import android.content.Context
import android.content.Intent
import androidx.navigation.NavHostController
import com.mnuel.dev.notes.Section
import com.mnuel.dev.notes.ui.navigation.Routes
import com.mnuel.dev.notes.ui.screens.home.HomeScreenEvent
import com.mnuel.dev.notes.ui.screens.home.HomeScreenEvent.*
import com.mnuel.dev.notes.ui.screens.home.NoteScreenState
import com.mnuel.dev.notes.ui.screens.home.NotesScreenViewModel
import com.mnuel.dev.notes.ui.screens.home.Sort

fun handleNoteScreenEvents(
    navController: NavHostController,
    viewModel: NotesScreenViewModel,
    uiState: NoteScreenState,
    context: Context,
    event: HomeScreenEvent
) {
    when (event) {
        CreateNoteEvent -> navController.navigate(route = Routes.EDIT_NOTE)
        is EditNoteEvent -> navController.navigate(route = "${Routes.EDIT_NOTE}?noteId=${event.noteId}")
        SearchEvent -> navController.navigate(route = Section.Search.route)
        is SelectNoteEvent -> viewModel.selectNote(event.noteId)
        AddFavorite -> viewModel.addToFavorites()
        CopyNote -> viewModel.copyNote()
        DeleteNote -> viewModel.deleteSelectedNote()
        PinNote -> viewModel.pinNote()
        EditNote -> {
            val noteId = uiState.selection?.id
            navController.navigate(route = "start?noteId=$noteId")
        }
        ShareNote -> {
            val text = uiState.selection?.content
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
        is SortEvent -> {
            when (event.sort) {
                Sort.SortAlphabetically -> viewModel.sortAlphabetically()
                Sort.SortByCreationDateAsc -> viewModel.sortByCreated(asc = true)
                Sort.SortByModifiedDateAsc -> viewModel.sortByModified(asc = true)
                Sort.SortByCreationDateDsc -> viewModel.sortByModified(asc = false)
                Sort.SortByModifiedDateDsc -> viewModel.sortByModified(asc = false)
            }
        }
        RemoveFavorite -> viewModel.removeFromFavorites()
        UnpinNote -> viewModel.unpinNote()
    }
}