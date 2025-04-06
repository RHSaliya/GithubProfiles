package com.rhs.githubprofiles.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rhs.githubprofiles.R
import com.rhs.githubprofiles.databinding.ActivityHomeBinding
import com.rhs.githubprofiles.ui.GitHubViewModel
import com.rhs.githubprofiles.ui.profile.ProfileActivity

/**
 * Main screen for searching GitHub users.
 */
class HomeActivity : AppCompatActivity(R.layout.activity_home) {

    private val binding by viewBinding(ActivityHomeBinding::bind)
    private val gitHubViewModel: GitHubViewModel by viewModels()

    private val userAdapter by lazy { UserAdapter(gitHubViewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupSearch()
        setupSwipeRefresh()
    }

    /**
     * Initializes RecyclerView with adapter and click handlers.
     */
    private fun setupRecyclerView() {
        binding.rvUsers.adapter = userAdapter

        userAdapter.onClickListener = { user ->
            val intent = ProfileActivity.newIntent(this, user)
            startActivity(intent)
        }

        userAdapter.onListEmpty = { isEmpty ->
            binding.rvUsers.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.tvNoUsers.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    /**
     * Sets up pull-to-refresh functionality.
     */
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            val query = binding.etSearch.text.toString()
            gitHubViewModel.searchUsers(applicationContext, query)
        }
    }

    /**
     * Handles search button click and observes data from ViewModel.
     */
    private fun setupSearch() {
        binding.mbSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            if (query.isNotBlank()) {
                gitHubViewModel.searchUsers(applicationContext, query)
            } else {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.tvCount.text = ""
                binding.tvNoUsers.visibility = View.VISIBLE
                userAdapter.clear()
            }
        }

        gitHubViewModel.searchResults.observe(this) { users ->
            binding.swipeRefreshLayout.isRefreshing = false
            users?.let { userAdapter.setUsers(it) }
        }

        gitHubViewModel.userCount.observe(this) { count ->
            binding.swipeRefreshLayout.isRefreshing = false
            binding.tvCount.text = if (count > 0) {
                getString(R.string.found_user_count, count)
            } else {
                ""
            }
        }
    }
}
