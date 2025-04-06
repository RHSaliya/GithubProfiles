package com.rhs.githubprofiles.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The Queries data class.
 *
 * @param query the query string
 * @param totalCount the total count of results
 * @param lastUpdated the last updated time
 */
@Entity(tableName = "queries_table")
data class QueriesModel(
    @PrimaryKey
    val query: String,
    val totalCount: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)