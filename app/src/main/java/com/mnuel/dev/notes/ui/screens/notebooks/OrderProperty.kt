package com.mnuel.dev.notes.ui.screens.notebooks

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.ui.graphics.vector.ImageVector

enum class OrderProperty(val icon: ImageVector, val title: String) {
    Alphabetically(icon = Icons.Outlined.SortByAlpha, title = "Alphabetically"),
    CreationDate(icon = Icons.Outlined.Event, title = "Creation date"),
}

enum class OrderBy(val icon: ImageVector, val title: String){
    Ascending(Icons.Outlined.ArrowUpward, title = "Ascending"),
    Descending(Icons.Outlined.ArrowDownward, title = "Descending"),
}