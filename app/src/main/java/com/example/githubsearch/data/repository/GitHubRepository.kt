package com.example.githubsearch.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.githubsearch.data.api.GitHubApiService
import com.example.githubsearch.data.local.SearchCacheManager
import com.example.githubsearch.data.model.GitHubUser
import com.example.githubsearch.data.model.GitHubUserDetails
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for user search + details, sitting between the
 * ViewModels and the API/local cache. Kept interface-light so it's easy to
 * fake in unit tests.
 */
class GitHubRepository(
    private val api: GitHubApiService,
    private val cacheManager: SearchCacheManager
) {

    fun searchUsersPaged(query: String): Flow<PagingData<GitHubUser>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                com.example.githubsearch.data.paging.GitHubUserPagingSource(api, query)
            }
        ).flow
    }

    suspend fun getUserDetails(username: String): GitHubUserDetails {
        return api.getUserDetails(username)
    }

    suspend fun saveLastSearch(query: String, results: List<GitHubUser>) {
        cacheManager.saveLastSearch(query, results)
    }

    suspend fun getCachedResults(): List<GitHubUser> = cacheManager.getLastResults()

    val lastQuery: Flow<String> = cacheManager.lastQuery
}
