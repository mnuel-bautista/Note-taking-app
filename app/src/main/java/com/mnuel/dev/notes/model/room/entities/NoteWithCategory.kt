package com.mnuel.dev.notes.model.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithCategory(
    @Embedded val category: Notebook,
    @Relation(
        parentColumn = "id",
        entityColumn = "collectionId",
    )
    val note: Note
)