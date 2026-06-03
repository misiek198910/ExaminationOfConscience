package com.example.examinationofconscience.remote

import retrofit2.Response
import retrofit2.http.GET

interface ParishApiService {
    @GET("news")
    suspend fun getNewsFeed(): Response<List<NewsResponse>>
}