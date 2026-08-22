package com.example.githubsearch.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper from GET https://api.github.com/search/users?q={user}&page=1
 */
data class SearchResponse(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("incomplete_results") val incompleteResults: Boolean,
    @SerializedName("items") val items: List<GitHubUser>
)

/**
 * A single user item as returned by the Search Users API.
 * This is intentionally lightweight - the Search API does not return
 * full profile info (bio, followers, etc.), only enough to render a list row.
 */
data class GitHubUser(
    @SerializedName("id") val id: Long,
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("score") val score: Double
)
