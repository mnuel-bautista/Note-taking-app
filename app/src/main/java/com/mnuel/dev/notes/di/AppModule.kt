package com.mnuel.dev.notes.di

import android.content.Context
import com.mnuel.dev.notes.model.repositories.CollectionsRepository
import com.mnuel.dev.notes.model.repositories.CollectionsRepositoryImpl
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.repositories.NotesRepositoryImpl
import com.mnuel.dev.notes.model.room.NotesDatabase
import com.mnuel.dev.notes.model.room.daos.CollectionDao
import com.mnuel.dev.notes.model.room.daos.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): NotesDatabase {
        return NotesDatabase.getDatabase(context)
    }

    @Provides
    fun providesNoteDao(database: NotesDatabase): NoteDao{
        return database.noteDao()
    }

    @Singleton
    @Provides
    fun providesCategoriesRepository(dao: CollectionDao): CollectionsRepository {
        return CollectionsRepositoryImpl(dao)
    }

    @Provides
    fun providesCategoriesDao(database: NotesDatabase): CollectionDao {
        return database.categoryDao()
    }


}