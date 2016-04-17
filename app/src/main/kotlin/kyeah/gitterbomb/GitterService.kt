package kyeah.gitterbomb

import com.amatkivskiy.gitter.sdk.rx.client.RxGitterApiClient
import com.amatkivskiy.gitter.sdk.rx.client.RxGitterStreamingApiClient

/**
 * Created by kyeh on 4/13/16.
 */

object GitterService {
    var client: RxGitterApiClient = null!!
    var streamingClient: RxGitterStreamingApiClient = null!!

    fun buildClient(access_token: String) {
        client = RxGitterApiClient.Builder()
                .withAccountToken(access_token)
                .build();

        streamingClient = RxGitterStreamingApiClient.Builder()
                .withAccountToken(access_token)
                .build();
    }
}