# GitHub Search App

A native Android app (Kotlin + Jetpack Compose) that searches GitHub users and shows their profile details. Built to satisfy the sample project brief, **including all three optional requirements**:

- ✅ Pagination — loads more results as you scroll (Paging 3)
- ✅ Local caching — last search results are saved and shown on relaunch (DataStore)
- ✅ Unit tests — paging source, repository, and view model (JUnit + MockK + Truth)

## How to open the project

1. Install **Android Studio** (Koala or newer recommended).
2. `File > Open`, select the `GitHubSearchApp` folder (the one containing `settings.gradle.kts`).
3. Let Gradle sync — it will download the dependencies listed in `app/build.gradle.kts` automatically. You need an internet connection for this step.
4. Run on an emulator or physical device (min SDK 24 / Android 7.0+).

No API key is required — the GitHub Search and User endpoints work unauthenticated, just at a lower rate limit (60 requests/hour per IP). If you hit `403` errors while testing, see the commented-out `Authorization` header in `RetrofitInstance.kt` and add a personal access token there.

## Project structure

```
app/src/main/java/com/example/githubsearch/
├── MainActivity.kt                  # Entry point, wires up repository + nav
├── data/
│   ├── api/
│   │   ├── GitHubApiService.kt      # Retrofit interface (search + user details)
│   │   └── RetrofitInstance.kt      # Retrofit/OkHttp singleton
│   ├── model/
│   │   ├── SearchResponse.kt        # /search/users response + GitHubUser
│   │   └── GitHubUserDetails.kt     # /users/{username} response
│   ├── paging/
│   │   └── GitHubUserPagingSource.kt  # Paging 3 source -> infinite scroll
│   ├── local/
│   │   └── SearchCacheManager.kt    # DataStore cache of last query/results
│   └── repository/
│       └── GitHubRepository.kt      # Single source of truth for the UI
└── ui/
    ├── search/
    │   ├── SearchScreen.kt          # Search bar + paged list + cached view
    │   ├── SearchViewModel.kt
    │   └── SearchViewModelFactory.kt
    ├── details/
    │   ├── DetailsScreen.kt         # Profile details screen
    │   └── DetailsViewModel.kt
    ├── navigation/
    │   └── NavGraph.kt              # Compose Navigation: search <-> details
    └── theme/
        ├── Theme.kt
        └── Type.kt

app/src/test/java/com/example/githubsearch/
├── GitHubUserPagingSourceTest.kt
├── GitHubRepositoryTest.kt
└── SearchViewModelTest.kt
```

## Architecture

MVVM, roughly:

`Compose UI` → observes → `ViewModel (StateFlow)` → calls → `Repository` → calls → `Retrofit API` / `DataStore cache`

- **SearchViewModel** exposes a `Flow<PagingData<GitHubUser>>` built from `GitHubUserPagingSource`, which is what powers infinite scroll. It also restores the last cached search on cold start and re-saves results to `SearchCacheManager` whenever a fresh search's first page loads.
- **DetailsViewModel** fetches full profile info for the tapped user via `GET /users/{username}` and exposes a simple `Loading / Success / Error` state.
- ViewModels take their dependencies through a small manual factory (`SearchViewModelFactory`, `DetailsViewModelFactory`) rather than a DI framework, to keep the sample easy to read — swap in Hilt/Koin if the app grows.

## Running the tests

In Android Studio: right-click `app/src/test` → **Run 'Tests in githubsearch'**, or from the command line:

```bash
./gradlew testDebugUnitTest
```

## Pushing to GitHub

```bash
cd GitHubSearchApp
git init
git add .
git commit -m "Initial commit: GitHub Search App"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```

## Notes / possible extensions

- Add a "Retry" affordance for `refresh` errors on the search list (details screen already has one).
- Swap manual ViewModel factories for Hilt if the app grows past two screens.
- Add an in-memory `Coil` disk cache config if you want avatar images to survive offline reopens too (currently only the user list/metadata is cached, not images).
