package com.mnuel.dev.notes.ui.screens.trash

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mnuel.dev.notes.ui.screens.home.NoteListItem
import com.mnuel.dev.notes.ui.theme.NotesTheme
import com.mnuel.dev.notes.ui.theme.noteColors

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TrashScreen(
    trashScreenState: TrashScreenState,
    onDeleteAllPermanently: () -> Unit = {},
    onDeleteSelected: () -> Unit = {},
    onRestoreNotes: () -> Unit = {},
    onUnselectAll: () -> Unit = {},
    onSelect: (id: Int, selected: Boolean) -> Unit,
    onNavigateUp: () -> Unit,
) {

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(trashScreenState.showRecoveryMessage) {
        if(trashScreenState.showRecoveryMessage) {
            val message = if(trashScreenState.restoreCount > 1) "The notes have been restored" else "The note has been restored."
            scaffoldState.snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if(trashScreenState.appBarState == TopAppBarState.DEFAULT) {
                        IconButton(onClick = onNavigateUp) {
                            Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "")
                        }
                    } else {
                        IconButton(onClick = onUnselectAll) {
                            Icon(imageVector = Icons.Outlined.Clear, contentDescription = "")
                        }
                    }
                },
                title = {},
                actions = {

                    TopAppBarAction(
                        visible = trashScreenState.appBarState == TopAppBarState.DEFAULT,
                        icon = Icons.Outlined.DeleteForever,
                        onClick = onDeleteAllPermanently
                    )

                    TopAppBarAction(
                        visible = trashScreenState.appBarState == TopAppBarState.CONTEXTUAL,
                        icon = Icons.Outlined.Restore,
                        onClick = onRestoreNotes
                    )

                    TopAppBarAction(
                        visible = trashScreenState.appBarState == TopAppBarState.CONTEXTUAL,
                        icon = Icons.Outlined.Delete,
                        onClick = onDeleteSelected
                    )

                }
            )
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(
                trashScreenState.notes,
                key = { "$it - ${trashScreenState.isSelected(it.id)}" }) {
                NoteListItem(
                    title = it.title,
                    content = it.content,
                    selected = trashScreenState.isSelected(it.id),
                    color = noteColors[it.color] ?: MaterialTheme.colors.background,
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RowScope.TopAppBarAction(
    visible: Boolean = false,
    icon: ImageVector,
    onClick: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = ""
            )
        }
    }
}


@Preview
@Composable
fun TrashScreenPreview() {
    var trashScreenState by remember { mutableStateOf(TrashScreenState())}
    NotesTheme {
        TrashScreen(
            trashScreenState = trashScreenState,
            onSelect = { _, _ ->
                trashScreenState = trashScreenState.copy()
            },
            onNavigateUp = {},
        )
    }
}