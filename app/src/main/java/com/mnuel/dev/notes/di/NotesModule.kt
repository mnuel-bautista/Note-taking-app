package com.mnuel.dev.notes.di

import android.content.Intent
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.mnuel.dev.notes.model.repositories.FavoriteNotesRepository
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.repositories.NotesRepositoryImpl
import com.mnuel.dev.notes.model.room.daos.NoteDao
import com.mnuel.dev.notes.ui.components.Routes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class NotesModule {

    @Provides
    fun provideRepository(dao: NoteDao, handle: SavedStateHandle): NotesRepository {

        val key = "android-support-nav:controller:deepLinkIntent"
        val intent = handle.get<Intent>(key)

        if(intent == null) {
            return NotesRepositoryImpl(dao)
        } else {
            val uriString = intent.data.toString()
            when {
                uriString.contains(Routes.COLLECTIONS.name) -> {
                    return NotesRepositoryImpl(dao)
                }
                uriString.contains(Routes.FAVORITES.name) -> {
                    return FavoriteNotesRepository(dao)
                }
            }
        }

        return NotesRepositoryImpl(dao)
    }

}