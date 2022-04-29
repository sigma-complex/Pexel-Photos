package com.hiddendimension.pexelphotos.feature_photo_search.data.repository

import com.hiddendimension.pexelphotos.feature_photo_search.domain.model.PexelSearchResponse
import com.hiddendimension.pexelphotos.feature_photo_search.domain.model.Photo
import com.hiddendimension.pexelphotos.feature_photo_search.domain.model.Src
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.toHexString
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response
import java.util.*

class FakePhotoService {


    fun searchRepos(

        query: String,
        page: Int,
        itemsPerPage: Int
    ): PexelSearchResponse {

        if (HAS_IO_ERROR)
            throw IOException("Has io error flag is on")


        if (HAS_HTTP_ERROR)
            throw HttpException(
                Response.error<ResponseBody>(
                    500,
                    "Internal server error".toResponseBody("plain/text".toMediaTypeOrNull())
                )
            )

        if (HAS_AUTHORIZATION_ERROR)
            throw HttpException(
                Response.error<ResponseBody>(
                    401,
                    "{Authorization:Authorization key needed}".toResponseBody("plain/text".toMediaTypeOrNull())
                )
            )

        requestCounter++

        if (requestCounter > maxRequestNumber)
            throw HttpException(
                Response.error<ResponseBody>(
                    429,
                    "{Too many requests per hour. Please try again later.}".toResponseBody("plain/text".toMediaTypeOrNull())
                )
            )

        return PexelSearchResponse(
            next_page = if (HAS_NEXT_PAGE) (page + 1).toString() else null,
            page = page,
            per_page = itemsPerPage,
            photos = (0 until itemsPerPage).map {
                Photo(
                    id = page * 1000 + it,
                    alt = "$query should be here but might not contains that $query $it",
                    avg_color = ((Math.random() * 16777215).toInt() or (0xFF shl 24)).toHexString(),
                    photographer = "Photographer name : " + UUID.randomUUID().toString()
                        .substring(0, 15),
                    photographer_id = (Math.random() * 16777215).toInt(),
                    photographer_url = "https://randomphoto.com/photographer/$it",
                    src = Src(
                        landscape = "",
                        large = "",
                        large2x = "",
                        medium = "",
                        original = "",
                        portrait = "",
                        small = "",
                        tiny = ""
                    ),

                    url = "https://randomphoto.com/photo/" + page * 1000 + it,
                    height = (1080..2000).random(),
                    width = (600..1080).random()

                )
            },
            prev_page = (if (page > 1) page - 1 else 1).toString(),
            total_results = itemsPerPage
        )
    }

    companion object {
        var HAS_IO_ERROR = true
        var HAS_HTTP_ERROR = true
        var HAS_AUTHORIZATION_ERROR = true
        var HAS_NEXT_PAGE = false

        var requestCounter = 0
        const val maxRequestNumber = 200

    }
}