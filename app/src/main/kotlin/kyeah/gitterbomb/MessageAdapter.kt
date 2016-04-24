package kyeah.gitterbomb

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amatkivskiy.gitter.sdk.model.response.message.MessageResponse
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
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
        holder.bind(messageList[position], messageList.elementAtOrNull(position-1))
    }

    override fun getItemCount(): Int {
        return messageList.count()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val icon = view.icon
        val name = view.name
        val timestamp = view.timestamp
        val text = view.text

        fun bind(messageResponse: MessageResponse, prev: MessageResponse?) {

            val date = stringToDate(messageResponse.sent)
            var hide = false

            // Check date difference, avoiding unnecessary computation
            if (prev != null && messageResponse.fromUser.id.equals(prev.fromUser.id)) {
                val prevDate = stringToDate(prev.sent)
                if (date != null && prevDate != null) {
                    val diff = date.time - prevDate.time
                    if (diff < 60000) {
                        hide = true
                    }
                }
            }

            val margin = (view.resources.getDimension(R.dimen.row_message_margin) / view.resources.displayMetrics.density).toInt()
            Log.e("TAG", ""+margin)
            if (hide) {
                icon.visibility = View.GONE
                name.visibility = View.GONE
                timestamp.visibility = View.GONE
                (view.layoutParams as RecyclerView.LayoutParams).setMargins(margin, 0, margin, 0)
            } else {
                icon.visibility = View.VISIBLE
                name.visibility = View.VISIBLE
                timestamp.visibility = View.VISIBLE
                (view.layoutParams as RecyclerView.LayoutParams).setMargins(margin, margin*2, margin, 0)

                Glide.with(icon.context)
                        .load(messageResponse.fromUser.avatarUrlSmall)
                        .bitmapTransform(RoundedCornersTransformation(icon.context, 4, 4))
                        .into(icon)

                name.text = messageResponse.fromUser.displayName
                timestamp.text = dateToTime(date)
            }

            text.text = messageResponse.text

            view.setOnLongClickListener {
                val menu = PopupMenu(view.context, view)
                menu.menuInflater.inflate(R.menu.message, menu.menu)

                menu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.copy_text -> consume {
                            val clipboard = view.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(text.text, text.text)
                            clipboard.primaryClip = clip
                        }
                        R.id.edit -> consume {}
                        else -> false
                    }
                }

                menu.show()
                true
            }
        }
    }
}