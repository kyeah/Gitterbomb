package kyeah.gitterbomb.views.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amatkivskiy.gitter.sdk.model.response.message.MessageResponse
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.row_message.view.*
import kyeah.gitterbomb.R
import kyeah.gitterbomb.consume
import kyeah.gitterbomb.dateToTime
import kyeah.gitterbomb.stringToDate
import java.util.*

/**
 * Created by kyeh on 4/16/16.
 */

class MessageAdapter(val recyclerView: RecyclerView, var messageList: ArrayList<MessageResponse>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    enum class  ViewType {
        Progress,
        Item
    }

    interface OnLoadMoreListener {
        fun onLoadMore(size: Int)
    }

    private val visibleThreshold = 5
    private var loading =  false
    private var finishedLoading = false
    private var _onLoadMoreListener: OnLoadMoreListener? = null
    var onLoadMoreListener: OnLoadMoreListener?
        get() = _onLoadMoreListener
        set(value) {
            _onLoadMoreListener = value
        }

    init {
        val llm = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy);

                val lastVisibleItem = llm.findFirstVisibleItemPosition() - visibleThreshold
                if (!loading && !finishedLoading && lastVisibleItem == 0) {
                    loading = true
                    notifyItemInserted(0)
                    onLoadMoreListener?.onLoadMore(itemCount - 1)
                }
            }
        });
    }

    fun onLoadFinished(size: Int) {
        loading = false
        if (size == itemCount) {
            finishedLoading = true
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        val inflater = LayoutInflater.from(parent.context)
        val view = if (viewType == ViewType.Progress.ordinal) {
            inflater.inflate(R.layout.row_progress, parent, false)
        } else {
            inflater.inflate(R.layout.row_message, parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val pos = if (loading) position - 1 else position
            holder.bind(messageList[pos], messageList.elementAtOrNull(pos-1))
        } else if (holder is ProgressViewHolder){
            holder.setIndeterminate(true)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (loading && position == 0) {
            return ViewType.Progress.ordinal
        } else {
            return ViewType.Item.ordinal
        }
    }

    override fun getItemCount(): Int {
        if (loading) {
            return messageList.count() + 1
        } else {
            return messageList.count()
        }
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