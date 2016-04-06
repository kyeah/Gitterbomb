package kyeah.gitterbomb

import android.support.v4.widget.DrawerLayout

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