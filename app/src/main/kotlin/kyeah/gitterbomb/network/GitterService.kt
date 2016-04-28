package kyeah.gitterbomb.network

import com.amatkivskiy.gitter.sdk.rx.client.RxGitterApiClient
import com.amatkivskiy.gitter.sdk.rx.client.RxGitterStreamingApiClient
import com.squareup.okhttp.OkHttpClient
import retrofit.client.OkClient
import java.util.concurrent.TimeUnit

/**
 * Created by kyeh on 4/13/16.
 */

object GitterService {
    private var _client: RxGitterApiClient? = null
    var client: RxGitterApiClient
        get() = _client!!
        set(value) {
            _client = value
        }

    private var _streamingClient: RxGitterStreamingApiClient? = null
    var streamingClient: RxGitterStreamingApiClient
        get() = _streamingClient!!
        set(value) {
            _streamingClient = value
        }

    fun buildClient(access_token: String) {
        client = RxGitterApiClient.Builder()
                .withAccountToken(access_token)
                .build()

        val okClient = OkHttpClient()
        okClient.setReadTimeout(10, TimeUnit.MINUTES)

        streamingClient = RxGitterStreamingApiClient.Builder()
                .withAccountToken(access_token)
                .withClient(OkClient(okClient))
                .build()
    }
}