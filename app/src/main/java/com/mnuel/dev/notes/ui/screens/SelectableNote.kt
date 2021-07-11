package com.mnuel.dev.notes.ui.screens

import com.mnuel.dev.notes.model.room.entities.Note
import java.time.OffsetDateTime

/**
 * Represents a note that can be selected. Contains all the fields that a note has, plus the boolean
 * field indicating whether the note is selected or not.
 * */
data class SelectableNote(
    val id: Int,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val isDeleted: Boolean = false,
    val isFavorite: Boolean = false,
    val creationDate: OffsetDateTime = OffsetDateTime.now(),
    val modificationDate: OffsetDateTime = OffsetDateTime.now(),
    val color: Int = 0,
    val collectionId: Int = 1,
    val selected: Boolean
) {

    companion object {
        fun map(note: Note): SelectableNote {
            return SelectableNote(
                id = note.id,
                title = note.title,
                content = note.content,
                isPinned = note.isPinned,
                isDeleted = note.isDeleted,
                isFavorite = note.isFavorite,
                creationDate = note.creationDate,
                modificationDate = note.modificationDate,
                color = note.color,
                collectionId = note.collectionId,
                selected = false
            )
        }
    }
}