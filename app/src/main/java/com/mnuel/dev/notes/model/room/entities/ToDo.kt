package com.mnuel.dev.notes.model.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class ToDo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val content: String,
    val isDone: Boolean,
    val noteId: Int,
)