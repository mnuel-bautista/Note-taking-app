package com.mnuel.dev.notes.ui.screens.notebooks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun OrderingDialog(
    orderState: OrderState = remember { OrderState() },
    onAccept: () -> Unit = {}
) {
    DialogContent(
        orderState = orderState,
        onAccept = onAccept,
        onCancel = { orderState.visible = false }
    )
}

@Composable
private fun DialogContent(
    orderState: OrderState,
    onAccept: () -> Unit,
    onCancel: () -> Unit
) {

    Surface {
        when (orderState.dialogContent) {
            DialogContent.OrderingProperties -> {
                OrderDialog(
                    title = "Sort",
                    onAccept = onAccept,
                    onCancel = onCancel,
                    content = {
                        PropertyOptions(
                            properties = OrderProperty.values().toList(),
                            onPropertySelect = {
                                orderState.orderProperty = it
                                orderState.dialogContent = DialogContent.SelectedOptions
                            }
                        )
                    }
                )
            }
            DialogContent.OrderBy -> {
                OrderDialog(
                    title = "Sort",
                    onAccept = onAccept,
                    onCancel = onCancel,
                    content = {
                        OrderByOptions(
                            orderByList = OrderBy.values().toList(),
                            onOrderBySelected = {
                                orderState.orderBy = it
                                orderState.dialogContent = DialogContent.SelectedOptions
                            }
                        )
                    }
                )
            }
            DialogContent.SelectedOptions -> {
                OrderDialog(
                    title = "Sort",
                    onAccept = onAccept,
                    onCancel = onCancel,
                    content = {
                        SelectedOptions(
                            orderProperty = orderState.orderProperty,
                            orderBy = orderState.orderBy,
                            onPropertyClick = {
                                orderState.dialogContent = DialogContent.OrderingProperties
                            },
                            onOrderByClick = { orderState.dialogContent = DialogContent.OrderBy },
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
    properties: List<OrderProperty>,
    onPropertySelect: (OrderProperty) -> Unit,
) {
    properties.forEach { property ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { onPropertySelect(property) },
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
    orderByList: List<OrderBy>,
    onOrderBySelected: (OrderBy) -> Unit,
) {
    orderByList.forEach { orderBy ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { onOrderBySelected(orderBy) },
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
}


@Composable
private fun OrderDialog(
    title: String,
    onCancel: () -> Unit,
    onAccept: () -> Unit,
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
            TextButton(onClick = onCancel) {
                Text(text = "CANCEL")
            }
            TextButton(onClick = onAccept) {
                Text(text = "ACCEPT")
            }
        }
    }
}

@Preview
@Composable
private fun DialogPreview() {
    DialogContent(orderState = remember { OrderState() }, onAccept = {}, onCancel = {})
}