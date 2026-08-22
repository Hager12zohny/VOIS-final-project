package com.example.githubsearch.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://api.github.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // GitHub's unauthenticated rate limit is low (60 req/hr).
        // Add an Authorization header here with a personal access token if you hit 403s:
        // .addInterceptor { chain ->
        //     val request = chain.request().newBuilder()
        //         .addHeader("Authorization", "Bearer YOUR_TOKEN")
        //         .build()
        //     chain.proceed(request)
        // }
        .build()

    val api: GitHubApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApiService::class.java)
    }
}
