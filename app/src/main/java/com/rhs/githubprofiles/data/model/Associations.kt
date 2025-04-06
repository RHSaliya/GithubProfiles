package com.rhs.githubprofiles.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * The Associations data class.
 *
 * @param login username
 * @param followersList list of followers
 * @param followingList list of following
 * @param lastUpdated last updated time
 */
@Entity(tableName = "associations_table")
data class Associations (
    @PrimaryKey
    val login: String,
    var followersList : List<String> = emptyList(),
    var followingList : List<String> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis()
)