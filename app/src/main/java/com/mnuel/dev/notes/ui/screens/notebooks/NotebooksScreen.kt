package com.mnuel.dev.notes.ui.screens.notebooks

import android.view.RoundedCorner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mnuel.dev.notes.R
import com.mnuel.dev.notes.ui.screens.category.CollectionsScreenState
import kotlinx.coroutines.launch

data class Notebook(
    val name: String,
    val noteCount: Int,
)

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun NotebooksScreen(
    notebooks: List<com.mnuel.dev.notes.model.room.entities.Notebook>,
    uiState: CollectionsScreenState, //Change the name of the class
    orderState: OrderState,
    onCreateNotebook: (notebook: String) -> Unit,
    query: String,
    onSearch: (query: String) -> Unit,
    onBack: () -> Unit,
    onSort: () -> Unit = {},
    onDelete: () -> Unit = {},
) {

    var dialogVisible by remember { mutableStateOf(false) }
    val bottomState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    if (dialogVisible) {
        NotebookDialog(
            onDismiss = { dialogVisible = false },
            onAccept = { name ->
                onCreateNotebook(name)
                dialogVisible = false
            },
            onCancel = { dialogVisible = false }
        )
    }

    //Side effect for showing the bottom dialog when OrderState.visible is true
    LaunchedEffect(orderState.visible) {
        if (orderState.visible) {
            bottomState.show()
        } else {
            bottomState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetContent = {
            OrderingDialog(orderState = orderState, onAccept = onSort)
        }
    ) {

        Scaffold(
            topBar = {
                AppBar(
                    onBack = onBack,
                    onOrder = { orderState.visible = true },
                    isContext = uiState.selectionCount > 0,
                    onCloseContextual = { uiState.unselectAll() },
                    onDelete = onDelete,
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { dialogVisible = true }) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                }
            }
        ) {
            Column {
                Spacer(Modifier.height(24.dp))
                SearchBar(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    query = query,
                    onQueryChange = onSearch,
                )
                LazyVerticalGrid(
                    cells = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(notebooks) { notebook ->
                        Notebook(
                            modifier = Modifier.combinedClickable(
                                onLongClick = { uiState.selectNotebook(notebook.id) }
                            ) {
                                if (uiState.selectionCount > 0) {
                                    if(uiState.isSelected(notebook.id)) {
                                        uiState.unselectNotebook(notebook.id)
                                    } else {
                                        uiState.selectNotebook(notebook.id)
                                    }
                                }
                            },
                            name = notebook.description,
                            isSelected = uiState.isSelected(notebook.id)
                        )
                    }
                }
            }
        }
    }
}


/**
 *
 * @param isContext When search bar should be contextual
 * */
@Composable
private fun AppBar(
    onOrder: () -> Unit,
    onBack: () -> Unit,
    isContext: Boolean = false,
    onCloseContextual: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .height(92.dp)
            .fillMaxWidth()
    ) {
        if (isContext) {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onCloseContextual) {
                        Icon(Icons.Outlined.Close, contentDescription = null)
                    }
                },
                title = { },
                actions = {
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "")
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp
            )
        } else {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null)
                    }
                },
                title = { },
                actions = {
                    IconButton(onClick = onOrder) {
                        Icon(imageVector = Icons.Outlined.Sort, contentDescription = "")
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp
            )
        }

        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = buildAnnotatedString {
                append("Your ")
                withStyle(style = SpanStyle(color = Color(0xFF985EFF))) {
                    append("notebooks")
                }
            },
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    val placeholder = "Search your notes"

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        decorationBox = { innerTextField ->
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(Color(0xFFE8D9FC).copy(alpha = 0.18f)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(start = 12.dp, end = 16.dp),
                    painter = painterResource(R.drawable.ic_baseline_search_18),
                    contentDescription = null,
                    tint = LocalContentColor.current.copy(alpha = ContentAlpha.high)
                )
                Box(
                    Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    innerTextField()
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.subtitle1.copy(
                                color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                            )
                        )
                    }
                }
            }
        },
        textStyle = MaterialTheme.typography.subtitle1.copy(color = LocalContentColor.current),
        cursorBrush = SolidColor(LocalContentColor.current),
        maxLines = 1
    )
}

@Composable
private fun Notebook(
    modifier: Modifier = Modifier,
    name: String,
    isSelected: Boolean = false,
) {

    val selectedModifier = Modifier.border(width = 1.dp, color = MaterialTheme.colors.primary)

    Column(
        modifier = modifier
            .width(184.dp)
            .height(76.dp)
            .then(if(isSelected) selectedModifier else Modifier)
            .background(color = Color(0xffE8D9FC).copy(alpha = 0.12f))
            .padding(start = 16.dp, top = 16.dp),
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.body2,
            color = Color(0xff7F39FB),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "28 notes",
            style = MaterialTheme.typography.caption,
        )
    }
}

@Preview
@Composable
private fun NotebooksScreenPreview() {
    NotebooksScreen(
        notebooks = emptyList(),
        orderState = OrderState(),
        uiState = CollectionsScreenState(),
        onCreateNotebook = {},
        onBack = {},
        onSearch = {},
        query = "",
    )
}