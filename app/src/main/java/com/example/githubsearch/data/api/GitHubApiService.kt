package com.example.githubsearch.data.api

import com.example.githubsearch.data.model.GitHubUserDetails
import com.example.githubsearch.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {

    /**
     * GitHub Search Users API
     * https://api.github.com/search/users?q={user}&page=1
     */
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 20
    ): SearchResponse

    /**
     * GitHub User Details API
     * https://api.github.com/users/{username}
     */
    @GET("users/{username}")
    suspend fun getUserDetails(
        @Path("username") username: String
    ): GitHubUserDetails
}
