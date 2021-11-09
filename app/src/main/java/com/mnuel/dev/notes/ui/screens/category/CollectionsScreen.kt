package com.mnuel.dev.notes.ui.screens.category

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnuel.dev.notes.R
import com.mnuel.dev.notes.model.room.entities.Notebook
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollectionsScreen(
    uiState: CollectionsScreenState = remember { CollectionsScreenState() },
    onNavigateUp: () -> Unit = {},
    onSelectCollection: (Notebook) -> Unit = {},
    onNavigate: (Notebook) -> Unit = {},
    onCreateCollection: (String) -> Unit = {},
    onDeleteCollection: (Notebook) -> Unit = {},
) {

    val coroutineScope = rememberCoroutineScope()

    val selection = uiState.selection

    val collections = uiState.notebooks

    val isSelectionScreen = uiState.isSelectionScreen

    val bottomDialogState = rememberBottomDialogState()

    var showDialog by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }

    //The collection that has been selected with the MoreVert icon in a List Item.
    var contextMenuNotebook: Notebook? by remember { mutableStateOf(null) }

    if (showDialog) {
        CreateCollectionDialog(
            onCancel = { showDialog = false },
            onAccept = {
                onCreateCollection(it)
                showDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        DeleteCollectionDialog(
            onCancel = { showDeleteDialog = false },
            onAccept = { contextMenuNotebook?.let { onDeleteCollection(it) } }
        )
    }

    ModalBottomSheetLayout(
        sheetState = bottomDialogState.bottomSheetState,
        sheetContent = {
            BottomDialog(
                bottomDialogState,
                onCancel = {},
                onAccept = {})
        }
    ) {
        Scaffold(
            topBar = {
                CollectionsScreenTopBar(
                    onNavigateUp = onNavigateUp,
                    onAdd = { coroutineScope.launch { bottomDialogState.show() } })
            }
        ) {
            Surface {
                LazyColumn(
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    items(
                        collections,
                        key = {
                            if (isSelectionScreen) {
                                "$it:${it.id == selection?.id}"
                            } else {
                                "$it"
                            }
                        }
                    ) {

                        var expanded by remember { mutableStateOf(false) }

                        CollectionItem(
                            selected = it.id == selection?.id,
                            description = it.description,
                            onClick = {
                                if (isSelectionScreen) {
                                    onSelectCollection(it)
                                } else {
                                    onNavigate(it)
                                }
                            },
                            onLongClick = {

                            },
                            menu = {
                                Box {
                                    if (!isSelectionScreen) {
                                        IconButton(
                                            onClick = {
                                                contextMenuNotebook = it
                                                expanded = true
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.MoreVert,
                                                contentDescription = ""
                                            )
                                        }
                                        ListItemDropdownMenu(
                                            expanded = expanded,
                                            onDismiss = { expanded = false },
                                            onDelete = { showDeleteDialog = true },
                                            onEdit = {},
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CollectionsScreenTopBar(
    onNavigateUp: () -> Unit,
    onAdd: () -> Unit,
) {
    TopAppBar(
        modifier = Modifier.height(80.dp),
        title = {
            Text(
                text = stringResource(R.string.your_notebooks),
                style = MaterialTheme.typography.h5.copy(
                    letterSpacing = 1.18.sp
                )
            )
        },
        actions = {
            IconButton(onClick = onAdd) {
                Icon(imageVector = Icons.Outlined.Sort, contentDescription = "")
            }
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp
    )
}


@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun CollectionItem(
    selected: Boolean = false,
    description: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    menu: @Composable (() -> Unit)? = null,
) {

    val color by animateColorAsState(if (selected) Color.Green.copy(alpha = 0.12f) else Color.Transparent)

    Surface {
        ListItem(
            modifier = Modifier
                .background(color)
                .combinedClickable(onLongClick = onLongClick) { onClick() },
            icon = {
                Icon(imageVector = Icons.Outlined.BookmarkBorder, contentDescription = "")
            },
            trailing = {
                menu?.invoke()
            }
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = description,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Composable
fun CreateCollectionDialog(onCancel: () -> Unit, onAccept: (String) -> Unit) {

    var text by remember { mutableStateOf("") }

    AlertDialog(
        modifier = Modifier.fillMaxWidth(.85f),
        onDismissRequest = onCancel,
        title = {
            Text("Collection name", style = MaterialTheme.typography.subtitle1)
        },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { text = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onAccept(text) }) {
                Text("CONFIRM")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("CANCEL")
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun BottomDialog(
    state: BottomDialogState,
    onCancel: () -> Unit,
    onAccept: () -> Unit
) {

    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    SideEffect {
        if (!state.bottomSheetState.isVisible) {
            keyboard?.hide()
            focusManager.clearFocus(force = true)
            Log.d("CollectionsScreen", "SideEffect")
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.text,
            label = { Text("Collection name") },
            onValueChange = { state.text = it }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            TextButton(onClick = onAccept) {
                Text("CANCEL")
            }
            Button(onClick = onCancel) {
                Text("ACCEPT")
            }
        }
    }
}

@Composable
fun DeleteCollectionDialog(onCancel: () -> Unit, onAccept: () -> Unit) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(.85f),
        onDismissRequest = onCancel,
        text = {
            Text("The notes in this collection will be deleted as well.")
        },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text("CONFIRM")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("CANCEL")
            }
        }
    )
}

@Composable
fun ListItemDropdownMenu(
    expanded: Boolean = false,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        DropdownMenuItem(onClick = onEdit) {
            Text("Edit")
        }
        DropdownMenuItem(onClick = onDelete) {
            Text("Delete")
        }
    }
}


@Preview
@Composable
fun BottomDialogPreview() {
    BottomDialog(state = rememberBottomDialogState(), onCancel = { /*TODO*/ }, onAccept = {})
}

@Preview
@Composable
fun CollectionScreenPreview() {
    CollectionsScreen { }
}


@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun CategoryElementPreview() {
    CollectionItem(description = "School", onClick = {}, onLongClick = {})
}