package com.mnuel.dev.notes

import android.content.Context
import androidx.room.Room
import com.mnuel.dev.notes.di.AppModule
import com.mnuel.dev.notes.model.repositories.NotebooksRepository
import com.mnuel.dev.notes.model.repositories.NotebooksRepositoryImpl
import com.mnuel.dev.notes.model.room.NotesDatabase
import com.mnuel.dev.notes.model.room.daos.CollectionDao
import com.mnuel.dev.notes.model.room.daos.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
class TestModule {

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): NotesDatabase {
        return Room.inMemoryDatabaseBuilder(context, NotesDatabase::class.java)
            .build()
    }

    @Provides
    fun providesNoteDao(database: NotesDatabase): NoteDao {
        return database.noteDao()
    }

    @Singleton
    @Provides
    fun providesCategoriesRepository(dao: CollectionDao): NotebooksRepository {
        return NotebooksRepositoryImpl(dao)
    }

    @Provides
    fun providesCategoriesDao(database: NotesDatabase): CollectionDao {
        return database.categoryDao()
    }


}