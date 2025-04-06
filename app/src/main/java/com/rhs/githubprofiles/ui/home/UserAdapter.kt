package com.rhs.githubprofiles.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rhs.githubprofiles.R
import com.rhs.githubprofiles.data.model.UserResponse
import com.rhs.githubprofiles.databinding.ItemGithubUserBinding
import com.rhs.githubprofiles.ui.GitHubViewModel
import com.rhs.githubprofiles.ui.associations.AssociationsActivity

/**
 * Adapter for displaying GitHub users in a RecyclerView.
 *
 * @param gitHubViewModel ViewModel used to fetch full user data when needed
 */
class UserAdapter(
    private val gitHubViewModel: GitHubViewModel
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // Callback for item clicks
    var onClickListener: ((UserResponse) -> Unit)? = null

    // Callback to notify when list becomes empty or non-empty
    var onListEmpty: ((Boolean) -> Unit)? = null

    // Internal user list
    private val userResponses = mutableListOf<UserResponse>()

    /**
     * ViewHolder class to hold the view bindings
     */
    inner class UserViewHolder(
        val binding: ItemGithubUserBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemGithubUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int = userResponses.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userResponses[position]
        val context = holder.binding.root.context

        // If full user data is available (i.e., name is not null), load directly
        if (user.name != null) {
            loadUserData(holder, user)
        } else {
            // Otherwise, fetch complete user profile from API via ViewModel
            gitHubViewModel.getUser(context, user.login) { userData ->
                // Update the local list with the fetched user data
                val updatedUser = userData ?: user
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    userResponses[holder.adapterPosition] = updatedUser
                    loadUserData(holder, updatedUser)
                }
            }
        }

        // Handle followers click
        holder.binding.tvFollowers.setOnClickListener {
            val intent = AssociationsActivity.newIntent(
                context,
                user,
                AssociationsActivity.FOLLOWERS
            )
            context.startActivity(intent)
        }

        // Handle following click
        holder.binding.tvFollowing.setOnClickListener {
            val intent = AssociationsActivity.newIntent(
                context,
                user,
                AssociationsActivity.FOLLOWING
            )
            context.startActivity(intent)
        }

        // Handle item click
        holder.binding.mcvRoot.setOnClickListener {
            onClickListener?.invoke(userResponses[holder.adapterPosition])
        }
    }

    /**
     * Loads user data into the ViewHolder UI components.
     *
     * @param holder the ViewHolder to bind data to
     * @param user the UserResponse object containing user data
     */
    private fun loadUserData(holder: UserViewHolder, user: UserResponse) {
        val context = holder.binding.root.context

        holder.binding.apply {
            tvUserName.text = user.login
            tvName.text = user.name ?: user.login
            tvBio.text = user.bio ?: context.getString(R.string.no_bio)
            tvFollowers.text = context.getString(R.string.followers_count, user.followers)
            tvFollowing.text = context.getString(R.string.following_count, user.following)

            Glide.with(context)
                .load(user.avatarUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(imageAvatar)
        }
    }

    /**
     * Adds a list of users to the existing list and updates the RecyclerView.
     *
     * @param newUsers the new list of users to add
     */
    fun addUsers(newUsers: List<UserResponse>) {
        val start = userResponses.size
        userResponses.addAll(newUsers)
        onListEmpty?.invoke(userResponses.isEmpty())
        notifyItemRangeInserted(start, newUsers.size)
    }

    /**
     * Replaces the current list of users with a new one.
     *
     * @param newUsers the new list of users
     */
    fun setUsers(newUsers: List<UserResponse>) {
        userResponses.clear()
        userResponses.addAll(newUsers)
        onListEmpty?.invoke(userResponses.isEmpty())
        notifyDataSetChanged()
    }

    /**
     * Clears the user list and notifies the RecyclerView.
     */
    fun clear() {
        userResponses.clear()
        onListEmpty?.invoke(true)
        notifyDataSetChanged()
    }
}
