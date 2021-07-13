package com.mnuel.dev.notes.ui.screens.search

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mnuel.dev.notes.ui.screens.home.NoteListItem
import com.mnuel.dev.notes.ui.screens.note.TextField
import com.mnuel.dev.notes.ui.theme.noteColors

private val SearchBarHeight = 48.dp

@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel,
    onSelectNote: (id: Int) -> Unit = {},
    onClose: () -> Unit = {},
) {

    val notes by viewModel.searchResult.collectAsState()

    val query by viewModel.query.collectAsState()

    val queryPlaceholder = viewModel.queryPlaceholder

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp)
    ) {
        SearchBar(
            query = query,
            placeholder = queryPlaceholder,
            onQueryChange = { viewModel.search(it) },
            onClose = onClose
        )

        LazyColumn(
            contentPadding = PaddingValues(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(notes) {
                NoteListItem(
                    title = it.title,
                    content = it.content,
                    color = noteColors[it.color] ?: MaterialTheme.colors.background,
                    onClick = { onSelectNote(it.id) }
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String = "",
    placeholder: String = "",
    onQueryChange: (query: String) -> Unit = {},
    onClose: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SearchBarHeight)
            .clip(shape = RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.12f),
                shape = RoundedCornerShape(4.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 16.dp)
                .weight(1f, fill = true),
            contentAlignment = Alignment.CenterStart
        ) {
            TextField(
                value = query,
                onValueChange = { onQueryChange(it) },
                placeholder = placeholder,
                singleLine = true,
                style = MaterialTheme.typography.body2
            )
        }
        IconButton(onClick = onClose) {
            Icon(imageVector = Icons.Outlined.Close, contentDescription = null)
        }
    }
}