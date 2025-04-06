package com.rhs.githubprofiles.data.networking

import com.rhs.githubprofiles.data.model.SearchResponse
import com.rhs.githubprofiles.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The GitHub API interface.
 */
interface GitHubApi {

    /**
     * Get the user profile for a given username.
     *
     * @param username the username
     * @return the user profile
     */
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): Response<UserResponse>

    /**
     * Get the user profile for a given username.
     *
     * @param username the username
     * @return the user profile list
     */
    @GET("users/{username}/followers")
    suspend fun getFollowers(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<List<UserResponse>>

    /**
     * Get the user profile for a given username.
     *
     * @param username the username
     * @return the user profile list
     */
    @GET("users/{username}/following")
    suspend fun getFollowing(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<List<UserResponse>>

    /**
     * Get the user profile for a given username.
     *
     * @param query the username
     * @param page the page number
     * @param perPage the number of items per page
     * @return the user profile list
     */
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<SearchResponse>
}
