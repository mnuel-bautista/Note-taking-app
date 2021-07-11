package com.mnuel.dev.notes.ui.screens.trash

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mnuel.dev.notes.ui.screens.home.NoteListItem
import com.mnuel.dev.notes.ui.theme.NotesTheme
import com.mnuel.dev.notes.ui.theme.noteColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrashScreen(
    trashScreenState: TrashScreenState,
    onDeleteAllPermanently: () -> Unit = {},
    onDeleteSelected: () -> Unit = {},
    onRestoreNotes: () -> Unit = {},
    onSelect: (id: Int, selected: Boolean) -> Unit,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "")
                    }
                },
                title = {},
                actions = {
                    if (trashScreenState.selectCount > 0) {
                        IconButton(onClick = onRestoreNotes) {
                            Icon(imageVector = Icons.Outlined.Restore, contentDescription = "")
                        }
                        IconButton(onClick = onDeleteSelected) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "")
                        }
                    } else {
                        IconButton(onClick = onDeleteAllPermanently) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteForever,
                                contentDescription = ""
                            )
                        }
                    }
                }
            )
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(trashScreenState.notes, key = { "$it - ${trashScreenState.isSelected(it.id)}" }) {
                NoteListItem(
                    title = it.title,
                    content = it.content,
                    selected = trashScreenState.isSelected(it.id),
                    color = noteColors[it.color],
                    onLongClick = { onSelect(it.id, true) },
                    onClick = {
                        if (trashScreenState.isSelected(it.id)) {
                            onSelect(it.id, false)
                        } else {
                            if (trashScreenState.selectCount > 0) {
                                onSelect(it.id, true)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun TrashScreenPreview() {
    NotesTheme {
        TrashScreen(
            trashScreenState = TrashScreenState(),
            onSelect = { _, _ -> },
            onNavigateUp = {},
        )
    }
}