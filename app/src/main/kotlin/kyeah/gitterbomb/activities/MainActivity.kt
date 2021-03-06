package kyeah.gitterbomb.activities

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import com.amatkivskiy.gitter.sdk.model.response.UserResponse
import com.amatkivskiy.gitter.sdk.model.response.room.RoomResponse
import com.amatkivskiy.gitter.sdk.model.response.room.RoomType
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding.support.design.widget.RxNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kyeah.gitterbomb.R
import kyeah.gitterbomb.consume
import kyeah.gitterbomb.fragments.ChatFragment
import kyeah.gitterbomb.fragments.ExploreFragment
import kyeah.gitterbomb.network.GitterService
import kyeah.gitterbomb.toggle
import rx.android.schedulers.AndroidSchedulers
import java.util.*

class MainActivity : AppCompatActivity() {

    data class MenuResult(val menu: SubMenu, val menuId: Int)

    var user: UserResponse? = null
    var rooms: HashMap<String, RoomResponse> = HashMap()
    var prevItem: MenuItem? = null
    private var toggle: ActionBarDrawerToggle? = null

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle?.syncState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        toggle = ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle!!)
        RxNavigationView.itemSelections(nav)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onNavigationItemSelected(it) })

        val menuStarred = nav.menu.findItem(R.id.starred).subMenu
        val menuChannels = nav.menu.findItem(R.id.channels).subMenu
        val menuOrgs = nav.menu.findItem(R.id.orgs).subMenu
        val menuRepos = nav.menu.findItem(R.id.repos).subMenu
        val menuDirect = nav.menu.findItem(R.id.direct_chat).subMenu

        GitterService.client.currentUser
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    user = it
                    username.text = user?.displayName
                    Glide.with(this).load(user?.avatarUrlSmall).into(userImage)
                })

        GitterService.client.currentUserRooms
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    for (i in IntRange(0, it.size - 1)) {
                        rooms.put(it[i].name, it[i])
                        if (it[i].favourite == 1) {
                            menuStarred.add(R.id.starred, Menu.NONE, Menu.NONE, it[i].name)
                        } else {
                            val result = when (it[i].githubRoomType) {
                                RoomType.ONETOONE -> MenuResult(menuDirect, R.id.group_direct_chat)
                                RoomType.ORG, RoomType.ORG_CHANNEL -> MenuResult(menuOrgs, R.id.group_orgs)
                                RoomType.REPO, RoomType.REPO_CHANNEL -> MenuResult(menuRepos, R.id.group_repos)
                                RoomType.USER_CHANNEL -> MenuResult(menuChannels, R.id.group_channels)
                                else -> null
                    }
                            if (result != null) {
                                result.menu.add(result.menuId, Menu.NONE, Menu.NONE, it[i].name)
                            }
                        }
                    }
                })

        //prevItem = nav.menu.findItem(R.id.explore)
        //prevItem?.isChecked = true
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu) = consume {
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> consume { drawer.toggle() }
        R.id.action_settings -> consume {}
        else -> super.onOptionsItemSelected(item)
    }

    fun onNavigationItemSelected(item: MenuItem): Boolean {
        prevItem?.isChecked = false
        item.isCheckable = true
        item.isChecked = true
        prevItem = item

        val fragment = when (item.itemId) {
            R.id.explore -> {
                ExploreFragment()
            }
            else -> {
                val room = rooms[item.title] ?: return false
                val bundle = Bundle()
                val chatFragment = ChatFragment()
                bundle.putString("roomName", room.name)
                bundle.putString("roomId", room.id)
                chatFragment.arguments = bundle
                chatFragment
            }
        }

        return drawer.consume {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit()
        }
    }
}
