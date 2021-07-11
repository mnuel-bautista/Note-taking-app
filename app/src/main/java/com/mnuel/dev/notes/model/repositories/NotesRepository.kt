package com.mnuel.dev.notes.model.repositories

import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.mnuel.dev.notes.model.room.daos.NoteDao
import com.mnuel.dev.notes.model.room.entities.Note
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface NotesRepository {

    fun getAllNotes(): Flow<List<Note>>

    fun search(query: String): Flow<List<Note>>

    suspend fun getNoteById(id: Int): Note

    suspend fun insert(note: Note)

    suspend fun delete(id: Int)

    suspend fun update(note: Note)

    suspend fun restoreNotes(notesIds: List<Int>)

    /**
     * Method to only update the passed arguments of the Note instance.
     * */
    suspend fun update(
        id: Int,
        title: String,
        content: String,
        isFavorite: Boolean,
        isPinned: Boolean,
        color: Int,
        categoryId: Int,
        modificationDate: OffsetDateTime,
    )

    /**
     * Only non-null values will be used for the selection.
     * */
    suspend fun getAllNotes(isFavorite: Boolean?, collectionId: Int?): Flow<List<Note>>

    suspend fun getFavoriteNotes(): Flow<List<Note>>

    suspend fun getNotesByCollection(categoryId: Int): Flow<List<Note>>

    fun getDeletedNotes(): Flow<List<Note>>

    suspend fun deleteAllNotesPermanently()

    suspend fun deleteNotes(notesIds: List<Int>)

}

class NotesRepositoryImpl(private val notesDao: NoteDao) : NotesRepository {

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes()
    }

    override suspend fun getAllNotes(isFavorite: Boolean?, collectionId: Int?): Flow<List<Note>> {
        val queryBuilder = SupportSQLiteQueryBuilder.builder("notes")
        var selection = ""
        var argsCounter = 0

        if(isFavorite != null) {
            selection = "isFavorite = ? "
            argsCounter++
        }

        if(collectionId != null) {
            selection += if(argsCounter > 0) {
                " AND collectionId = ? "
            } else {
                " collectionId = ?"
            }
        }

        val args = listOfNotNull(isFavorite, collectionId)
            .toTypedArray()

        val query = queryBuilder.selection(selection, args)
            .create()
        return notesDao.getAllNotes(query)
    }

    override fun search(query: String): Flow<List<Note>> {
        return notesDao.search(query)
    }

    override suspend fun getNoteById(id: Int): Note {
        return notesDao.getNoteById(id)
    }

    override suspend fun insert(note: Note) {
        notesDao.insert(note)
    }

    override suspend fun delete(id: Int) {
        notesDao.delete(id)
    }

    override suspend fun update(note: Note) {
        notesDao.update(note)
    }

    override suspend fun update(
        id: Int,
        title: String,
        content: String,
        isFavorite: Boolean,
        isPinned: Boolean,
        color: Int,
        categoryId: Int,
        modificationDate: OffsetDateTime
    ) {
        notesDao.update(id, title, content, isFavorite, isPinned, categoryId, color, modificationDate)
    }

    override suspend fun restoreNotes(notesIds: List<Int>) {
        notesDao.restoreNotes(notesIds)
    }

    override suspend fun getFavoriteNotes(): Flow<List<Note>> {
        return notesDao.getFavoriteNotes()
    }

    override suspend fun getNotesByCollection(collectionId: Int): Flow<List<Note>> {
        return notesDao.getNotesByCollection(collectionId)
    }

    override fun getDeletedNotes(): Flow<List<Note>> {
        return notesDao.getDeletedNotes()
    }

    override suspend fun deleteAllNotesPermanently() {
        notesDao.deleteAllNotesPermanently()
    }

    override suspend fun deleteNotes(notesIds: List<Int>) {
        notesDao.deleteNotes(notesIds)
    }

}