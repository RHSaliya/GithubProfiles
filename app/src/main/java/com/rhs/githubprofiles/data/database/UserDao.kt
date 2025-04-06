package com.rhs.githubprofiles.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rhs.githubprofiles.data.model.UserResponse

/**
 * The Data Access Object for the User table.
 */
@Dao
interface UserDao {

    /**
     * Get the profile for a given username.
     *
     * @param username the username
     * @return the profile
     */
    @Query("SELECT * FROM users WHERE login = :username")
    suspend fun getProfile(username: String): UserResponse?

    /**
     * Get the profile for a given username.
     *
     * @param profile the profile
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserResponse)

    /**
     * Get the profile for a given username.
     *
     * @param query the username
     * @return the list of profiles
     */
    @Query("SELECT * FROM users WHERE login like :query")
    suspend fun searchProfile(query: String): List<UserResponse>
}