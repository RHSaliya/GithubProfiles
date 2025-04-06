package com.rhs.githubprofiles.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.rhs.githubprofiles.R
import com.rhs.githubprofiles.data.model.UserResponse
import com.rhs.githubprofiles.databinding.ActivityProfileBinding
import com.rhs.githubprofiles.ui.associations.AssociationsActivity

/**
 * Activity that shows a GitHub user's profile.
 * Displays avatar, name, bio, location, and association counts (followers/following).
 * Allows navigation to:
 * - Followers/Following list
 * - Web view of profile
 */
class ProfileActivity : AppCompatActivity(R.layout.activity_profile) {

    private val binding by viewBinding(ActivityProfileBinding::bind)

    private var userInfo: UserResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Handle edge-to-edge display (status/navigation bar insets)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve user object from Intent
        userInfo = IntentCompat.getSerializableExtra(intent, EXTRA_USER, UserResponse::class.java)

        // Exit if user info is null
        if (userInfo == null) {
            finish()
            return
        }

        setupUI()
        loadUserInfo()
    }

    /**
     * Sets up click listeners and toolbar buttons.
     */
    private fun setupUI() = with(binding) {
        mbBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        mbWeb.setOnClickListener {
            userInfo?.profileUrl?.let { profileUrl ->
                startActivity(ProfileWebActivity.newIntent(this@ProfileActivity, profileUrl))
            }
        }

        tvFollowers.setOnClickListener {
            userInfo?.let {
                startActivity(
                    AssociationsActivity.newIntent(
                        this@ProfileActivity,
                        it,
                        AssociationsActivity.FOLLOWERS
                    )
                )
            }
        }

        tvFollowing.setOnClickListener {
            userInfo?.let {
                startActivity(
                    AssociationsActivity.newIntent(
                        this@ProfileActivity,
                        it,
                        AssociationsActivity.FOLLOWING
                    )
                )
            }
        }
    }

    /**
     * Loads user info into the UI.
     */
    private fun loadUserInfo() = with(binding) {
        userInfo?.let { user ->
            tvUserName.text = user.login
            tvName.text = user.name ?: user.login
            tvBio.text = user.bio ?: getString(R.string.no_bio)
            tvAddress.text = user.location
            tvFollowers.text = getString(R.string.followers_count, user.followers)
            tvFollowing.text = getString(R.string.following_count, user.following)

            Glide.with(applicationContext)
                .load(user.avatarUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(imageAvatar)
        }
    }

    companion object {
        private const val EXTRA_USER = "user"

        /**
         * Launches [ProfileActivity] with the provided [UserResponse].
         *
         * @param activity The calling [Activity].
         * @param userResponse The user to show in the profile.
         */
        fun newIntent(activity: Activity, userResponse: UserResponse): Intent {
            return Intent(activity, ProfileActivity::class.java).apply {
                putExtra(EXTRA_USER, userResponse)
            }
        }
    }
}
