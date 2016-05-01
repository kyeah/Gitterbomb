package kyeah.gitterbomb.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amatkivskiy.gitter.sdk.model.response.room.RoomResponse
import kotlinx.android.synthetic.main.fragment_explore.*
import kyeah.gitterbomb.R
import kyeah.gitterbomb.logger
import kyeah.gitterbomb.network.GitterService
import kyeah.gitterbomb.views.adapters.RoomAdapter
import rx.android.schedulers.AndroidSchedulers
import java.util.*

/**
 * Created by kyeh on 4/20/16.
 */

class ExploreFragment : Fragment() {
    val log = logger<ExploreFragment>()

    var roomAdapter: RoomAdapter? = null
    var rooms: ArrayList<RoomResponse> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity.title = "Explore Rooms"
        val view = inflater.inflate(R.layout.fragment_explore, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val gridLayout = GridLayoutManager(activity, 2)
        roomAdapter = RoomAdapter(activity as AppCompatActivity, rooms)
        list.layoutManager = gridLayout
        list.adapter = roomAdapter

        GitterService.client.suggestedRooms
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    rooms.addAll(it)
                    roomAdapter?.notifyDataSetChanged()
                })
    }
}