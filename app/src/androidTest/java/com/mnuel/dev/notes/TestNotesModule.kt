package com.mnuel.dev.notes

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import com.mnuel.dev.notes.di.NotesModule
import com.mnuel.dev.notes.model.repositories.CollectionNotesRepository
import com.mnuel.dev.notes.model.repositories.FavoriteNotesRepository
import com.mnuel.dev.notes.model.repositories.NotesRepository
import com.mnuel.dev.notes.model.repositories.NotesRepositoryImpl
import com.mnuel.dev.notes.model.room.daos.NoteDao
import com.mnuel.dev.notes.ui.navigation.Routes
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [ViewModelComponent::class],
    replaces = [NotesModule::class]
)
class TestNotesModule{

    @Provides
    fun provideNotesRepository(dao: NoteDao, handle: SavedStateHandle): NotesRepository {

        val key = "android-support-nav:controller:deepLinkIntent"
        val intent = handle.get<Intent>(key)

        if(intent == null) {
            return NotesRepositoryImpl(dao)
        } else {
            val uriString = intent.data.toString()
            when {
                uriString.contains(Routes.COLLECTIONS) -> {
                    val id = uriString.substringAfterLast("/").toInt()
                    return CollectionNotesRepository(dao, id)
                }
                uriString.contains(Routes.FAVORITES.name) -> {
                    return FavoriteNotesRepository(dao)
                }
            }
        }

        return NotesRepositoryImpl(dao)
    }

}