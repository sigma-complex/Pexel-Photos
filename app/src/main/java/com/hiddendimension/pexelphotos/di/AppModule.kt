package com.hiddendimension.pexelphotos.di

import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.hiddendimension.pexelphotos.feature_photo_search.data.repository.PexelPhotoRepository
import com.hiddendimension.pexelphotos.feature_photo_search.data.repository.PexelPhotoService
import com.hiddendimension.pexelphotos.feature_photo_search.presentation.ViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private fun providePhotoRepository(): PexelPhotoRepository {
        return PexelPhotoRepository(PexelPhotoService.create())
    }

    @Provides
    @Singleton
    fun provideViewModelFactory(owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelFactory(owner, providePhotoRepository())
    }
}