package com.rhs.githubprofiles.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rhs.githubprofiles.R
import com.rhs.githubprofiles.databinding.ActivityProfileBinding
import com.rhs.githubprofiles.databinding.ActivityProfileWebBinding

class ProfileWebActivity : AppCompatActivity(R.layout.activity_profile_web) {

    private val binding by viewBinding(ActivityProfileWebBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the profile URL passed via Intent
        val profileUrl = intent.getStringExtra(EXTRA_PROFILE_URL)

        setupWebView()

        // Load profile URL if available
        profileUrl?.let { binding.webView.loadUrl(it) }
    }

    /**
     * Sets up the WebView with necessary settings and clients.
     */
    private fun setupWebView() {
        binding.webView.apply {
            // Enable JavaScript and DOM storage for modern web page support
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            // Clear previous session data
            clearCache(false)
            clearHistory()

            // Set WebViewClient to handle navigation within WebView
            webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // Optional: Hide progress bar or perform UI actions here
                }
            }

            webChromeClient = WebChromeClient()
        }
    }

    /**
     * Overrides the back button to navigate back within the WebView history.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
                return true
            } else {
                finish()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        private const val EXTRA_PROFILE_URL = "profile_url"

        /**
         * Creates an intent to start [ProfileActivity] with a given GitHub profile URL.
         */
        fun newIntent(activity: Activity, profileUrl: String): Intent {
            return Intent(activity, ProfileWebActivity::class.java).apply {
                putExtra(EXTRA_PROFILE_URL, profileUrl)
            }
        }
    }
}