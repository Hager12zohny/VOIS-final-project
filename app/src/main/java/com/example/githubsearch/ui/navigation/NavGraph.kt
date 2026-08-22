package com.example.githubsearch.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.githubsearch.data.repository.GitHubRepository
import com.example.githubsearch.ui.details.DetailsScreen
import com.example.githubsearch.ui.details.DetailsViewModelFactory
import com.example.githubsearch.ui.search.SearchScreen
import com.example.githubsearch.ui.search.SearchViewModel
import com.example.githubsearch.ui.search.SearchViewModelFactory

private object Routes {
    const val SEARCH = "search"
    const val DETAILS = "details/{username}"
    fun details(username: String) = "details/$username"
}

@Composable
fun AppNavGraph(repository: GitHubRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SEARCH) {

        composable(Routes.SEARCH) {
            val viewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory(repository))
            SearchScreen(
                viewModel = viewModel,
                onUserClick = { username -> navController.navigate(Routes.details(username)) }
            )
        }

        composable(
            route = Routes.DETAILS,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: return@composable
            val viewModel = viewModel<com.example.githubsearch.ui.details.DetailsViewModel>(
                factory = DetailsViewModelFactory(repository, username)
            )
            DetailsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
