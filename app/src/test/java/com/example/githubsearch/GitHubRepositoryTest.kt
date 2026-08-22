package com.example.githubsearch

import com.example.githubsearch.data.api.GitHubApiService
import com.example.githubsearch.data.local.SearchCacheManager
import com.example.githubsearch.data.model.GitHubUser
import com.example.githubsearch.data.model.GitHubUserDetails
import com.example.githubsearch.data.repository.GitHubRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GitHubRepositoryTest {

    private val api: GitHubApiService = mockk()
    private val cacheManager: SearchCacheManager = mockk(relaxed = true)
    private val repository = GitHubRepository(api, cacheManager)

    @Test
    fun `getUserDetails delegates to api and returns the response`() = runTest {
        val details = GitHubUserDetails(
            id = 1, login = "octocat", avatarUrl = "url", htmlUrl = "html",
            name = "The Octocat", bio = "bio", company = null, location = null,
            blog = null, email = null, followers = 100, following = 5,
            publicRepos = 10, publicGists = 2, createdAt = "2011-01-25T18:44:36Z"
        )
        coEvery { api.getUserDetails("octocat") } returns details

        val result = repository.getUserDetails("octocat")

        assertThat(result).isEqualTo(details)
    }

    @Test
    fun `saveLastSearch delegates to cache manager`() = runTest {
        val users = listOf(GitHubUser(1, "octocat", "avatar", "html", 1.0))

        repository.saveLastSearch("octo", users)

        coVerify { cacheManager.saveLastSearch("octo", users) }
    }

    @Test
    fun `getCachedResults returns results from cache manager`() = runTest {
        val users = listOf(GitHubUser(1, "octocat", "avatar", "html", 1.0))
        coEvery { cacheManager.getLastResults() } returns users

        val result = repository.getCachedResults()

        assertThat(result).isEqualTo(users)
    }
}
