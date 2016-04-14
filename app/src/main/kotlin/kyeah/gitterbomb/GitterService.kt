package kyeah.gitterbomb

import com.amatkivskiy.gitter.sdk.rx.client.RxGitterApiClient

/**
 * Created by kyeh on 4/13/16.
 */

object GitterService {
    var client: RxGitterApiClient? = null

    fun buildClient(access_token: String) {
        client = RxGitterApiClient.Builder()
                .withAccountToken(access_token)
                .build();
    }
}