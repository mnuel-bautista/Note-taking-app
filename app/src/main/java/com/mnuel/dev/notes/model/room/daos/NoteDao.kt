package com.mnuel.dev.notes.model.room.daos

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.mnuel.dev.notes.model.room.entities.Note
import com.mnuel.dev.notes.model.room.entities.NoteWithCategory
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
abstract class NoteDao {

    @Transaction
    @Query("SELECT * FROM notes INNER JOIN notebooks WHERE notes.collectionId = notebooks.id")
    abstract suspend fun getNoteWithCategory(): NoteWithCategory

    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY datetime(creationDate) DESC")
    abstract fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isFavorite = 1 AND isDeleted = 0")
    abstract fun getFavoriteNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isDeleted = 1")
    abstract fun getDeletedNotes(): Flow<List<Note>>

    @RawQuery(observedEntities = [Note::class])
    abstract fun getAllNotes(query: SupportSQLiteQuery): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    abstract suspend fun getNoteById(id: Int): Note

    @Query("SELECT * FROM notes WHERE collectionId = :id")
    abstract fun getNotesByCollection(id: Int): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE :query || '%'")
    abstract fun search(query: String): Flow<List<Note>>

    suspend fun restoreNotes(noteIds: List<Int>) {
        noteIds.forEach {
            restoreNote(it)
        }
    }

    @Query("UPDATE notes SET isDeleted = 0 WHERE id = :id")
    abstract suspend fun restoreNote(id: Int)

    @Insert
    abstract suspend fun insert(note: Note)

    @Query("DELETE FROM notes WHERE id = :id")
    abstract suspend fun delete(id: Int)

    @Query("DELETE FROM notes WHERE isDeleted = 1")
    abstract suspend fun deleteAllNotesPermanently()


    suspend fun deleteNotes(notesIds: List<Int>) {
        notesIds.forEach {
            delete(it)
        }
    }

    @Update
    abstract suspend fun update(note: Note)

    @Query("UPDATE notes SET title = :title, content = :content, isFavorite = :isFavorite, isPinned = :isPinned, color = :color, collectionId = :categoryId, modificationDate = :modificationDate WHERE id = :id")
    abstract suspend fun update(
        id: Int,
        title: String,
        content: String,
        isFavorite: Boolean,
        isPinned: Boolean,
        categoryId: Int,
        color: Int,
        modificationDate: OffsetDateTime
    )

}