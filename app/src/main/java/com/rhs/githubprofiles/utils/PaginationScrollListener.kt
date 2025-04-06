package com.rhs.githubprofiles.utils

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class PaginationScrollListener(private var layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    private val BUFFER = 5
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading && !isLastPage) {
            val userReachedBottom = (visibleItemCount + firstVisibleItemPosition + BUFFER) >= totalItemCount
            if (firstVisibleItemPosition >= 0 && userReachedBottom && currentPage < totalPageCount) {
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()

    abstract val totalPageCount: Int

    abstract val currentPage: Int

    abstract val isLastPage: Boolean

    abstract val isLoading: Boolean
}