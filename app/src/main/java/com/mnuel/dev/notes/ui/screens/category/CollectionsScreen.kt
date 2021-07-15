package com.mnuel.dev.notes.ui.screens.category

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mnuel.dev.notes.model.room.entities.Collection


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionsScreen(
    uiState: CollectionsScreenState = remember { CollectionsScreenState() },
    onNavigateUp: () -> Unit = {},
    onSelectCollection: (Collection) -> Unit = {},
    onNavigate: (Collection) -> Unit = {},
    onCreateCollection: (String) -> Unit = {},
    onDeleteCollection: (Collection) -> Unit = {},
) {

    val selection = uiState.selection

    val collections = uiState.collections

    val isSelectionScreen = uiState.isSelectionScreen

    var showDialog by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }

    //The collection that has been selected with the MoreVert icon in a List Item.
    var contextMenuCollection: Collection? by remember { mutableStateOf(null) }

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
            onAccept = { contextMenuCollection?.let { onDeleteCollection(it) } }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "")
                    }
                },
                title = {
                    Text(text = "Categories")
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(imageVector = Icons.Outlined.Add, contentDescription = "")
                    }
                }
            )
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
                                IconButton(
                                    onClick = {
                                        contextMenuCollection = it
                                        expanded = true
                                    }
                                ) {
                                    Icon(imageVector = Icons.Outlined.MoreVert,
                                        contentDescription = "")
                                }
                                ListItemDropdownMenu(
                                    expanded = expanded,
                                    onDismiss = { expanded = false },
                                    onDelete = { showDeleteDialog = true },
                                    onEdit = {},
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}


@ExperimentalFoundationApi
@OptIn(ExperimentalMaterialApi::class)
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
fun CreateCollectionDialogPreview() {
    CreateCollectionDialog(onCancel = {}, {})
}


@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun CategoryElementPreview() {
    CollectionItem(description = "School", onClick = {}, onLongClick = {})
}