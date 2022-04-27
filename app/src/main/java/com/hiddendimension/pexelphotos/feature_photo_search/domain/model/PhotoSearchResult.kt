package com.hiddendimension.pexelphotos.feature_photo_search.domain.model

sealed class PhotoSearchResult {
    data class Success(val data: List<Photo>) : PhotoSearchResult()
    data class Error(val error: Exception) : PhotoSearchResult()

}