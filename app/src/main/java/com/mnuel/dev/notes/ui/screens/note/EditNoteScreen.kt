package com.mnuel.dev.notes.ui.screens.note

import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mnuel.dev.notes.R
import com.mnuel.dev.notes.model.room.entities.Collection
import com.mnuel.dev.notes.ui.components.ColorPicker
import com.mnuel.dev.notes.ui.navigation.Routes
import com.mnuel.dev.notes.ui.screens.home.NotesScreenViewModel
import com.mnuel.dev.notes.ui.screens.note.EditNoteScreenEvent.SelectCollection
import com.mnuel.dev.notes.ui.theme.noteColors
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed class EditNoteScreenEvent {
    object OnBackEvent : EditNoteScreenEvent()
    class SelectCollection(val collectionId: Int) : EditNoteScreenEvent()
    object DeleteNote : EditNoteScreenEvent()
}

enum class BottomBarState {
    COLOR, DEFAULT
}


@Composable
fun EditNoteScreen(
    onEvent: (EditNoteScreenEvent) -> Unit,
    navController: NavController,
) {

    val viewModel = hiltViewModel<EditScreenViewModel>()

    val previousEntry = navController.previousBackStackEntry

    val notesScreenViewModel = previousEntry?.let { hiltViewModel<NotesScreenViewModel>(it) }

    val selectedCollection by viewModel.selectedCollection.collectAsState()

    val selectedColor by viewModel.selectedColor.collectAsState()

    val animatedSelectedColor by animateColorAsState(noteColors[selectedColor]
        ?: MaterialTheme.colors.background)

    val colors = MaterialTheme.colors.copy(surface = animatedSelectedColor)

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight

    SideEffect {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setStatusBarColor(
            color = animatedSelectedColor,
            darkIcons = useDarkIcons
        )

        // setStatusBarsColor() and setNavigationBarsColor() also exist
    }

    val contentColor =
        if (noteColors[selectedColor] != null) Color.Black else MaterialTheme.colors.onSurface
    MaterialTheme(colors = colors) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            EditNoteScreenContent(
                selectedCollection = selectedCollection,
                viewModel = viewModel,
                onEvent = { event ->
                    when (event) {
                        EditNoteScreenEvent.OnBackEvent -> {
                            navController.popBackStack()
                        }
                        is SelectCollection -> {
                            navController.navigate(route = "${Routes.SELECT_COLLECTION}/${event.collectionId}")
                        }
                        is EditNoteScreenEvent.DeleteNote -> {
                            viewModel.deleteNote()
                            notesScreenViewModel?.showUndoMessage()
                            navController.popBackStack()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun EditNoteScreenContent(
    selectedCollection: Collection,
    onEvent: (EditNoteScreenEvent) -> Unit = {},
    viewModel: EditScreenViewModel = hiltViewModel(),
) {

    val title by viewModel.title.collectAsState()

    val content by viewModel.content.collectAsState()

    val favorite by viewModel.isFavorite.collectAsState()

    val pinned by viewModel.isPinned.collectAsState()

    val selectedColor by viewModel.selectedColor.collectAsState()

    val creationDate by viewModel.creationDate.collectAsState()

    val modificationDate by viewModel.modificationDate.collectAsState()

    val scaffoldState = rememberScaffoldState()

    var bottomBarState by remember { mutableStateOf(BottomBarState.DEFAULT) }


    LaunchedEffect(Unit) {

        launch {
            viewModel.showEmptyFieldsMessage.collect { showMessage ->
                if (showMessage) {
                    scaffoldState.snackbarHostState.showSnackbar("Empty content")
                }
            }

        }

        launch {
            viewModel.showCopiedNoteMessage.collect { showMessage ->
                if (showMessage) {
                    scaffoldState.snackbarHostState.showSnackbar("Note copied")
                }
            }
        }

    }
    Log.d("EditNoteScreen", LocalContentColor.current.toString())
    Scaffold(
        scaffoldState = scaffoldState,
        contentColor = LocalContentColor.current,
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
                contentColor = LocalContentColor.current,
                navigationIcon = {
                    IconButton(onClick = { onEvent(EditNoteScreenEvent.OnBackEvent) }) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.pinNote() }) {
                        val icon = if (pinned) Icons.Filled.PushPin else Icons.Outlined.PushPin
                        Icon(imageVector = icon, contentDescription = null)
                    }
                    IconButton(onClick = { viewModel.markAsFavorite() }) {
                        val icon =
                            if (favorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder
                        Icon(imageVector = icon, contentDescription = null)
                    }
                    IconButton(
                        modifier = Modifier.clearAndSetSemantics {
                            contentDescription = "Confirm Note"
                        },
                        onClick = {
                            viewModel.saveNote()
                            if (!viewModel.isContentBlank()) {
                                onEvent(EditNoteScreenEvent.OnBackEvent)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = Color(0xffFF371C)
                        )
                    }
                },
                title = {}
            )
        },
        bottomBar = {
            val colors = listOf(MaterialTheme.colors.background) + noteColors.values
            val context = LocalContext.current
            BottomAppBar(
                bottomBarState = bottomBarState,
                selectedCollection = selectedCollection,
                onSelectCollection = { onEvent(SelectCollection(it)) },
                onShareClicked = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, content)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
                onDeleteClicked = {
                    onEvent(EditNoteScreenEvent.DeleteNote)
                },
                onCopyClicked = {
                    viewModel.copyNote()
                },
                onColorPaletteClicked = { bottomBarState = BottomBarState.COLOR },
                colorPicker = {
                    ColorPicker(
                        colors = colors,
                        selectedColor = noteColors[selectedColor]
                            ?: MaterialTheme.colors.background,
                        onSelectColor = {
                            viewModel.selectColor(it)
                        },
                        onClose = { bottomBarState = BottomBarState.DEFAULT }
                    )
                }
            )
        }
    ) {
        Surface(contentColor = LocalContentColor.current,) {
            Column {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                ) {
                    TextField(
                        modifier = Modifier
                            .paddingFrom(FirstBaseline, before = 40.dp, after = 0.dp)
                            .semantics { contentDescription = "Note Title" },
                        value = title,
                        placeholder = stringResource(R.string.note_title),
                        onValueChange = { viewModel.changeTitle(it) },
                        style = MaterialTheme.typography.h5
                    )

                    if (creationDate.isNotEmpty() && modificationDate.isNotEmpty()) {
                        Row(
                            modifier = Modifier.paddingFromBaseline(top = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ProvideTextStyle(
                                value = MaterialTheme.typography.caption.copy(
                                    color = LocalContentColor.current.copy(
                                        alpha = ContentAlpha.disabled
                                    )
                                )
                            ) {
                                Text(text = "Created: $creationDate")
                                Text(text = "Modified: $modificationDate")
                            }
                        }
                    }

                    TextField(
                        modifier = Modifier
                            .paddingFromBaseline(top = 24.dp)
                            .semantics { contentDescription = "Note Content" },
                        value = content,
                        placeholder = stringResource(R.string.note_content),
                        onValueChange = { viewModel.changeContent(it) },
                        style = MaterialTheme.typography.body1
                    )
                }
                Divider()
            }
        }
    }
}

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    singleLine: Boolean = false,
    style: TextStyle = LocalTextStyle.current,
) {
    Box {
        if (value.isEmpty()) {
            Text(
                modifier = modifier,
                text = placeholder,
                style = style,
                color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
            )
        }
        BasicTextField(
            modifier = modifier,
            value = value,
            cursorBrush = SolidColor(LocalContentColor.current),
            singleLine = singleLine,
            onValueChange = onValueChange,
            textStyle = style.copy(color = LocalContentColor.current),
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomAppBar(
    selectedCollection: Collection,
    bottomBarState: BottomBarState = BottomBarState.DEFAULT,
    onSelectCollection: (Int) -> Unit = {},
    onShareClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onCopyClicked: () -> Unit = {},
    onColorPaletteClicked: () -> Unit = {},
    colorPicker: @Composable () -> Unit,
) {

    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.medium,
        ) {
            AnimatedContent(modifier = Modifier.weight(1f), targetState = bottomBarState) {
                when (it) {
                    BottomBarState.COLOR -> {
                        colorPicker()
                    }
                    BottomBarState.DEFAULT -> {
                        Row {
                            IconButton(onClick = onColorPaletteClicked) {
                                Icon(
                                    imageVector = Icons.Outlined.Palette,
                                    contentDescription = "Change Note Color"
                                )
                            }
                            IconButton(onClick = onCopyClicked) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "Copy Note"
                                )
                            }
                            IconButton(onClick = onShareClicked) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = "Share note"
                                )
                            }
                            IconButton(onClick = onDeleteClicked) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete note"
                                )
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .width(136.dp)
                .fillMaxHeight()
        ) {
            //Position a box above the content, so that clicks are not passed to the Icon Button and Text.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(),
                        onClick = {}
                    )
            )
            Row(
                Modifier
                    .width(136.dp)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(),
                        onClick = { onSelectCollection(selectedCollection.id) }
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Icon(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    imageVector = Icons.Outlined.ListAlt,
                    contentDescription = null,
                )

                Text(
                    text = selectedCollection.description,
                    style = MaterialTheme.typography.subtitle1,
                )
            }
        }
    }
}

@Preview
@Composable
fun EditNoteScreenPreview() {
    EditNoteScreen(onEvent = {}, navController = rememberNavController())
}