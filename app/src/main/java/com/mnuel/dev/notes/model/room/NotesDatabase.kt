package com.mnuel.dev.notes.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mnuel.dev.notes.model.room.daos.CollectionDao
import com.mnuel.dev.notes.model.room.daos.NoteDao
import com.mnuel.dev.notes.model.room.entities.Collection
import com.mnuel.dev.notes.model.room.entities.Note
import com.mnuel.dev.notes.model.room.entities.ToDo

@Database(entities = [Note::class, Collection::class, ToDo::class], version = 1, exportSchema = true)
@TypeConverters(NoteTypeConverters::class)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    abstract fun categoryDao(): CollectionDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun getDatabase(context: Context): NotesDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "notes_database"
                ).createFromAsset("notes1.db")
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}