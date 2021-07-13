package com.mnuel.dev.notes.model.repositories

import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.mnuel.dev.notes.model.repositories.NotesRepository.Companion.CREATED_FIELD
import com.mnuel.dev.notes.model.repositories.NotesRepository.Companion.MODIFIED_FIELD
import com.mnuel.dev.notes.model.repositories.NotesRepository.Companion.TITLE_FIELD
import com.mnuel.dev.notes.model.room.daos.NoteDao
import com.mnuel.dev.notes.model.room.entities.Note
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface NotesRepository {

    fun getAllNotes(): Flow<List<Note>>

    /**
     * @param field The field used to sort the notes.
     * */
    fun getAllNotesSorted(field: Int, asc: Boolean): Flow<List<Note>>

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

    fun getDeletedNotes(): Flow<List<Note>>

    suspend fun deleteAllNotesPermanently()

    suspend fun deleteNotes(notesIds: List<Int>)

    companion object {

        const val TITLE_FIELD = 1
        const val CREATED_FIELD = 2
        const val MODIFIED_FIELD = 3

    }

}

class NotesRepositoryImpl(private val notesDao: NoteDao) : NotesRepository {

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes()
    }

    override fun getAllNotesSorted(field: Int, asc: Boolean): Flow<List<Note>> {

        val queryBuilder = SupportSQLiteQueryBuilder.builder("notes")

        val order = if(asc) "ASC" else "DESC"
        when (field) {
            TITLE_FIELD -> {
                queryBuilder.orderBy("title $order")
            }
            CREATED_FIELD -> {

                queryBuilder.orderBy("datetime(creationDate) $order")
            }
            MODIFIED_FIELD -> {
                queryBuilder.orderBy("datetime(modificationDate) $order")
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
        notesDao.update(
            id,
            title,
            content,
            isFavorite,
            isPinned,
            categoryId,
            color,
            modificationDate
        )
    }

    override suspend fun restoreNotes(notesIds: List<Int>) {
        notesDao.restoreNotes(notesIds)
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