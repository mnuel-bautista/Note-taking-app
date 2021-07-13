package com.mnuel.dev.notes.model.repositories

import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.mnuel.dev.notes.model.room.daos.NoteDao
import com.mnuel.dev.notes.model.room.entities.Note
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime


/**
 * A repository to retrieve only the notes marked as favorite.
 * */
class FavoriteNotesRepository(
    private val notesDao: NoteDao,
): NotesRepository {

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getFavoriteNotes()
    }

    override fun getAllNotesSorted(field: Int, asc: Boolean): Flow<List<Note>> {
        val queryBuilder = SupportSQLiteQueryBuilder.builder("notes")
            .selection("WHERE isFavorite = ?", arrayOf(1))

        val order = if(asc) "ASC" else "DESC"
        when (field) {
            NotesRepository.TITLE_FIELD -> {
                queryBuilder.orderBy(" ORDER BY title $order")
            }
            NotesRepository.CREATED_FIELD -> {

                queryBuilder.orderBy("ORDER BY datetime(creationDate) $order")
            }
            NotesRepository.MODIFIED_FIELD -> {
                queryBuilder.orderBy("ORDER BY datetime(modificationDate) $order")
            }
        }

        val query = queryBuilder.create()

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

    override fun getDeletedNotes(): Flow<List<Note>> {
        return notesDao.getDeletedNotes()
    }

    override suspend fun deleteAllNotesPermanently() {
        return notesDao.deleteAllNotesPermanently()
    }

    override suspend fun deleteNotes(notesIds: List<Int>) {
        return notesDao.deleteNotes(notesIds)
    }

}