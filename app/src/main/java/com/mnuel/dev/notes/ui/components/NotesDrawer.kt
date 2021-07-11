package com.mnuel.dev.notes.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mnuel.dev.notes.model.room.entities.Collection


enum class Routes {
    HOME, FAVORITES, COLLECTIONS, CREATE, BACKUP, TRASH, SETTINGS
}

data class DrawerSect(
    val route: String,
    val title: String,
    val icon: ImageVector,
)

@Composable
fun DrawerContent(
    title: String,
    selected: DrawerSect? = null,
    sections: List<DrawerSect>,
    onNavigation: (DrawerSect) -> Unit = {},
) {
    Text(
        modifier = Modifier
            .paddingFrom(FirstBaseline, before = 36.dp)
            .padding(start = 24.dp),
        text = title,
        color = LocalContentColor.current.copy(alpha = 0.87f),
        style = MaterialTheme.typography.h5,
        fontWeight = FontWeight.SemiBold,
    )

    Column(
        modifier = Modifier
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        sections.take(2).forEach {
            DrawerItem(
                icon = it.icon,
                text = it.title,
                selected = it == selected,
                onSelect = { onNavigation(it) }
            )
        }

    }

    Divider()

    Text(
        modifier = Modifier.padding(top = 8.dp, start = 24.dp),
        text = "Collections",
        style = MaterialTheme.typography.caption,
    )

    Column(
        modifier = Modifier
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        sections.drop(2).take(4).forEach {
            DrawerItem(
                icon = it.icon,
                text = it.title,
                selected = it == selected,
                onSelect = { onNavigation(it) }
            )
        }
    }

    Divider()

    Column(
        modifier = Modifier
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sections.drop(6).forEach {
            DrawerItem(
                icon = it.icon,
                text = it.title,
                selected = it == selected,
                onSelect = { onNavigation(it) }
            )
        }
    }

}

@Composable
fun DrawerItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    selected: Boolean = false,
    onSelect: () -> Unit = {},
) {

    val color by animateColorAsState(if (selected) MaterialTheme.colors.primary else LocalContentColor.current)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(if (selected) color.copy(alpha = 0.12f) else Color.Transparent)
            .clickable { onSelect() },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(start = 16.dp),
            imageVector = icon,
            contentDescription = "",
            tint = if (selected) color else color.copy(alpha = LocalContentAlpha.current)
        )
        Text(
            text = text,
            color = if (selected) color else color.copy(alpha = LocalContentAlpha.current)
        )
    }

}