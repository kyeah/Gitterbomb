package kyeah.gitterbomb.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.amatkivskiy.gitter.sdk.model.request.ChatMessagesRequestParams.ChatMessagesRequestParamsBuilder
import com.amatkivskiy.gitter.sdk.model.response.message.MessageResponse
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kyeah.gitterbomb.R
import kyeah.gitterbomb.logger
import kyeah.gitterbomb.network.GitterService
import kyeah.gitterbomb.views.adapters.MessageAdapter
import rx.android.schedulers.AndroidSchedulers
import java.util.*

/**
 * Created by kyeh on 4/16/16.
 */

class ChatFragment() : Fragment() {
    val log = logger<ChatFragment>()

    var roomName: String? = null
    var roomId: String? = null
    var messageAdapter: MessageAdapter? = null
    var messages: ArrayList<MessageResponse> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        roomId = arguments.getString("roomId") ?: return null
        roomName = arguments.getString("roomName")
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        activity.title = roomName
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val llm = LinearLayoutManager(view.context)
        llm.orientation = LinearLayoutManager.VERTICAL
        llm.stackFromEnd = true
        list.layoutManager = llm

        messageAdapter = MessageAdapter(list, messages)
        messageAdapter?.onLoadMoreListener = object: MessageAdapter.OnLoadMoreListener {
            override fun onLoadMore(size: Int) {
                val params = ChatMessagesRequestParamsBuilder().beforeId(messages[0].id).build()
                GitterService.client.getRoomMessages(roomId, params)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            messages.addAll(0, it)
                            messageAdapter?.onLoadFinished(size)
                        })
            }
        }

        list.itemAnimator = FadeInAnimator()
        list.itemAnimator.addDuration = 200
        val animAdapter = AlphaInAnimationAdapter(messageAdapter)
        animAdapter.setDuration(200)
        list.adapter = animAdapter

        GitterService.client.getRoomMessages(roomId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    messages.clear()
                    messages.addAll(it)
                    messageAdapter?.notifyDataSetChanged()
                    list.scrollToPosition(it.size - 1)

                    GitterService.streamingClient.getRoomMessagesStream(roomId)
                            .retry()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                Log.e("GOT ROOM MESSAGE", it.toString())
                                messages.add(it)
                                messageAdapter?.notifyItemInserted(messages.size - 1)
                            }, {
                                Log.e("FAILED", "ya", it)
                            }, {
                                Log.e("COM", "PLETE???")
                            })
                })

        view.edit_message.hint = "Message #$roomName"
        view.edit_message.setOnEditorActionListener({ textView, i, keyEvent ->
            val res = (i == EditorInfo.IME_ACTION_DONE)
            if (res) {
                val msg = edit_message.text.toString()
                GitterService.client.sendMessage(roomId, msg)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            log.info("Sent message '$msg' to '$roomId'")
                            edit_message.text.clear()
                        }, {
                            Toast.makeText(edit_message.context, "Failed to send message to '$roomName'", Toast.LENGTH_LONG).show()
                        })
            }
            res
        })
    }
}