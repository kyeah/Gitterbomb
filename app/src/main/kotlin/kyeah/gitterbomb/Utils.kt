package kyeah.gitterbomb

import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger

/**
 * Created by kyeh on 2/15/16.
 */
object Utils {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val timeFormat = SimpleDateFormat("hh:mm aa")

    init {
        dateFormat.timeZone = TimeZone.getTimeZone("UTC");
    }
}

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

inline fun stringToDate(s: String): Date? {
    return Utils.dateFormat.parse(s)
}

inline fun dateToTime(date: Date?): String {
    return if (date != null) {
        Utils.timeFormat.format(date)
    } else {
        ""
    }
}

inline fun <reified T:Any> logger() = Logger.getLogger(T::class.java.toString())