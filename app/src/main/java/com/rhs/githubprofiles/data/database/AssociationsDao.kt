package com.rhs.githubprofiles.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rhs.githubprofiles.data.model.Associations
import com.rhs.githubprofiles.data.model.UserResponse

/**
 * The Data Access Object for the Associations table.
 */
@Dao
interface AssociationsDao {

    /**
     * Get the associations for a given username.
     *
     * @param username the username
     * @return the associations
     */
    @Query("SELECT * FROM associations_table WHERE login = :username")
    fun getAssociations(username: String): Associations?

    /**
     * Get the followers for a given username.
     *
     * @param associations the associations
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAssociations(associations: Associations)

    /**
     * Get the followers for a given username.
     *
     * @param username the username
     * @param followersList the followers list
     * @param followingList the following list
     */
    fun insertAssociations(username: String, followersList: List<UserResponse>, followingList: List<UserResponse>) {
        val associations = Associations(
            login = username,
            followersList = followersList.map { it.login },
            followingList = followingList.map { it.login }
        )
        insertAssociations(associations)
    }
}