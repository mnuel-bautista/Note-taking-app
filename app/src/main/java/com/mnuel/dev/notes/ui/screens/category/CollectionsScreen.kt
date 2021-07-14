package com.mnuel.dev.notes.ui.screens.category

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mnuel.dev.notes.model.room.entities.Collection


@Composable
fun CollectionsScreen(
    uiState: CollectionsScreenState = remember { CollectionsScreenState() },
    onNavigateUp: () -> Unit = {},
    onCollectionSelected: (Collection) -> Unit = {},
    onNavigate: (Collection) -> Unit = {},
    onCreateCollection: (String) -> Unit = {},
) {

    val selection = uiState.selection

    val collections = uiState.collections

    val isSelectionScreen = uiState.isSelectionScreen

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        CreateCollectionDialog(
            onCancel = { showDialog = false },
            onAccept = {
                onCreateCollection(it)
                showDialog = false
            }
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
        LazyColumn(
            modifier = Modifier.padding(top = 8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
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
                CategoryElement(
                    selected = it.id == selection?.id,
                    description = it.description,
                    onSelect = {
                        if (isSelectionScreen) {
                            onCollectionSelected(it)
                        } else {
                            onNavigate(it)
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun CategoryElement(
    selected: Boolean = false,
    description: String,
    onSelect: () -> Unit,
) {

    val color by animateColorAsState(if (selected) Color.Green.copy(alpha = 0.12f) else Color.Transparent)

    Box(
        Modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(color)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(),
                onClick = onSelect
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = description
        )
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
                Text("DISAGREE")
            }
        }
    )
}


@Preview
@Composable
fun CreateCollectionDialogPreview() {
    CreateCollectionDialog(onCancel = {}, {})
}


@Preview
@Composable
fun CategoryElementPreview() {
    CategoryElement(description = "School", onSelect = {})
}