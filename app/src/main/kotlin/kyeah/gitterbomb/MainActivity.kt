package kyeah.gitterbomb

import android.os.Bundle
import android.support.design.widget.NavigationView
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    data class MenuResult(val menu: SubMenu, val menuId: Int)

    var user: UserResponse? = null;
    var rooms: HashMap<String, RoomResponse> = HashMap();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)
        toggle.syncState()
        nav.setNavigationItemSelectedListener(this)

        val menuStarred = nav.menu.findItem(R.id.starred).subMenu
        val menuChannels = nav.menu.findItem(R.id.channels).subMenu
        val menuOrgs = nav.menu.findItem(R.id.orgs).subMenu
        val menuRepos = nav.menu.findItem(R.id.repos).subMenu
        val menuDirect = nav.menu.findItem(R.id.direct_chat).subMenu

        GitterService.client.currentUser.subscribe({
            user = it
            username.text = user?.displayName
            Glide.with(this).load(user?.avatarUrlSmall).into(userImage)
        })

        GitterService.client.currentUserRooms.subscribe({
            for (i in IntRange(0, it.size - 1)) {
                rooms.put(it[i].name, it[i])
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
        })
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
        R.id.action_settings -> consume {}
        else -> super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment = when (item.itemId) {
           R.id.explore -> ExploreFragment()
           else -> {
               val room = rooms[item.title] ?: return false

               val bundle = Bundle()
               val chatFragment = ChatFragment()
               bundle.putString("roomId", room.id)
               chatFragment.arguments = bundle
               chatFragment
           }
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_main, fragment)
                .commit()

        return true
    }
}
