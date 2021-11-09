package com.mnuel.dev.notes.ui.screens.notebooks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OrderingDialog() {
    DialogContent()
}

@Composable
private fun DialogContent() {
    Surface {
        Column(
            Modifier.fillMaxWidth()
                .height(204.dp)
        ) {
            Divider(
                modifier = Modifier.width(12.dp)
                    .padding(top = 8.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(height = 8.dp))

            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "Ordenar",
                style = MaterialTheme.typography.subtitle1,
            )

            Spacer(Modifier.height(height = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(48.dp)
                    .clickable { },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.padding(start = 16.dp),
                    imageVector = Icons.Outlined.Sort,
                    contentDescription = null
                )
                Spacer(Modifier.width(width = 16.dp))
                Text(text = "Alphabetically")
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(48.dp)
                    .clickable { },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.padding(start = 16.dp),
                    imageVector = Icons.Outlined.Sort,
                    contentDescription = null
                )
                Spacer(Modifier.width(width = 16.dp))
                Text(text = "Ascending")
            }

            Spacer(Modifier.height(height = 8.dp))
            Row(
                Modifier.fillMaxWidth()
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
}

@Preview
@Composable
private fun DialogPreview() {
    DialogContent()
}