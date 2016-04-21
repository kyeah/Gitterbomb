package kyeah.gitterbomb

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amatkivskiy.gitter.sdk.model.response.room.RoomResponse
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.*

/**
 * Created by kyeh on 4/20/16.
 */

class ExploreFragment : Fragment() {
    val log = logger<ExploreFragment>()

    var roomAdapter: RoomAdapter? = null
    var rooms: ArrayList<RoomResponse> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        val gridLayout = GridLayoutManager(activity, 2)
        roomAdapter = RoomAdapter(rooms)
        list.layoutManager = gridLayout
        list.adapter = roomAdapter

        GitterService.client.suggestedRooms.subscribe({
            rooms.addAll(it)
            roomAdapter?.notifyDataSetChanged()
        })

        return view
    }
}