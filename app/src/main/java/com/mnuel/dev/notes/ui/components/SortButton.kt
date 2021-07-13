package com.mnuel.dev.notes.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.mnuel.dev.notes.ui.screens.home.HomeScreenEvent
import com.mnuel.dev.notes.ui.screens.home.HomeScreenEvent.*
import com.mnuel.dev.notes.ui.screens.home.Sort
import com.mnuel.dev.notes.ui.screens.home.Sort.*

@Composable
fun SortButton(
    expanded: Boolean = false,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    onSort: (SortEvent) -> Unit
) {
    Box {
        IconButton(onClick = onClick) {
            Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
            DropdownMenuItem(onClick = { onSort(SortEvent(SortByCreationDateAsc)) }) {
                Text("Created(Oldest)")
            }
            DropdownMenuItem(onClick = { onSort(SortEvent(SortByCreationDateDsc)) }) {
                Text("Created(Newest)")
            }
            DropdownMenuItem(onClick = { onSort(SortEvent(SortByModifiedDateAsc))}) {
                Text("Modified(Oldest)")
            }
            DropdownMenuItem(onClick = { onSort(SortEvent(SortByModifiedDateDsc))}) {
                Text("Modified(Newest)")
            }
            DropdownMenuItem(onClick = { onSort(SortEvent(SortAlphabetically))}) {
                Text("Alphabetically")
            }
        }

    }
}

@Preview
@Composable
fun SortButtonPreview() {
    Scaffold {
        var expanded by remember { mutableStateOf(false) }
        SortButton(expanded, onClick = { expanded = true }, onDismiss = { expanded = false }, {})
    }
}