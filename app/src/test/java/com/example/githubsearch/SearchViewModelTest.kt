package com.example.githubsearch

import com.example.githubsearch.data.model.GitHubUser
import com.example.githubsearch.data.repository.GitHubRepository
import com.example.githubsearch.ui.search.SearchViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class SearchViewModelTest {

    private val repository: GitHubRepository = mockk()
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { repository.lastQuery } returns flowOf("")
        coEvery { repository.getCachedResults() } returns emptyList()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state restores cached results`() = runTest {
        val cached = listOf(GitHubUser(1, "octocat", "avatar", "html", 1.0))
        coEvery { repository.getCachedResults() } returns cached

        val viewModel = SearchViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.cachedResults).isEqualTo(cached)
        assertThat(viewModel.uiState.value.hasSearched).isFalse()
    }

    @Test
    fun `onQueryChanged updates the query in state`() {
        val viewModel = SearchViewModel(repository)

        viewModel.onQueryChanged("octocat")

        assertThat(viewModel.uiState.value.query).isEqualTo("octocat")
    }

    @Test
    fun `onSearchTriggered with blank query does not mark hasSearched`() {
        val viewModel = SearchViewModel(repository)

        viewModel.onQueryChanged("   ")
        viewModel.onSearchTriggered()

        assertThat(viewModel.uiState.value.hasSearched).isFalse()
    }

    @Test
    fun `onSearchTriggered with valid query marks hasSearched true`() {
        val viewModel = SearchViewModel(repository)

        viewModel.onQueryChanged("octocat")
        viewModel.onSearchTriggered()

        assertThat(viewModel.uiState.value.hasSearched).isTrue()
    }
}
