package kyeah.gitterbomb

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amatkivskiy.gitter.sdk.model.response.message.MessageResponse
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.*

/**
 * Created by kyeh on 4/16/16.
 */

class ChatFragment : Fragment() {
    var roomId: String? = null
    var messageAdapter: MessageAdapter? = null
    var messages: ArrayList<MessageResponse> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        roomId = arguments.getString("roomId") ?: return null
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        messageAdapter = MessageAdapter(messages)
        list.adapter = messageAdapter

        GitterService.client.getRoomMessages(roomId).subscribe({
            messages.clear()
            messages.addAll(it)
            messageAdapter?.notifyDataSetChanged()

            GitterService.streamingClient.getRoomMessagesStream(roomId).subscribe({
                messages.add(it)
                messageAdapter?.notifyDataSetChanged()
            })
        })

        return view
    }
}