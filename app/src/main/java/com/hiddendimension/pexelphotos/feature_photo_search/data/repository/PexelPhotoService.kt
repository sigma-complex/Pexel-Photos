package com.hiddendimension.pexelphotos.feature_photo_search.data.repository

import com.hiddendimension.pexelphotos.BuildConfig
import com.hiddendimension.pexelphotos.feature_photo_search.domain.model.PexelSearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface PexelPhotoService {
    @Headers("Authorization:$TOKEN")
    @GET("search")
    suspend fun searchRepos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): PexelSearchResponse

    companion object {
        private const val BASE_URL = "https://api.pexels.com/v1/"
        private const val TOKEN = BuildConfig.API_KEY

        fun create(): PexelPhotoService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PexelPhotoService::class.java)
        }
    }
}