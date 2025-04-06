package com.rhs.githubprofiles.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhs.githubprofiles.data.database.AppDatabase
import com.rhs.githubprofiles.data.model.QueriesModel
import com.rhs.githubprofiles.data.model.UserResponse
import com.rhs.githubprofiles.data.networking.GitHubRepository
import com.rhs.githubprofiles.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * This ViewModel is responsible for managing the data for the GitHub profiles.
 * It interacts with the repository to fetch user data and handles caching.
 *
 * @property repo the GitHubRepository instance
 */
class GitHubViewModel : ViewModel() {

    private val repo = GitHubRepository()
    private val CACHE_TIMEOUT = 60 * 60 * 1000 // 1 hour

    val userResponse = MutableLiveData<UserResponse?>()
    val followers = MutableLiveData<List<UserResponse>>()
    val following = MutableLiveData<List<UserResponse>>()
    val searchResults = MutableLiveData<List<UserResponse>?>()
    val userCount = MutableLiveData<Int>()

    private var currentPage = 1

    /**
     * Fetches the user profile for a given username.
     *
     * @param context the context
     * @param username the username
     * @param forceNetwork whether to force a network call
     * @param onLoad callback to be invoked after loading the user
     */
    fun getUser(
        context: Context,
        username: String,
        forceNetwork: Boolean = false,
        onLoad: (UserResponse?) -> Unit
    ) {
        viewModelScope.launch {
            val db = AppDatabase.getInstance(context)

            val cachedUser = withContext(Dispatchers.IO) {
                db.userDao()?.getProfile(username)
            }

            val shouldUseCache = cachedUser != null &&
                    System.currentTimeMillis() - cachedUser.lastUpdated < CACHE_TIMEOUT &&
                    !forceNetwork

            if (!NetworkUtil.isConnected(context) || shouldUseCache) {
                userResponse.postValue(cachedUser)
                return@launch
            }

            val response = repo.getUser(username)
            val user = response.body()

            if (response.isSuccessful && user != null) {
                withContext(Dispatchers.IO) {
                    db.userDao()?.insertProfile(user)
                }
                userResponse.postValue(user)
                onLoad(user)
            } else {
                userResponse.postValue(null)
                onLoad(null)
            }
        }
    }

    /**
     * Fetches the followers and following lists for a given username.
     *
     * @param context the context
     * @param username the username
     * @param forceNetwork whether to force a network call
     */
    fun fetchAssociations(context: Context, username: String, forceNetwork: Boolean = false) {
        viewModelScope.launch {
            val db = AppDatabase.getInstance(context)
            val followersList = mutableListOf<UserResponse>()
            val followingList = mutableListOf<UserResponse>()

            withContext(Dispatchers.IO) {
                val associations = db.associationsDao()?.getAssociations(username)
                val isCacheValid = associations != null &&
                        System.currentTimeMillis() - associations.lastUpdated < CACHE_TIMEOUT

                if (!NetworkUtil.isConnected(context) || (!forceNetwork && isCacheValid)) {
                    associations?.followersList?.mapNotNullTo(followersList) {
                        db.userDao()?.getProfile(it)
                    }

                    associations?.followingList?.mapNotNullTo(followingList) {
                        db.userDao()?.getProfile(it)
                    }

                    withContext(Dispatchers.Main) {
                        followers.postValue(followersList)
                        following.postValue(followingList)
                    }
                    return@withContext
                }

                val followersResponse = repo.getFollowers(username)
                val followingResponse = repo.getFollowing(username)

                if (followersResponse.isSuccessful) {
                    val fetchedFollowers = followersResponse.body().orEmpty()
                    followersList.addAll(fetchedFollowers)
                    fetchedFollowers.forEach { db.userDao()?.insertProfile(it) }
                }

                if (followingResponse.isSuccessful) {
                    val fetchedFollowing = followingResponse.body().orEmpty()
                    followingList.addAll(fetchedFollowing)
                    fetchedFollowing.forEach { db.userDao()?.insertProfile(it) }
                }

                // Do not store anything if both lists are empty
                if (followersList.size != 0 || followingList.size != 0)
                    db.associationsDao()?.insertAssociations(username, followersList, followingList)
            }

            // UI updates
            followers.postValue(followersList)
            following.postValue(followingList)
        }
    }

    /**
     * Searches for users based on a query.
     *
     * @param context the context
     * @param query the search query
     * @param isNextPage whether to load the next page of results
     * @param forceNetwork whether to force a network call
     */
    fun searchUsers(
        context: Context,
        query: String,
        isNextPage: Boolean = false,
        forceNetwork: Boolean = false
    ) {
        viewModelScope.launch {
            val db = AppDatabase.getInstance(context)

            val (cachedUsers, cachedQuery) = withContext(Dispatchers.IO) {
                val users = db.userDao()?.searchProfile("$query%")
                val queryMeta = db.queriesDao()?.getQuery(query)
                Pair(users, queryMeta)
            }

            val isCacheValid = cachedQuery != null &&
                    System.currentTimeMillis() - cachedQuery.lastUpdated < CACHE_TIMEOUT &&
                    !forceNetwork

            if (!NetworkUtil.isConnected(context) || (isCacheValid && !cachedUsers.isNullOrEmpty())) {
                searchResults.postValue(cachedUsers)
                userCount.postValue(cachedQuery?.totalCount)
                return@launch
            }

            val response = repo.searchUsers(query, currentPage)
            if (response.isSuccessful) {
                val users = response.body()?.users.orEmpty()
                val total = response.body()?.totalCount ?: 0

                withContext(Dispatchers.IO) {
                    db.queriesDao()?.insertQuery(
                        QueriesModel(query = query, totalCount = total)
                    )
                    users.forEach { db.userDao()?.insertProfile(it) }
                }

                userCount.postValue(total)

                val updatedList = if (isNextPage) {
                    searchResults.value.orEmpty() + users
                } else {
                    users
                }

                searchResults.postValue(updatedList)
                currentPage++
            } else {
                searchResults.postValue(null)
            }
        }
    }
}
