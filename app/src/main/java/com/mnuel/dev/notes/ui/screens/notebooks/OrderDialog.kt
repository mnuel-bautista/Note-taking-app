package com.mnuel.dev.notes.ui.screens.notebooks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun OrderingDialog() {
    DialogContent()
}

@Composable
private fun DialogContent() {

    val state = remember { OrderState() }

    Surface {
        when (state.dialogContent) {
            DialogContent.OrderingProperties -> {
                OrderDialog(
                    title = "Sort",
                    content = { PropertyOptions(properties = OrderProperty.values().toList()) }
                )
            }
            DialogContent.OrderBy -> {
                OrderDialog(
                    title = "Sort",
                    content = { OrderByOptions(properties = OrderBy.values().toList()) }
                )
            }
            DialogContent.SelectedOptions -> {
                OrderDialog(
                    title = "Sort",
                    content = {
                        SelectedOptions(
                            orderProperty = state.orderProperty,
                            orderBy = state.orderBy,
                            onPropertyClick = { state.dialogContent = DialogContent.OrderingProperties },
                            onOrderByClick = { state.dialogContent = DialogContent.OrderBy },
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SelectedOptions(
    orderProperty: OrderProperty,
    orderBy: OrderBy,
    onPropertyClick: () -> Unit,
    onOrderByClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onPropertyClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.padding(start = 16.dp),
            imageVector = orderProperty.icon,
            contentDescription = null
        )
        Spacer(Modifier.width(width = 16.dp))
        Text(text = orderProperty.title)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onOrderByClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.padding(start = 16.dp),
            imageVector = orderBy.icon,
            contentDescription = null
        )
        Spacer(Modifier.width(width = 16.dp))
        Text(text = orderBy.title)
    }
}


@Composable
fun PropertyOptions(
    properties: List<OrderProperty>
) {
    properties.forEachIndexed { index, property ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(start = 16.dp),
                imageVector = property.icon,
                contentDescription = null
            )
            Spacer(Modifier.width(width = 16.dp))
            Text(text = property.title)
        }
    }
}


@Composable
fun OrderByOptions(
    properties: List<OrderBy>
) {
    properties.forEachIndexed { index, property ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(start = 16.dp),
                imageVector = property.icon,
                contentDescription = null
            )
            Spacer(Modifier.width(width = 16.dp))
            Text(text = property.title)
        }
    }
}


@Composable
private fun OrderDialog(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(204.dp)
    ) {
        Divider(
            modifier = Modifier
                .width(12.dp)
                .padding(top = 8.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(height = 8.dp))

        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = title,
            style = MaterialTheme.typography.subtitle1,
        )

        Spacer(Modifier.height(height = 8.dp))

        content()

        Spacer(Modifier.height(height = 8.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
                .padding(horizontal = 8.dp),
        ) {
            TextButton(onClick = { }) {
                Text(text = "CANCEL")
            }
            TextButton(onClick = { }) {
                Text(text = "ACCEPT")
            }
        }
    }
}

@Preview
@Composable
private fun DialogPreview() {
    DialogContent()
}