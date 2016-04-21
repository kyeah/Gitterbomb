package kyeah.gitterbomb

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amatkivskiy.gitter.sdk.model.response.room.RoomResponse
import kotlinx.android.synthetic.main.row_message.view.*

/**
 * Created by kyeh on 4/16/16.
 */

class RoomAdapter(val activity: AppCompatActivity, val roomList: List<RoomResponse>) : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.grid_room, parent, false)
        return ViewHolder(activity, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(roomList[position])
    }

    override fun getItemCount(): Int {
        return roomList.count()
    }

    class ViewHolder(val activity: AppCompatActivity, val view: View) : RecyclerView.ViewHolder(view) {
        val icon = view.icon
        val name = view.name

        fun bind(room: RoomResponse) {
            name.text = room.name

            view.setOnClickListener {
                val bundle = Bundle()
                val chatFragment = ChatFragment()
                bundle.putString("roomId", room.id)
                chatFragment.arguments = bundle

                activity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.content_main, chatFragment)
                        .commit()
            }

            view.setOnLongClickListener {
                val menu = PopupMenu(view.context, view)
                menu.menuInflater.inflate(R.menu.message, menu.menu)

                menu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.copy_text -> consume {
                            val clipboard = view.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(name.text, name.text)
                            clipboard.primaryClip = clip
                        }
                        R.id.edit -> consume {}
                        else -> false
                    }
                }

                true
            }
        }
    }
}