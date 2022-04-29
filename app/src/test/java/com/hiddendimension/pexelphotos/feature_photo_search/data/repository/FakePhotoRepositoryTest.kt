package com.hiddendimension.pexelphotos.feature_photo_search.data.repository

import android.annotation.SuppressLint
import com.google.common.truth.Truth.assertThat
import com.hiddendimension.pexelphotos.feature_photo_search.domain.model.PexelSearchResponse
import com.hiddendimension.pexelphotos.feature_photo_search.domain.model.Photo
import com.hiddendimension.pexelphotos.feature_photo_search.domain.model.PhotoSearchResult
import junit.framework.TestCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

private const val PEXEL_STARTING_PAGE_INDEX = 1


class FakePexelPhotoRepositoryTest : TestCase() {

    private val inMemoryCache = mutableListOf<Photo>()

    private val searchResults = MutableSharedFlow<PhotoSearchResult>(replay = 1)

    private var lastRequestedPage = PEXEL_STARTING_PAGE_INDEX

    private var isRequestInProgress = false

    private var hasMoreData = true

    private var service = FakePhotoService()


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test if there is any authorization error`() {

        FakePhotoService.HAS_IO_ERROR=false
        FakePhotoService.HAS_HTTP_ERROR=false
        FakePhotoService.HAS_AUTHORIZATION_ERROR = false
        FakePhotoService.requestCounter=0

        val testQuery = "nature"

        lastRequestedPage = 1
        inMemoryCache.clear()

        CoroutineScope(UnconfinedTestDispatcher()).launch {
            requestAndSaveData(testQuery)
        }

        assertThat(inMemoryCache.size).isAtLeast(NETWORK_PAGE_SIZE)


    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetSearchResultStream() {

        FakePhotoService.HAS_IO_ERROR=false
        FakePhotoService.HAS_HTTP_ERROR=false
        FakePhotoService.HAS_AUTHORIZATION_ERROR = false
        FakePhotoService.requestCounter=0

        val testQuery = "nature"

        lastRequestedPage = 1
        inMemoryCache.clear()

        CoroutineScope(UnconfinedTestDispatcher()).launch {
            requestAndSaveData(testQuery)
        }

        assertThat(inMemoryCache.size).isAtLeast(NETWORK_PAGE_SIZE)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test if there is next page of result`() {

        FakePhotoService.HAS_IO_ERROR=false
        FakePhotoService.HAS_HTTP_ERROR=false
        FakePhotoService.HAS_AUTHORIZATION_ERROR = false
        FakePhotoService.requestCounter=0

        val testQuery = "nature"

        FakePhotoService.HAS_NEXT_PAGE = true


        lastRequestedPage = 1
        inMemoryCache.clear()

        var response: PexelSearchResponse? = null

        CoroutineScope(UnconfinedTestDispatcher()).launch {
            response = service.searchRepos(
                testQuery, lastRequestedPage,
                NETWORK_PAGE_SIZE
            )
        }

        assertThat(response?.next_page).isNotNull()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test if there is no next page of result`() {

        FakePhotoService.HAS_IO_ERROR=false
        FakePhotoService.HAS_HTTP_ERROR=false
        FakePhotoService.HAS_AUTHORIZATION_ERROR = false
        FakePhotoService.requestCounter=0

        val testQuery = "nature"
        FakePhotoService.HAS_NEXT_PAGE = false

        lastRequestedPage = 1
        inMemoryCache.clear()

        var response: PexelSearchResponse? = null

        CoroutineScope(UnconfinedTestDispatcher()).launch {
            response = service.searchRepos(
                testQuery, lastRequestedPage,
                NETWORK_PAGE_SIZE
            )
        }

        assertThat(response?.next_page).isNull()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test if the image alt contains the query`() {

        FakePhotoService.HAS_IO_ERROR=false
        FakePhotoService.HAS_HTTP_ERROR=false
        FakePhotoService.HAS_AUTHORIZATION_ERROR = false
        FakePhotoService.requestCounter=0

        val testQuery = "nature"
        var successful = false
        FakePhotoService.requestCounter=0

        CoroutineScope(UnconfinedTestDispatcher()).launch {
            successful = requestAndSaveData(testQuery)
        }

        for (x in inMemoryCache) {
            assertThat(x.alt).contains(testQuery + "")
        }

        assertThat(successful).isTrue()

    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test if there is any exception`() {

        FakePhotoService.HAS_IO_ERROR=false
        FakePhotoService.HAS_HTTP_ERROR=false
        FakePhotoService.HAS_AUTHORIZATION_ERROR=false

        var response: PexelSearchResponse? = null

        val testQuery = "nature"
        FakePhotoService.requestCounter=0

        CoroutineScope(UnconfinedTestDispatcher()).launch {
            response = service.searchRepos(
                testQuery, lastRequestedPage,
                NETWORK_PAGE_SIZE
            )
        }

        assertThat(response).isNotNull()

    }


    @SuppressLint("CheckResult")
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testRequestMore() {

        FakePhotoService.HAS_NEXT_PAGE = true
        FakePhotoService.requestCounter=0

        isRequestInProgress = false
        val testQuery = "nature"
        var successful = false

        if (isRequestInProgress) {
            assertThat(fail("request in progress"))
        }


        CoroutineScope(UnconfinedTestDispatcher()).launch {
            successful = requestAndSaveData(testQuery)
        }

        if (successful) {
            lastRequestedPage++
            assertThat(successful).isTrue()
            assertThat(inMemoryCache.size).isAtLeast(NETWORK_PAGE_SIZE)
        }
    }


    @SuppressLint("CheckResult")
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test if two many requests are made`() {

        FakePhotoService.HAS_NEXT_PAGE = true
        FakePhotoService.requestCounter=0

        isRequestInProgress = false
        val testQuery = "nature"

        if (isRequestInProgress) {
            assertThat(fail("request in progress"))
        }


        for (i in 1..199) {

            var response: PexelSearchResponse? = null
            CoroutineScope(UnconfinedTestDispatcher()).launch {
                response = service.searchRepos(
                    testQuery, lastRequestedPage,
                    NETWORK_PAGE_SIZE
                )
            }

            assertThat(response?.photos?.size).isAtLeast(1)
            assertThat(FakePhotoService.requestCounter).isLessThan(FakePhotoService.maxRequestNumber)
        }
    }


    private suspend fun requestAndSaveData(query: String): Boolean {
        isRequestInProgress = true
        var successful = false

        try {
            val response = service.searchRepos(
                query, lastRequestedPage,
                NETWORK_PAGE_SIZE
            )

            val photos = response.photos
            inMemoryCache.addAll(photos)
            searchResults.emit(PhotoSearchResult.Success(inMemoryCache))

            hasMoreData = response.next_page != (null)
            successful = true
        } catch (exception: IOException) {
            searchResults.emit(PhotoSearchResult.Error(exception))
        } catch (exception: HttpException) {
            searchResults.emit(PhotoSearchResult.Error(exception))
        }
        isRequestInProgress = false
        return successful
    }


    companion object {
        const val NETWORK_PAGE_SIZE = 80
    }
}