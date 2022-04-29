package com.hiddendimension.pexelphotos.feature_photo_search.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.hiddendimension.pexelphotos.feature_photo_search.data.repository.PexelPhotoRepository
import javax.inject.Inject

class PhotoSearchViewModel @Inject constructor(
    private val repository: PexelPhotoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel()  {
}