package com.mnuel.dev.notes.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mnuel.dev.notes.ui.components.SortButton
import com.mnuel.dev.notes.ui.screens.home.HomeScreenEvent.*
import com.mnuel.dev.notes.ui.theme.noteColors
import kotlinx.coroutines.launch

sealed class HomeScreenEvent {
    object CreateNoteEvent : HomeScreenEvent()
    object SearchEvent : HomeScreenEvent()
    class EditNoteEvent(val noteId: Int) : HomeScreenEvent()
    class SelectNoteEvent(val noteId: Int) : HomeScreenEvent()
    class SortEvent(val sort: Sort) : HomeScreenEvent()
    object CopyNote : HomeScreenEvent()
    object ShareNote : HomeScreenEvent()
    object EditNote : HomeScreenEvent()
    object PinNote : HomeScreenEvent()
    object UnpinNote : HomeScreenEvent()
    object DeleteNote : HomeScreenEvent()
    object AddFavorite : HomeScreenEvent()
    object RemoveFavorite : HomeScreenEvent()
}

enum class Sort {
    SortAlphabetically,
    SortByCreationDateAsc,
    SortByCreationDateDsc,
    SortByModifiedDateAsc,
    SortByModifiedDateDsc,
}

/**
 * @param onNavigation Callback for when the navigation icon is clicked.
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotesScreen(
    onEvent: (HomeScreenEvent) -> Unit = {},
    onNavigation: () -> Unit,
    title: String? = null,
    uiState: NoteScreenState = NoteScreenState(),
) {

    val scaffoldState = rememberScaffoldState()
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val notes = uiState.notes
    val menuItems = uiState.contextMenuItems
    val pinnedNotes = uiState.pinnedNotes
    val showUndoMessage = uiState.showUndoMessage
    val isMenuExpanded = uiState.isMenuExpanded

    LaunchedEffect(key1 = showUndoMessage) {
        if (showUndoMessage) {
            scaffoldState.snackbarHostState.showSnackbar("The note has been deleted")
        }
    }

    ModalBottomSheetLayout(sheetContent = {
        Column {
            menuItems.forEach {
                Surface {
                    ListItem(
                        modifier = Modifier.clickable {
                            onEvent(it.event)
                            scope.launch { state.hide() }
                        },
                        icon = {
                            Icon(imageVector = it.icon, contentDescription = it.description)
                        },
                        text = { Text(it.description) }
                    )
                }
            }
        }
    }, sheetState = state) {
        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.clearAndSetSemantics {
                        contentDescription = "Create New Note"
                    },
                    onClick = { onEvent(CreateNoteEvent) }
                ) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                }
            },
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onNavigation) {
                            Icon(imageVector = Icons.Outlined.Menu, contentDescription = null)
                        }
                    },
                    title = {
                        Text(text = title ?: "")
                    },
                    actions = {
                        IconButton(onClick = { onEvent(SearchEvent) }) {
                            Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
                        }
                        SortButton(
                            isMenuExpanded,
                            onClick = { uiState.showDropdownMenu() },
                            onDismiss = { uiState.hideDropdownMenu() },
                            onSort = { onEvent(it) }
                        )
                    }
                )
            },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(all = 8.dp)
            ) {
                item {
                    Text(if(pinnedNotes.isNotEmpty()) "Pinned notes" else "")
                }
                items(pinnedNotes) {
                    NoteListItem(
                        title = it.title,
                        content = it.content,
                        color = noteColors[it.color] ?: MaterialTheme.colors.background,
                        onClick = { onEvent(EditNoteEvent(it.id)) },
                        onLongClick = {
                            onEvent(SelectNoteEvent(it.id))
                            scope.launch { state.show() }
                        }
                    )
                }
                item {
                    Text(if(notes.isNotEmpty()) "Notes" else "")
                }
                items(notes, key = { "$it" }) {
                    NoteListItem(
                        title = it.title,
                        content = it.content,
                        color = noteColors[it.color] ?: MaterialTheme.colors.background,
                        onClick = { onEvent(EditNoteEvent(it.id)) },
                        onLongClick = {
                            onEvent(SelectNoteEvent(it.id))
                            scope.launch { state.show() }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListItem(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    color: Color,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {

    Surface(
        shape = RoundedCornerShape(2.dp),
        color = if (selected) Color.Red.copy(alpha = .8f) else color,
        elevation = 4.dp,
    ) {
        Column(
            modifier = modifier
                .combinedClickable(onLongClick = onLongClick) { onClick() }
                .fillMaxWidth()
                .height(136.dp)
                .padding(start = 16.dp)
        ) {
            Text(
                modifier = Modifier
                    .paddingFrom(FirstBaseline, before = 28.dp, after = 0.dp)
                    .background(Color.Blue.copy(alpha = .12f)),
                text = title,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                modifier = Modifier.paddingFrom(FirstBaseline, before = 24.dp),
                text = content,
                style = MaterialTheme.typography.body2,
                color = LocalContentColor.current.copy(alpha = 0.64f)
            )
        }

    }

}


