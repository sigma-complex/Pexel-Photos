package com.hiddendimension.pexelphotos.feature_photo_search.data.repository

import android.util.Log
import com.hiddendimension.pexelphotos.feature_photo_search.domain.model.Photo
import com.hiddendimension.pexelphotos.feature_photo_search.domain.model.PhotoSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import retrofit2.HttpException
import java.io.IOException

private const val PEXEL_STARTING_PAGE_INDEX = 1


class PexelPhotoRepository(private val service: PexelPhotoService) {

    private val inMemoryCache = mutableListOf<Photo>()

    private val searchResults = MutableSharedFlow<PhotoSearchResult>(replay = 1)

    private var lastRequestedPage = PEXEL_STARTING_PAGE_INDEX

    private var isRequestInProgress = false

    private var hasMoreData = true

    suspend fun getSearchResultStream(query: String): Flow<PhotoSearchResult> {
        Log.d("PhotoRepository", "New query: $query")
        lastRequestedPage = 1
        inMemoryCache.clear()
        requestAndSaveData(query)

        return searchResults
    }

    private suspend fun requestAndSaveData(query: String): Boolean {
        isRequestInProgress = true
        var successful = false

        try {
            val response = service.searchRepos(query, lastRequestedPage, NETWORK_PAGE_SIZE)
            Log.d("PhotoRepository", "apiQuery $query")

            Log.d("PhotoRepository", "response $response")
            val photos = response.photos
            inMemoryCache.addAll(photos)
            searchResults.emit(PhotoSearchResult.Success(inMemoryCache))

            hasMoreData = response.next_page!=(null)
            successful = true
        } catch (exception: IOException) {
            searchResults.emit(PhotoSearchResult.Error(exception))
        } catch (exception: HttpException) {
            searchResults.emit(PhotoSearchResult.Error(exception))
        }
        isRequestInProgress = false
        return successful
    }


    suspend fun requestMore(query: String) {
        if (isRequestInProgress || !hasMoreData) return
        val successful = requestAndSaveData(query)
        if (successful) {
            lastRequestedPage++
        }
    }


    companion object {
        const val NETWORK_PAGE_SIZE = 80
    }

}