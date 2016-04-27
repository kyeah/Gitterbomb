package kyeah.gitterbomb.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.amatkivskiy.gitter.sdk.model.request.ChatMessagesRequestParams.ChatMessagesRequestParamsBuilder
import com.amatkivskiy.gitter.sdk.model.response.message.MessageResponse
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kyeah.gitterbomb.R
import kyeah.gitterbomb.logger
import kyeah.gitterbomb.network.GitterService
import kyeah.gitterbomb.views.adapters.MessageAdapter
import java.util.*

/**
 * Created by kyeh on 4/16/16.
 */

class ChatFragment() : Fragment() {
    val log = logger<ChatFragment>()

    var roomId: String? = null
    var messageAdapter: MessageAdapter? = null
    var messages: ArrayList<MessageResponse> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        roomId = arguments.getString("roomId") ?: return null
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val llm = LinearLayoutManager(view.context)
        llm.orientation = LinearLayoutManager.VERTICAL
        list.layoutManager = llm

        messageAdapter = MessageAdapter(list, messages)
        messageAdapter?.onLoadMoreListener = object: MessageAdapter.OnLoadMoreListener {
            override fun onLoadMore(size: Int) {
                val params = ChatMessagesRequestParamsBuilder().beforeId(messages[0].id).build()
                GitterService.client.getRoomMessages(roomId, params).subscribe({
                    messages.addAll(0, it)
                    messageAdapter?.onLoadFinished(size)
                })
            }
        }
        list.adapter = messageAdapter

        GitterService.client.getRoomMessages(roomId).subscribe({
            messages.clear()
            messages.addAll(it)
            messageAdapter?.notifyDataSetChanged()
            list.scrollToPosition(it.size - 1);

            GitterService.streamingClient.getRoomMessagesStream(roomId).subscribe({
                messages.add(it)
                messageAdapter?.notifyDataSetChanged()
            })
        })

        view.edit_message.setOnEditorActionListener({ textView, i, keyEvent ->
            val res = (i == EditorInfo.IME_ACTION_DONE)
            if (res) {
                val msg = edit_message.text.toString()
                GitterService.client.sendMessage(roomId, msg).subscribe({
                    log.info("Sent message '$msg' to '$roomId'")
                }, {
                    log.severe("Failed to send message '$msg' to '$roomId'")
                })
            }
            res
        })
    }
}