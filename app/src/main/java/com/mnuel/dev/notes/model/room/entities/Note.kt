package com.mnuel.dev.notes.model.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val isDeleted: Boolean = false,
    val isFavorite: Boolean = false,
    val creationDate: OffsetDateTime = OffsetDateTime.now(),
    val modificationDate: OffsetDateTime = OffsetDateTime.now(),
    val color: Int = 0,
    val collectionId: Int = 1,
)