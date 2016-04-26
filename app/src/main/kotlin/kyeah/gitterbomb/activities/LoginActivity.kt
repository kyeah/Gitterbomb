package kyeah.gitterbomb.activities

import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import com.amatkivskiy.gitter.sdk.GitterOauthUtils
import com.amatkivskiy.gitter.sdk.credentials.GitterDeveloperCredentials
import com.amatkivskiy.gitter.sdk.credentials.SimpleGitterCredentialsProvider
import com.amatkivskiy.gitter.sdk.rx.client.RxGitterAuthenticationClient
import kyeah.gitterbomb.R
import kyeah.gitterbomb.R.string.*
import kyeah.gitterbomb.logger
import kyeah.gitterbomb.network.GitterService
import kyeah.gitterbomb.network.UserPreferences

class LoginActivity : AppCompatActivity() {
    companion object {
        val log = logger<LoginActivity>();
        val REQUEST_ACCESS_CODE = 0;
        val RESULT_CANCELLED = 0;
        val RESULT_OK = 1;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val token = UserPreferences.getAccessToken(this)
        if (token != null) {
            continueToMain(token)
        } else {
            loginToGitter()
        }
    }

    private fun loginToGitter() {
        GitterDeveloperCredentials.init(SimpleGitterCredentialsProvider(
                getString(oauth_key), getString(oauth_secret), getString(uri_login_redirect)));

        val gitterAccessUrl = GitterOauthUtils.buildOauthUrl()
        intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("url", gitterAccessUrl)
        startActivityForResult(intent, REQUEST_ACCESS_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        val access_code = intent.data?.getQueryParameter("code")
        if (access_code != null) {
            completeLogin(access_code)
        } else {
            log.severe("Failed to receive Gitter access code")
        }
    }

    private fun completeLogin(@NonNull access_code: String) {
        val authenticationClient = RxGitterAuthenticationClient.Builder().build();
        authenticationClient.getAccessToken(access_code).subscribe({
            UserPreferences.setAccessToken(this, it.accessToken)
            continueToMain(it.accessToken)
        }, {
            log.severe(it.message)
        })
    }

    private fun continueToMain(accessToken: String) {
        GitterService.buildClient(accessToken)
        intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent)
    }
}
