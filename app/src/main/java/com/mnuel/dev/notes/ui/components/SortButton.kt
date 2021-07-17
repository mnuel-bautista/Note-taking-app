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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mnuel.dev.notes.ui.screens.home.HomeScreenEvent
import com.mnuel.dev.notes.ui.screens.home.HomeScreenEvent.*
import com.mnuel.dev.notes.ui.screens.home.Sort
import com.mnuel.dev.notes.ui.screens.home.Sort.*

@Composable
fun SortButton(
    expanded: Boolean = false,
    items: List<Sort>,
    selected: Sort,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    onSort: (SortEvent) -> Unit
) {
    Box {
        IconButton(onClick = onClick) {
            Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
            items.forEach {
                DropdownMenuItem(onClick = { onSort(SortEvent(it)) }) {
                    Text(modifier = Modifier.weight(1f), text = stringResource(id = it.title))
                    RadioButton(selected = it == selected, onClick = { onSort(SortEvent(it)) })
                }
            }
        }
    }
}

@Preview
@Composable
fun SortButtonPreview() {
    Scaffold {
        var expanded by remember { mutableStateOf(false) }
        SortButton(
            expanded,
            Sort.values().toList(),
            selected = Sort.SortAlphabetically,
            onClick = { expanded = true },
            onDismiss = { expanded = false },
            {},
        )
    }
}