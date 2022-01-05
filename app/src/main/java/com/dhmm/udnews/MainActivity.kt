package com.dhmm.udnews

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.dhmm.udnews.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private val url = "https://udnews.org"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Get the web view settings instance
        val settings = activityMainBinding.webView.settings

        // Enable java script in web view
        settings.javaScriptEnabled = true

        activityMainBinding.webView.loadUrl(url)

        // Enable and setup web view cache
        settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setAppCachePath(cacheDir.path)


        // Enable zooming in web view
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false

        // Zoom web view text
        settings.textZoom = 100

        // Enable disable images in web view
        settings.blockNetworkImage = false
        // Whether the WebView should load image resources
        settings.loadsImagesAutomatically = true


        // More web view settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.safeBrowsingEnabled = true  // api 26
        }
        //settings.pluginState = WebSettings.PluginState.ON
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.mediaPlaybackRequiresUserGesture = false


        // More optional settings, you can enable it by yourself
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.loadWithOverviewMode = true
        settings.allowContentAccess = true
        settings.setGeolocationEnabled(true)
        settings.allowUniversalAccessFromFileURLs = true
        settings.allowFileAccess = true

        // WebView settings
        activityMainBinding.webView.apply {
            fitsSystemWindows = true
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, progress: Int) {
                    activityMainBinding.progressBar.progress = progress
//                    if (progress == 100) activity.setTitle(R.string.app_name)
                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url?.contains("udnews.org") == true) {
                        url.let { view?.loadUrl(it) }
                    } else {
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(i)
                    }
                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    activityMainBinding.progressBar.visibility = View.VISIBLE
                }

                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
//                    activityMainBinding.progressBar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (activityMainBinding.progressBar.isVisible)
                        activityMainBinding.progressBar.visibility = View.GONE
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                }
            }
            url?.let { loadUrl(it) }
        }

        fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.contains("udnews.org")) {
                view.loadUrl(url)
            } else {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(i)
            }
            return true
        }
    }


    // Method to show app exit dialog
    private fun showAppExitDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Please confirm")
        builder.setMessage("Do you want to exit the app?")
        builder.setCancelable(true)

        builder.setPositiveButton("Yes") { _, _ ->

            super@MainActivity.onBackPressed()
        }

        builder.setNegativeButton("No") { _, _ ->
            // Do something when want to stay in the app
//            toast("thank you.")
        }

        // Create the alert dialog using alert dialog builder
        val dialog = builder.create()

        // Finally, display the dialog when user press back button
        dialog.show()

    }

    override fun onBackPressed() {
        if (activityMainBinding.webView.copyBackForwardList().currentIndex > 0) {
            activityMainBinding.webView.goBack()
        } else {
            // show exit alert dialog
            showAppExitDialog()
        }
    }
}