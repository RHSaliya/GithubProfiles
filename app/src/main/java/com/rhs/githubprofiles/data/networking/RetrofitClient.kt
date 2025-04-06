package com.rhs.githubprofiles.data.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * The Retrofit client for the GitHub API.
 *
 * @property api the GitHub API interface
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.github.com/"

    /**
     * The GitHub API interface object.
     */
    val api: GitHubApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)
    }
}
