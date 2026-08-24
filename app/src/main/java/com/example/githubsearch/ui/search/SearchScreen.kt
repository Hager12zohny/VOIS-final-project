package com.example.githubsearch.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.githubsearch.data.model.GitHubUser
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onUserClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagedUsers = viewModel.pagedUsers.collectAsLazyPagingItems()

    // Persist first page of fresh results once they arrive - powers the
    // "save last search results locally" optional requirement.
    LaunchedEffect(pagedUsers.loadState.refresh, pagedUsers.itemCount) {
        if (pagedUsers.loadState.refresh is LoadState.NotLoading && pagedUsers.itemCount > 0) {
            val items = (0 until pagedUsers.itemCount).mapNotNull { pagedUsers[it] }
            viewModel.onResultsLoaded(items)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GitHub User Search", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search GitHub users...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSearch = { viewModel.onSearchTriggered() }
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Search
                )
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::onSearchTriggered,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.query.isNotBlank()
            ) {
                Text("Search")
            }

            Spacer(Modifier.height(12.dp))

            when {
                !uiState.hasSearched && uiState.cachedResults.isNotEmpty() -> {
                    Text(
                        text = "Last search results",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn {
                        items(uiState.cachedResults, key = { it.id }) { user ->
                            UserRow(user = user, onClick = { onUserClick(user.login) })
                        }
                    }
                }

                !uiState.hasSearched -> {
                    EmptyState(text = "Search for a GitHub username to get started.")
                }

                else -> {
                    PagedResultsList(pagedUsers = pagedUsers, onUserClick = onUserClick)
                }
            }
        }
    }
}

@Composable
private fun PagedResultsList(
    pagedUsers: LazyPagingItems<GitHubUser>,
    onUserClick: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(pagedUsers.itemCount) { index ->
                val user = pagedUsers[index]
                if (user != null) {
                    UserRow(user = user, onClick = { onUserClick(user.login) })
                }
            }

            // Loading indicator at the bottom while the next page loads
            // (the actual "pagination" trigger - Paging 3 auto-fetches
            // the next page as the user scrolls near the end of the list).
            pagedUsers.apply {
                when {
                    loadState.append is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        item {
                            Text(
                                "Couldn't load more results.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        if (pagedUsers.loadState.refresh is LoadState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        if (pagedUsers.loadState.refresh is LoadState.Error && pagedUsers.itemCount == 0) {
            EmptyState(text = "Something went wrong. Check your connection and try again.")
        }

        if (pagedUsers.loadState.refresh is LoadState.NotLoading && pagedUsers.itemCount == 0) {
            EmptyState(text = "No users found.")
        }
    }
}

@Composable
private fun UserRow(user: GitHubUser, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "${user.login} avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(user.login, fontWeight = FontWeight.SemiBold)
            Text(
                "ID: ${user.id}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Divider()
}

@Composable
private fun EmptyState(text: String) {
    Box(modifier = Modifier.fillMaxSize().padding(top = 48.dp), contentAlignment = Alignment.TopCenter) {
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
