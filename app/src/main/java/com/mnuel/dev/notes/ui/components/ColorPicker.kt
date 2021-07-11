package com.mnuel.dev.notes.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


private val ColorPickerHeight = 56.dp
private val ColorPickerItemSize = 24.dp

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    selectedColor: Color,
    onSelectColor: (Color) -> Unit = {},
    onClose: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(ColorPickerHeight)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        colors.forEach {
            ColorPickerItem(
                color = it,
                selected = selectedColor == it,
                onSelect = { onSelectColor(it) }
            )
        }
        IconButton(onClick = onClose) {
            Icon(imageVector = Icons.Outlined.ChevronLeft, contentDescription = null)
        }
    }
}

@Composable
fun ColorPickerItem(
    color: Color,
    selected: Boolean,
    onSelect: () -> Unit,
) {

    val borderColor by animateColorAsState(if (selected) Color.Black else color)

    Box(
        modifier = Modifier
            .size(ColorPickerItemSize)
            .clip(CircleShape)
            .background(color)
            .border(width = 1.dp, color = borderColor, shape = CircleShape)
            .clickable { onSelect() }
    )
}