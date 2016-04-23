package kyeah.gitterbomb

import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import java.util.logging.Logger

/**
 * Created by kyeh on 2/15/16.
 */

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

inline fun DrawerLayout.consume(f: () -> Unit): Boolean {
    f()
    closeDrawers()
    return true
}

inline fun DrawerLayout.toggle() {
    if (isDrawerOpen(GravityCompat.START)) {
        closeDrawer(GravityCompat.START)
    } else {
        openDrawer(GravityCompat.START)
    }
}

inline fun <reified T:Any> logger() = Logger.getLogger(T::class.java.toString())