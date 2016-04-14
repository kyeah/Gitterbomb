package kyeah.gitterbomb

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.amatkivskiy.gitter.sdk.GitterOauthUtils
import com.amatkivskiy.gitter.sdk.credentials.GitterDeveloperCredentials
import com.amatkivskiy.gitter.sdk.credentials.SimpleGitterCredentialsProvider
import com.amatkivskiy.gitter.sdk.rx.client.RxGitterAuthenticationClient
import kotlinx.android.synthetic.main.activity_login.*
import kyeah.gitterbomb.R.string.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    companion object { val log = logger<LoginActivity>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_button.setOnClickListener(this)

        val access_code = intent.data?.getQueryParameter("code")
        if (access_code != null) {
            completeLogin(access_code)
        }
    }

    override fun onClick(v: View?) {
        GitterDeveloperCredentials.init(SimpleGitterCredentialsProvider(
                getString(oauth_key), getString(oauth_secret), getString(uri_login_redirect)));

        val gitterAccessUrl = GitterOauthUtils.buildOauthUrl()
        intent = Intent(Intent.ACTION_VIEW, Uri.parse(gitterAccessUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent)
    }

    private fun completeLogin(@NonNull access_code: String) {
        val authenticationClient = RxGitterAuthenticationClient.Builder().build();
        authenticationClient.getAccessToken(access_code).subscribe({
            GitterService.buildClient(it.accessToken)
            intent = Intent(this, MainActivity::class.java)
            super.startActivity(intent)
        }, {
            log.severe(it.message)
        })
    }
}
