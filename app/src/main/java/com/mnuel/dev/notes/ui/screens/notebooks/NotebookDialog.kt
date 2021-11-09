package com.mnuel.dev.notes.ui.screens.notebooks

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
    Dialog(onDismissRequest = onDismiss) {
        DialogContent(
            onAccept,
            onCancel,
        )
    }
}

@Composable
private fun DialogContent(
    onAccept: (notebook: String) -> Unit,
    onCancel: () -> Unit,
) {

    var notebook by remember { mutableStateOf("") }

    Surface {
        Column(
            modifier = Modifier.width(280.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "New Notebook",
                style = MaterialTheme.typography.subtitle1
            )
            TextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = notebook,
                onValueChange = { notebook = it },
                label = { Text(text = "Notebook") },
            )

            Row(
                Modifier.fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
                    .padding(horizontal = 8.dp),
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
    DialogContent({}, {})
}