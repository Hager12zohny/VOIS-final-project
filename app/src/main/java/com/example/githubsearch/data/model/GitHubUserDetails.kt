package com.example.githubsearch.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response from GET https://api.github.com/users/{username}
 * Extra fields beyond id/avatar are the "additional fields of your choice"
 * required by the brief - shown on the details screen.
 */
data class GitHubUserDetails(
    @SerializedName("id") val id: Long,
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("name") val name: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("company") val company: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("blog") val blog: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("followers") val followers: Int,
    @SerializedName("following") val following: Int,
    @SerializedName("public_repos") val publicRepos: Int,
    @SerializedName("public_gists") val publicGists: Int,
    @SerializedName("created_at") val createdAt: String?
)
