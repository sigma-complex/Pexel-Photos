package com.hiddendimension.pexelphotos.feature_photo_search.domain.model

data class PexelSearchResponse(
    val next_page: String,
    val page: Int,
    val per_page: Int,
    val photos: List<Photo>,
    val prev_page: String,
    val total_results: Int
)