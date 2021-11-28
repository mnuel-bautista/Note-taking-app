package com.mnuel.dev.notes.ui.screens.notebooks

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun NotebookDialog(
    onDismiss: () -> Unit,
    onAccept: (notebook: String) -> Unit,
    onCancel: () -> Unit,
) {

    var showError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        DialogContent(
            onAccept = { notebook ->
                if (notebook.isBlank()) {
                    showError = true
                } else onAccept(notebook)
            },
            onCancel,
            showError = showError,
            onShowError = { showError  = it }
        )
    }
}

@Composable
private fun DialogContent(
    onAccept: (notebook: String) -> Unit,
    onCancel: () -> Unit,
    showError: Boolean = false,
    onShowError: (Boolean) -> Unit,
) {

    var notebook by remember { mutableStateOf("") }

    Surface {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .animateContentSize()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "New Notebook",
                style = MaterialTheme.typography.subtitle1
            )
            Column {
                TextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    value = notebook,
                    onValueChange = {
                        notebook = it
                        onShowError(false)
                    },
                    label = { Text(text = "Notebook") },
                    isError = showError,
                )
                if (showError) {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = "You must set a name for notebook",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.error,
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp),
            ) {
                TextButton(onClick = onCancel) {
                    Text(text = "CANCEL")
                }
                TextButton(onClick = { onAccept(notebook) }) {
                    Text(text = "ACCEPT")
                }
            }
        }
    }
}

@Preview
@Composable
private fun DialogPreview() {
    DialogContent({}, {}, onShowError = {})
}