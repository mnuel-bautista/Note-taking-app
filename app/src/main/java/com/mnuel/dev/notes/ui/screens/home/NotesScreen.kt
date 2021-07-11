package com.mnuel.dev.notes.ui.screens.home

import android.util.Log
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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mnuel.dev.notes.model.room.entities.Note
import com.mnuel.dev.notes.ui.screens.home.HomeScreenEvent.CreateNoteEvent
import com.mnuel.dev.notes.ui.theme.noteColors
import kotlinx.coroutines.launch

sealed class HomeScreenEvent {
    object CreateNoteEvent : HomeScreenEvent()
    object SearchEvent : HomeScreenEvent()
    class EditNoteEvent(val noteId: Int) : HomeScreenEvent()
    class SelectNoteEvent(val noteId: Int): HomeScreenEvent()
}

data class ContextMenuItem(
    val icon: ImageVector,
    val description: String
)

private val contextMenuItems = listOf(
    ContextMenuItem(Icons.Outlined.ContentCopy, "Copy"),
    ContextMenuItem(Icons.Outlined.Share, "Share"),
    ContextMenuItem(Icons.Outlined.Edit, "Edit"),
    ContextMenuItem(Icons.Outlined.Favorite, "Favorite"),
    ContextMenuItem(Icons.Outlined.PushPin, "Pin"),
    ContextMenuItem(Icons.Outlined.Delete, "Delete"),
)

/**
 * @param onNavigation Callback for when the navigation icon is clicked.
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotesScreen(
    onEvent: (HomeScreenEvent) -> Unit = {},
    onNavigation: () -> Unit,
    title: String? = null,
    notes: List<Note>,
    viewModel: NotesScreenViewModel,
) {

    val scaffoldState = rememberScaffoldState()
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(sheetContent = {
        Column {
            contextMenuItems.forEach {
                Surface {
                    ListItem(
                        modifier = Modifier.clickable {  },
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
                        IconButton(onClick = { onEvent(HomeScreenEvent.SearchEvent) }) {
                            Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "More")
                        }
                    }
                )
            },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(all = 8.dp)
            ) {
                items(notes, key = { "$it" }) {
                    Log.d("NotesScreen", "$it")
                    NoteListItem(
                        title = it.title,
                        content = it.content,
                        color = noteColors[it.color],
                        onClick = { onEvent(HomeScreenEvent.EditNoteEvent(it.id)) },
                        onLongClick = { scope.launch { state.show() } }
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


