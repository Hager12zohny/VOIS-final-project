package com.example.githubsearch

import androidx.paging.PagingSource
import com.example.githubsearch.data.api.GitHubApiService
import com.example.githubsearch.data.model.GitHubUser
import com.example.githubsearch.data.model.SearchResponse
import com.example.githubsearch.data.paging.GitHubUserPagingSource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GitHubUserPagingSourceTest {

    private val api: GitHubApiService = mockk()

    private fun user(id: Long, login: String) =
        GitHubUser(id = id, login = login, avatarUrl = "", htmlUrl = "", score = 1.0)

    @Test
    fun `load returns page of results with correct next key`() = runTest {
        val users = listOf(user(1, "octocat"), user(2, "octodog"))
        coEvery { api.searchUsers(query = "octo", page = 1, perPage = any()) } returns
            SearchResponse(totalCount = 2, incompleteResults = false, items = users)

        val pagingSource = GitHubUserPagingSource(api, "octo")
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val page = result as PagingSource.LoadResult.Page
        assertThat(page.data).hasSize(2)
        assertThat(page.data.map { it.login }).containsExactly("octocat", "octodog")
        assertThat(page.prevKey).isNull()
        assertThat(page.nextKey).isEqualTo(2)
    }

    @Test
    fun `load returns null next key when page is empty`() = runTest {
        coEvery { api.searchUsers(query = "zzz", page = 1, perPage = any()) } returns
            SearchResponse(totalCount = 0, incompleteResults = false, items = emptyList())

        val pagingSource = GitHubUserPagingSource(api, "zzz")
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        val page = result as PagingSource.LoadResult.Page
        assertThat(page.nextKey).isNull()
    }

    @Test
    fun `load returns empty page immediately for blank query`() = runTest {
        val pagingSource = GitHubUserPagingSource(api, "")
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        val page = result as PagingSource.LoadResult.Page
        assertThat(page.data).isEmpty()
    }

    @Test
    fun `load returns Error result on IOException`() = runTest {
        coEvery { api.searchUsers(query = "octo", page = 1, perPage = any()) } throws
            java.io.IOException("network down")

        val pagingSource = GitHubUserPagingSource(api, "octo")
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Error::class.java)
    }
}
