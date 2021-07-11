package com.mnuel.dev.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mnuel.dev.notes.ui.app.NotesApp
import com.mnuel.dev.notes.ui.theme.NotesTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class Section(val route: String) {
    object Home : Section("Home")
    object EditNote : Section("Edit Note")
    object SelectCategory : Section("Select Category")
    object Search : Section("Search")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesTheme {
                NotesApp()
            }
        }
    }
}
