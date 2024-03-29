package com.mnuel.dev.notes.di

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import com.mnuel.dev.notes.model.repositories.*
import com.mnuel.dev.notes.model.room.daos.NoteDao
import com.mnuel.dev.notes.ui.navigation.Routes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class NotesModule {

    @Provides
    fun provideNotesRepository(dao: NoteDao, handle: SavedStateHandle): NotesRepository {

        val key = "android-support-nav:controller:deepLinkIntent"
        val intent = handle.get<Intent>(key)

        if(intent == null) {
            return NotesRepositoryImpl(dao)
        } else {
            val uriString = intent.data.toString()
            when {
                uriString.contains("collections") -> {
                    val id = uriString.substringAfterLast("/").toInt()
                    return CollectionNotesRepository(dao, id)
                }
                uriString.contains(Routes.FAVORITES) -> {
                    return FavoriteNotesRepository(dao)
                }
            }
        }

        return NotesRepositoryImpl(dao)
    }

}