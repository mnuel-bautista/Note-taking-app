package com.mnuel.dev.notes.model.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithCategory(
    @Embedded val category: Collection,
    @Relation(
        parentColumn = "id",
        entityColumn = "collectionId",
    )
    val note: Note
)