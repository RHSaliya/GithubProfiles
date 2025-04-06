package com.rhs.githubprofiles.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rhs.githubprofiles.data.model.QueriesModel
import com.rhs.githubprofiles.data.model.UserResponse

/**
 * The Data Access Object for the Queries table.
 */
@Dao
interface QueriesDao {

    /**
     * Get the queries for a given username.
     *
     * @param query the query
     * @return the queries
     */
    @Query("SELECT * FROM queries_table WHERE `query` = :query")
    suspend fun getQuery(query: String): QueriesModel?

    /**
     * Get the queries for a given username.
     *
     * @param profile the profile
     * @return the queries
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(profile: QueriesModel)
}