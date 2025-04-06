package com.rhs.githubprofiles.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rhs.githubprofiles.R
import com.rhs.githubprofiles.databinding.ActivityHomeBinding
import com.rhs.githubprofiles.ui.GitHubViewModel
import com.rhs.githubprofiles.ui.profile.ProfileActivity
import com.rhs.githubprofiles.utils.PaginationScrollListener

/**
 * Main screen for searching GitHub users.
 */
class HomeActivity : AppCompatActivity(R.layout.activity_home) {

    private val binding by viewBinding(ActivityHomeBinding::bind)
    private val gitHubViewModel: GitHubViewModel by viewModels()

    private val userAdapter by lazy { UserAdapter(gitHubViewModel) }

    private var pageCount = 0
    private var itemsPerPage = 10
    private var currentPage = 0
    private var isLoading = false

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

        // Set up the toolbar
        binding.etSearch.setText("RHSaliya")
        binding.mbSearch.performClick()
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

        binding.rvUsers.addOnScrollListener(object :
            PaginationScrollListener(binding.rvUsers.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                this@HomeActivity.isLoading = true
                // Load more items here
                val query = binding.etSearch.text.toString()
                gitHubViewModel.searchUsers(
                    applicationContext,
                    query,
                    itemsPerPage,
                    isNextPage = true
                )
            }

            override val totalPageCount: Int
                get() = pageCount
            override val currentPage: Int
                get() = this@HomeActivity.currentPage
            override val isLastPage: Boolean
                get() = currentPage == pageCount
            override val isLoading: Boolean
                get() = this@HomeActivity.isLoading
        })
    }

    /**
     * Sets up pull-to-refresh functionality.
     */
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            val query = binding.etSearch.text.toString()
            gitHubViewModel.searchUsers(applicationContext, query, itemsPerPage)
        }
    }

    /**
     * Handles search button click and observes data from ViewModel.
     */
    private fun setupSearch() {
        binding.mbSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            if (query.isNotBlank()) {
                gitHubViewModel.searchUsers(applicationContext, query, itemsPerPage)
            } else {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.tvCount.text = ""
                binding.tvNoUsers.visibility = View.VISIBLE
                userAdapter.clear()
            }
        }

        gitHubViewModel.searchResults.observe(this) { users ->
            binding.swipeRefreshLayout.isRefreshing = false
            currentPage = ((users?.size ?: 0) / itemsPerPage) + 1
            if (isLoading) {
                users?.let { userAdapter.addUsers(it) }
            } else {
                users?.let { userAdapter.setUsers(it) }
            }
            binding.tvLoadedCount.text = getString(R.string.loaded_user_count, userAdapter.itemCount)
            isLoading = false
        }

        gitHubViewModel.userCount.observe(this) { count ->
            binding.swipeRefreshLayout.isRefreshing = false
            pageCount = count / itemsPerPage + 1
            binding.tvCount.text = if (count > 0) {
                getString(R.string.found_user_count, count)
            } else {
                ""
            }
        }
    }
}
