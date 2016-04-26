package kyeah.gitterbomb.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*
import kyeah.gitterbomb.R
import kyeah.gitterbomb.network.RedirectWebViewClient

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val url = intent.getStringExtra("url")!!
        web_view.setWebViewClient(RedirectWebViewClient(this))
        web_view.loadUrl(url)
    }
}
