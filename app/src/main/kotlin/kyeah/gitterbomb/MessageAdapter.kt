package kyeah.gitterbomb

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amatkivskiy.gitter.sdk.model.response.message.MessageResponse
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_message.view.*

/**
 * Created by kyeh on 4/16/16.
 */

class MessageAdapter(val messageList: List<MessageResponse>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int {
        return messageList.count()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon = view.icon
        val name = view.name
        val timestamp = view.timestamp
        val text = view.text

        fun bind(messageResponse: MessageResponse) {
            Glide.with(icon.context).load(messageResponse.fromUser.avatarUrlSmall).into(icon)
            name.text = messageResponse.fromUser.displayName
            timestamp.text = messageResponse.sent
            text.text = messageResponse.text
        }
    }
}