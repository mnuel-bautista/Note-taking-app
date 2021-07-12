package com.mnuel.dev.notes.model.repositories

import com.mnuel.dev.notes.model.room.daos.NoteDao
import com.mnuel.dev.notes.model.room.entities.Note
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime


class CollectionNotesRepository(
    private val notesDao: NoteDao,
    private val collectionId: Int,
): NotesRepository {

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getNotesByCollection(collectionId)
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