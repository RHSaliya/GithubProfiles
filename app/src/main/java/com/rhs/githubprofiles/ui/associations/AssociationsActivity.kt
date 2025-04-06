package com.rhs.githubprofiles.ui.associations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rhs.githubprofiles.R
import com.rhs.githubprofiles.data.model.UserResponse
import com.rhs.githubprofiles.databinding.ActivityAssociationsBinding
import com.rhs.githubprofiles.ui.GitHubViewModel
import com.rhs.githubprofiles.ui.home.UserAdapter
import com.rhs.githubprofiles.ui.profile.ProfileActivity

/**
 * Displays followers or following list for a GitHub user.
 */
class AssociationsActivity : AppCompatActivity(R.layout.activity_associations) {

    private val binding by viewBinding(ActivityAssociationsBinding::bind)
    private val gitHubViewModel: GitHubViewModel by viewModels()

    private val followersAdapter by lazy { UserAdapter(gitHubViewModel) }
    private val followingAdapter by lazy { UserAdapter(gitHubViewModel) }

    private var userInfo: UserResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBackButton()

        userInfo = IntentCompat.getSerializableExtra(intent, EXTRA_USER, UserResponse::class.java) ?: return

        setupToggleButtons()
        fetchUserAssociations()
        setupRecyclerViews()

        observeUserClickEvents()
    }

    /**
     * Assigns adapters to recycler views.
     */
    private fun setupRecyclerViews() {
        binding.rvFollowers.adapter = followersAdapter
        binding.rvFollowing.adapter = followingAdapter
    }

    /**
     * Sets listeners for toggle group to switch between followers and following.
     */
    private fun setupToggleButtons() {
        binding.mbtToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.mbFollowers -> showFollowers()
                    R.id.mbFollowing -> showFollowing()
                }
            }
        }
    }

    /**
     * Handles back button click.
     */
    private fun setupBackButton() {
        binding.mbBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Sets click listener for user items to open their profile.
     */
    private fun observeUserClickEvents() {
        val onUserClick: (user: UserResponse) -> Unit = { user ->
            startActivity(ProfileActivity.newIntent(this, user))
        }
        followersAdapter.onClickListener = onUserClick
        followingAdapter.onClickListener = onUserClick
    }

    /**
     * Fetches followers and following data and observes updates.
     */
    private fun fetchUserAssociations() {
        gitHubViewModel.followers.observe(this) { followers ->
            followersAdapter.setUsers(followers)
            updateCount()
        }

        gitHubViewModel.following.observe(this) { following ->
            followingAdapter.setUsers(following)
            updateCount()
        }

        gitHubViewModel.fetchAssociations(applicationContext, userInfo?.login ?: "")

        // Set initial toggle state based on intent extra
        updateToggleSelection()
    }

    /**
     * Updates the toggle button selection and count text based on intent.
     */
    private fun updateToggleSelection() {
        when (intent.getStringExtra(EXTRA_TYPE)) {
            FOLLOWERS -> {
                binding.mbtToggleGroup.check(R.id.mbFollowers)
                showFollowers()
            }
            FOLLOWING -> {
                binding.mbtToggleGroup.check(R.id.mbFollowing)
                showFollowing()
            }
        }
    }

    /**
     * Shows the followers list.
     */
    private fun showFollowers() {
        binding.rvFollowers.visibility = View.VISIBLE
        binding.rvFollowing.visibility = View.GONE
        binding.tvCount.text = getString(R.string.total_followers_count, userInfo?.followers ?: 0)
    }

    /**
     * Shows the following list.
     */
    private fun showFollowing() {
        binding.rvFollowers.visibility = View.GONE
        binding.rvFollowing.visibility = View.VISIBLE
        binding.tvCount.text = getString(R.string.total_following_count, userInfo?.following ?: 0)
    }

    /**
     * Updates the count text based on the currently visible list.
     */
    private fun updateCount() {
        when (binding.mbtToggleGroup.checkedButtonId) {
            R.id.mbFollowers -> binding.tvCount.text =
                getString(R.string.total_followers_count, followersAdapter.itemCount)

            R.id.mbFollowing -> binding.tvCount.text =
                getString(R.string.total_following_count, followingAdapter.itemCount)
        }
    }

    companion object {
        private const val EXTRA_USER = "user"
        private const val EXTRA_TYPE = "clicked_association"

        const val FOLLOWERS = "followers"
        const val FOLLOWING = "following"

        /**
         * Creates an Intent to launch AssociationsActivity.
         */
        fun newIntent(context: Context?, userResponse: UserResponse, type: String): Intent {
            return Intent(context, AssociationsActivity::class.java).apply {
                putExtra(EXTRA_USER, userResponse)
                putExtra(EXTRA_TYPE, type)
            }
        }
    }
}
