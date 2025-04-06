package com.rhs.githubprofiles.data.model

import com.google.gson.annotations.SerializedName

/**
 * The SearchResponse data class.
 *
 * @param users the list of users
 * @param totalCount the total count of users
 */
data class SearchResponse(
    @SerializedName("items")
    val users: List<UserResponse>,

    @SerializedName("total_count")
    val totalCount: Int,
)