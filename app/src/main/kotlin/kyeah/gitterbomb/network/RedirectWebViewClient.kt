package kyeah.gitterbomb.network

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import kyeah.gitterbomb.R
import kyeah.gitterbomb.activities.LoginActivity

/**
 * Created by kyeh on 4/23/16.
 */

class RedirectWebViewClient(val activity: Activity): WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (url.startsWith(activity.getString(R.string.uri_login_redirect))) {
            val intent = Intent()
            intent.data = Uri.parse(url)
            activity.setResult(LoginActivity.RESULT_OK, intent)
            activity.finish()
        }

        return false
    }
}