package com.mnuel.dev.notes.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.mnuel.dev.notes.MainActivity
import com.mnuel.dev.notes.domain.entities.Category
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.repositories.NotesRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class EditScreenTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var notesRepository: NotesRepository

    @Inject
    lateinit var collectionsRepository: CollectionsRepository

    @Before
    fun setUp() = runBlocking {
        hiltRule.inject()

        collectionsRepository.insert(Category(1, "Work"))
        collectionsRepository.insert(Category(2, "School"))
        collectionsRepository.insert(Category(3, "Math"))
    }

    @Test
    fun createNote() = runBlocking {
        val floatingButton = composeRule.onNode(hasContentDescription("Create New Note"))
        val confirmButton = composeRule.onNode(hasContentDescription("Confirm Note"))
        val title = composeRule.onNode(hasContentDescription("Note Title") and hasSetTextAction())
        val content = composeRule.onNode(hasContentDescription("Note Content") and hasSetTextAction())

        floatingButton.performClick()
        composeRule.onRoot().printToLog("EditNoteScreenTest")
        title.performTextInput("My first note")
        content.performTextInput("My first note content")
        confirmButton.performClick()

        val notes = notesRepository.getAllNotes().first()
        val insertedNote = notes.first()

        assertEquals("My first note", insertedNote.title)
        assertEquals("My first note content", insertedNote.content)
    }

}