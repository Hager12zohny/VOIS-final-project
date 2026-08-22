# GitHub Search App

A native Android app (Kotlin + Jetpack Compose) that lets you search for GitHub users and view their profile details.

## What it does

You type a username into a search bar, and the app queries GitHub's Search Users API to show a scrollable list of matching users (avatar + username). Scrolling to the bottom loads more results automatically. Tapping a user opens a details screen with their full profile — name, bio, followers/following, public repos, company, location, etc., pulled from GitHub's User Details API. The last search is saved on the device, so reopening the app shows those results again instead of a blank screen.

## Architecture

The app follows MVVM (Model-View-ViewModel):

**UI (Compose screens)** → observes state from → **ViewModel** → asks for data from → **Repository** → which talks to → **Retrofit (network)** and **DataStore (local cache)**

The idea is each layer only knows about the layer directly below it. The UI never talks to the network directly; it just reacts to state the ViewModel exposes. The ViewModel never talks to Retrofit or DataStore directly; it goes through the Repository. This separation is what makes the app testable — in tests, we can swap the Repository for a fake one instead of hitting the real GitHub API.

## Files, explained

**`MainActivity.kt`** — the entry point. Creates the Repository (wiring together the network client and the local cache) and sets the screen content to the navigation graph.

### Data layer

- **`data/model/SearchResponse.kt`** — matches the JSON shape returned by the Search Users API. `GitHubUser` holds the small set of fields that endpoint gives you (id, username, avatar, profile URL).
- **`data/model/GitHubUserDetails.kt`** — matches the JSON from the User Details endpoint, which has much more (bio, followers, company, etc.) — shown on the details screen.
- **`data/api/GitHubApiService.kt`** — the Retrofit interface describing the two API calls (search users, get user details). Retrofit builds the actual network requests from this automatically.
- **`data/api/RetrofitInstance.kt`** — sets up one shared Retrofit client used everywhere in the app.
- **`data/paging/GitHubUserPagingSource.kt`** — powers infinite scroll. Fetches one page of results at a time and tells the UI whether there's a next page to load.
- **`data/local/SearchCacheManager.kt`** — saves the last search query and its results to the device using DataStore, and reads them back on app launch.
- **`data/repository/GitHubRepository.kt`** — the single place ViewModels go to for data, whether it's coming from the network or the local cache.

### UI layer

- **`ui/search/SearchViewModel.kt`** — holds the search bar's state (what's typed, whether a search has run) and exposes the paged list of results. Also triggers saving results to the cache and restoring them on startup.
- **`ui/search/SearchScreen.kt`** — the search screen itself: text field, search button, and the scrolling list of results.
- **`ui/details/DetailsViewModel.kt`** — fetches one user's full profile when the details screen opens.
- **`ui/details/DetailsScreen.kt`** — the profile screen: avatar, bio, stats, and other details.
- **`ui/navigation/NavGraph.kt`** — defines the two screens and how tapping a user takes you from the search screen to the details screen.
- **`ui/theme/`** — colors and typography used across the app.

### Tests (`app/src/test/`)

- **`GitHubUserPagingSourceTest.kt`** — checks that pagination logic works correctly (pages load, next page detection, error handling).
- **`GitHubRepositoryTest.kt`** — checks the repository correctly passes data between the network/cache and whoever's asking for it.
- **`SearchViewModelTest.kt`** — checks the search screen's state behaves correctly as you type and search.

All three use fake (mocked) dependencies instead of real network calls, which is the standard way to write unit tests — fast, reliable, and not dependent on GitHub's servers being reachable.