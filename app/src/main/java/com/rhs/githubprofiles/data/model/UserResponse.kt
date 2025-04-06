package com.rhs.githubprofiles.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * The UserResponse data class.
 *
 * @param login the username
 * @param name the name of the user
 * @param avatarUrl the URL of the user's avatar
 * @param bio the bio of the user
 * @param followers the number of followers
 * @param following the number of users followed by this user
 * @param profileUrl the URL of the user's profile
 * @param lastUpdated the last updated time
 */
@Entity(tableName = "users")
data class UserResponse(
    @PrimaryKey
    val login: String,
    val name: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    val bio: String?,
    val location: String?,
    val followers: Int = 0,
    val following: Int = 0,
    @SerializedName("html_url")
    val profileUrl: String,
    val lastUpdated: Long = System.currentTimeMillis()
) : java.io.Serializable {
    override fun toString(): String {
        return "UserResponse(login='$login', name=$name, avatarUrl='$avatarUrl', bio=$bio, followers=$followers, following=$following, profileUrl='$profileUrl', lastUpdated=$lastUpdated)"
    }
}