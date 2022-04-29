package com.hiddendimension.pexelphotos.feature_photo_search.presentation

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.hiddendimension.pexelphotos.feature_photo_search.data.repository.PexelPhotoRepository

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: PexelPhotoRepository
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {

        if (modelClass.isAssignableFrom(PhotoSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhotoSearchViewModel(repository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")


    }
}