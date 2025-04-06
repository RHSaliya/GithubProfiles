package com.rhs.githubprofiles.data.networking

/**
 * The GitHubRepository class.
 *
 * This class is responsible for making network calls to the GitHub API.
 */
class GitHubRepository {
    private val api = RetrofitClient.api

    /**
     * Get the user profile for a given username.
     *
     * @param username the username
     * @return the user profile
     */
    suspend fun getUser(username: String) = api.getUser(username)

    /**
     * Get the followers for a given username.
     *
     * @param username the username
     * @return the followers list
     */
    suspend fun getFollowers(username: String) = api.getFollowers(username)

    /**
     * Get the following for a given username.
     *
     * @param username the username
     * @return the following list
     */
    suspend fun getFollowing(username: String) = api.getFollowing(username)

    /**
     * Search for users based on a query.
     *
     * @param query the search query
     * @param page the page number
     * @return the search response
     */
    suspend fun searchUsers(query: String, page: Int, perPage: Int) = api.searchUsers(query, page)
}
