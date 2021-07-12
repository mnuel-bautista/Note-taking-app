package com.mnuel.dev.notes.ui.util

import android.content.Context
import android.content.Intent
import androidx.navigation.NavHostController
import com.mnuel.dev.notes.Section
import com.mnuel.dev.notes.ui.screens.home.HomeScreenEvent
import com.mnuel.dev.notes.ui.screens.home.NoteScreenState
import com.mnuel.dev.notes.ui.screens.home.NotesScreenViewModel

fun handleNoteScreenEvents(
    navController: NavHostController,
    viewModel: NotesScreenViewModel,
    uiState: NoteScreenState,
    context: Context,
    event: HomeScreenEvent
) {
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
        HomeScreenEvent.DeleteNote -> {
            viewModel.deleteSelectedNote()
        }
        HomeScreenEvent.EditNote -> {
            val noteId = uiState.selection?.id
            navController.navigate(route = "start?noteId=$noteId")
        }
        HomeScreenEvent.PinNote -> TODO()
        HomeScreenEvent.ShareNote -> {
            val text = uiState.selection?.content
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
    }
}