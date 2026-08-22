package com.example.githubsearch.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.githubsearch.data.api.GitHubApiService
import com.example.githubsearch.data.model.GitHubUser
import retrofit2.HttpException
import java.io.IOException

/**
 * Drives the "load more search results when scrolling to the end of the list"
 * optional requirement, backed by GET /search/users?q={user}&page=N.
 */
class GitHubUserPagingSource(
    private val api: GitHubApiService,
    private val query: String
) : PagingSource<Int, GitHubUser>() {

    override fun getRefreshKey(state: PagingState<Int, GitHubUser>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GitHubUser> {
        val page = params.key ?: 1
        return try {
            if (query.isBlank()) {
                return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
            }

            val response = api.searchUsers(query = query, page = page, perPage = params.loadSize)

            LoadResult.Page(
                data = response.items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.items.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}
